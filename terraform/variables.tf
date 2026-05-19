# Variables Terraform pour le projet recrute

variable "aws_region" {
  description = "Région AWS pour déployer les ressources"
  type        = string
  default     = "eu-west-3"  # Paris
}

variable "environment" {
  description = "Environnement de déploiement"
  type        = string
  default     = "production"

  validation {
    condition     = contains(["development", "staging", "production"], var.environment)
    error_message = "Environment doit être development, staging ou production."
  }
}

# Variables EKS
variable "node_desired_capacity" {
  description = "Nombre de noeuds Kubernetes désiré"
  type        = number
  default     = 2
}

variable "node_max_capacity" {
  description = "Nombre maximum de noeuds Kubernetes"
  type        = number
  default     = 5
}

variable "node_min_capacity" {
  description = "Nombre minimum de noeuds Kubernetes"
  type        = number
  default     = 1
}

variable "node_instance_types" {
  description = "Types d'instances EC2 pour les noeuds Kubernetes"
  type        = list(string)
  default     = ["t3.medium", "t3.large"]
}

# Variables RDS
variable "db_instance_class" {
  description = "Classe d'instance RDS"
  type        = string
  default     = "db.t3.micro"
}

variable "db_allocated_storage" {
  description = "Stockage alloué pour RDS (GB)"
  type        = number
  default     = 20
}

variable "db_max_allocated_storage" {
  description = "Stockage maximum pour RDS (GB)"
  type        = number
  default     = 100
}

variable "db_name" {
  description = "Nom de la base de données"
  type        = string
  default     = "recrutedb"
}

variable "db_username" {
  description = "Nom d'utilisateur RDS"
  type        = string
  default     = "recrute_admin"
  sensitive   = true
}

variable "db_backup_retention" {
  description = "Jours de rétention des backups RDS"
  type        = number
  default     = 7
}

# Variables Redis
variable "redis_node_type" {
  description = "Type de noeud ElastiCache Redis"
  type        = string
  default     = "cache.t3.micro"
}

# Variables de tagging
variable "project_name" {
  description = "Nom du projet"
  type        = string
  default     = "recrute"
}

variable "contact_email" {
  description = "Email de contact pour les alertes"
  type        = string
  default     = "admin@recrute.com"
}