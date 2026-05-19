# Outputs Terraform

output "cluster_name" {
  description = "Nom du cluster EKS"
  value       = module.eks.cluster_name
}

output "cluster_endpoint" {
  description = "Endpoint du cluster EKS"
  value       = module.eks.cluster_endpoint
  sensitive   = true
}

output "cluster_security_group_id" {
  description = "ID du security group du cluster"
  value       = module.eks.cluster_security_group_id
}

output "db_endpoint" {
  description = "Endpoint de la base de données RDS"
  value       = aws_db_instance.recrute.endpoint
  sensitive   = true
}

output "db_password" {
  description = "Mot de passe de la base de données (sensitive)"
  value       = random_password.db_password.result
  sensitive   = true
}

output "db_name" {
  description = "Nom de la base de données"
  value       = aws_db_instance.recrute.db_name
}

output "redis_endpoint" {
  description = "Endpoint Redis"
  value       = aws_elasticache_cluster.recrute.cache_nodes[0].address
}

output "backup_bucket" {
  description = "Nom du bucket S3 pour les backups"
  value       = aws_s3_bucket.backups.bucket
}

output "vpc_id" {
  description = "ID du VPC"
  value       = aws_vpc.main.id
}

output "subnet_ids" {
  description = "IDs des subnets privés"
  value       = aws_subnet.private[*].id
}

# Commandes pour configurer kubectl
output "configure_kubectl" {
  description = "Commande pour configurer kubectl"
  value       = "aws eks update-kubeconfig --region ${var.aws_region} --name ${module.eks.cluster_name}"
}

# Informations de connexion
output "grafana_url" {
  description = "URL Grafana"
  value       = "http://localhost:3000"
}

output "prometheus_url" {
  description = "URL Prometheus"
  value       = "http://localhost:9090"
}

output "loki_url" {
  description = "URL Loki"
  value       = "http://localhost:3100"
}