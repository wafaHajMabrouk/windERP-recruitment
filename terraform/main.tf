terraform {
  required_version = ">= 1.0.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.23"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.5"
    }
  }

  backend "s3" {
    # Configuration du backend S3 pour stocker l'état Terraform
    # bucket = "recrute-terraform-state"
    # key    = "terraform.tfstate"
    # region = "eu-west-3"
  }
}

# Provider AWS
provider "aws" {
  region = var.aws_region
  default_tags {
    tags = {
      Project     = "recrute"
      Environment = var.environment
      ManagedBy   = "Terraform"
    }
  }
}

# Provider Kubernetes (configuré après création du cluster)
provider "kubernetes" {
  host                   = module.eks.cluster_endpoint
  cluster_ca_certificate = base64decode(module.eks.cluster_certificate_authority_data)
  token                  = data.aws_eks_cluster_auth.cluster.token
}

data "aws_eks_cluster_auth" "cluster" {
  name = module.eks.cluster_name
}

# ==================== VPC ====================
resource "aws_vpc" "main" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Name = "recrute-vpc"
  }
}

# Subnets publics
resource "aws_subnet" "public" {
  count                   = 2
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.${count.index + 1}.0/24"
  availability_zone       = data.aws_availability_zones.available.names[count.index]
  map_public_ip_on_launch = true

  tags = {
    Name = "recrute-public-subnet-${count.index + 1}"
    "kubernetes.io/role/elb" = "1"
  }
}

# Subnets privés
resource "aws_subnet" "private" {
  count             = 2
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.${count.index + 10}.0/24"
  availability_zone = data.aws_availability_zones.available.names[count.index]

  tags = {
    Name = "recrute-private-subnet-${count.index + 1}"
    "kubernetes.io/role/internal-elb" = "1"
  }
}

# Internet Gateway
resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "recrute-igw"
  }
}

# Route tables
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }

  tags = {
    Name = "recrute-public-rt"
  }
}

resource "aws_route_table_association" "public" {
  count          = length(aws_subnet.public)
  subnet_id      = aws_subnet.public[count.index].id
  route_table_id = aws_route_table.public.id
}

# Security groups
resource "aws_security_group" "eks_cluster" {
  name        = "recrute-eks-cluster-sg"
  description = "Security group for EKS cluster"
  vpc_id      = aws_vpc.main.id

  tags = {
    Name = "recrute-eks-cluster-sg"
  }
}

resource "aws_security_group" "eks_nodes" {
  name        = "recrute-eks-nodes-sg"
  description = "Security group for EKS nodes"
  vpc_id      = aws_vpc.main.id

  tags = {
    Name = "recrute-eks-nodes-sg"
  }
}

# ==================== EKS CLUSTER ====================
module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "19.0"

  cluster_name    = "recrute-cluster"
  cluster_version = "1.28"

  cluster_endpoint_public_access  = true
  cluster_endpoint_private_access = true

  cluster_addons = {
    coredns = {
      most_recent = true
    }
    kube-proxy = {
      most_recent = true
    }
    vpc-cni = {
      most_recent = true
    }
  }

  vpc_id     = aws_vpc.main.id
  subnet_ids = aws_subnet.private[*].id

  node_groups = {
    main = {
      desired_capacity = var.node_desired_capacity
      max_capacity     = var.node_max_capacity
      min_capacity     = var.node_min_capacity

      instance_types = var.node_instance_types

      subnet_ids = aws_subnet.private[*].id

      kubernetes_labels = {
        Environment = var.environment
        NodeGroup   = "main"
      }

      tags = {
        "k8s.io/cluster-autoscaler/enabled" = "true"
        "k8s.io/cluster-autoscaler/recrute-cluster" = "owned"
      }
    }
  }

  tags = {
    Environment = var.environment
    Project     = "recrute"
  }
}

# ==================== RDS POSTGRESQL ====================
resource "aws_db_subnet_group" "recrute" {
  name        = "recrute-db-subnet-group"
  description = "Subnet group for Recrute RDS"
  subnet_ids  = aws_subnet.private[*].id

  tags = {
    Name = "recrute-db-subnet-group"
  }
}

resource "aws_security_group" "rds" {
  name        = "recrute-rds-sg"
  description = "Security group for RDS"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.eks_nodes.id]
  }

  tags = {
    Name = "recrute-rds-sg"
  }
}

resource "aws_db_instance" "recrute" {
  identifier = "recrute-db"

  engine         = "postgres"
  engine_version = "15.3"

  instance_class = var.db_instance_class

  allocated_storage     = var.db_allocated_storage
  max_allocated_storage = var.db_max_allocated_storage

  db_name  = var.db_name
  username = var.db_username
  password = random_password.db_password.result

  db_subnet_group_name   = aws_db_subnet_group.recrute.name
  vpc_security_group_ids = [aws_security_group.rds.id]

  backup_retention_period = var.db_backup_retention
  backup_window          = "03:00-04:00"
  maintenance_window     = "sun:04:00-sun:05:00"

  storage_encrypted = true
  storage_type      = "gp3"

  skip_final_snapshot = var.environment == "production" ? false : true
  final_snapshot_identifier = var.environment == "production" ? "recrute-db-final-snapshot-${formatdate("YYYY-MM-DD-hhmm", timestamp())}" : null

  enabled_cloudwatch_logs_exports = ["postgresql"]

  performance_insights_enabled          = true
  performance_insights_retention_period = 7

  tags = {
    Name = "recrute-postgres"
  }
}

# Mot de passe RDS aléatoire
resource "random_password" "db_password" {
  length  = 16
  special = false
  numeric = true
  upper   = true
  lower   = true
}

# ==================== ELASTICACHE REDIS ====================
resource "aws_security_group" "redis" {
  name        = "recrute-redis-sg"
  description = "Security group for Redis"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port       = 6379
    to_port         = 6379
    protocol        = "tcp"
    security_groups = [aws_security_group.eks_nodes.id]
  }

  tags = {
    Name = "recrute-redis-sg"
  }
}

resource "aws_elasticache_subnet_group" "recrute" {
  name        = "recrute-redis-subnet-group"
  description = "Subnet group for Redis"
  subnet_ids  = aws_subnet.private[*].id
}

resource "aws_elasticache_cluster" "recrute" {
  cluster_id           = "recrute-redis"
  engine              = "redis"
  engine_version      = "7.0"
  node_type           = var.redis_node_type
  num_cache_nodes     = 1
  parameter_group_name = "default.redis7"
  port                = 6379

  subnet_group_name = aws_elasticache_subnet_group.recrute.name
  security_group_ids = [aws_security_group.redis.id]

  tags = {
    Name = "recrute-redis"
  }
}

# ==================== S3 BUCKETS ====================
resource "random_id" "bucket_suffix" {
  byte_length = 4
}

resource "aws_s3_bucket" "backups" {
  bucket = "recrute-backups-${random_id.bucket_suffix.hex}"

  tags = {
    Name = "recrute-backups"
  }
}

resource "aws_s3_bucket_versioning" "backups" {
  bucket = aws_s3_bucket.backups.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "backups" {
  bucket = aws_s3_bucket.backups.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "backups" {
  bucket = aws_s3_bucket.backups.id

  rule {
    id     = "archive-old-backups"
    status = "Enabled"

    transition {
      days          = 30
      storage_class = "GLACIER"
    }

    expiration {
      days = 90
    }
  }
}

# ==================== DATA SOURCES ====================
data "aws_availability_zones" "available" {
  state = "available"
}