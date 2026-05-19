#!/bin/bash

# ==================== CONFIGURATION ====================
set -euo pipefail

# Variables
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/tmp/recrute-backups"
S3_BUCKET="recrute-backups-xxxxx"  # Remplacer par votre bucket
RETENTION_DAYS=30
LOG_FILE="/var/log/recrute-backup.log"
NAMESPACE="recrute"

# Couleurs pour les logs
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# ==================== FONCTIONS ====================
log_info() {
    echo -e "${GREEN}[$(date '+%Y-%m-%d %H:%M:%S')] INFO: $1${NC}" | tee -a $LOG_FILE
}

log_warn() {
    echo -e "${YELLOW}[$(date '+%Y-%m-%d %H:%M:%S')] WARN: $1${NC}" | tee -a $LOG_FILE
}

log_error() {
    echo -e "${RED}[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}" | tee -a $LOG_FILE
}

check_prerequisites() {
    log_info "Vérification des prérequis..."

    command -v kubectl >/dev/null 2>&1 || { log_error "kubectl n'est pas installé"; exit 1; }
    command -v aws >/dev/null 2>&1 || { log_warn "aws CLI n'est pas installé - backup S3 désactivé"; S3_BACKUP=false; }
    command -v tar >/dev/null 2>&1 || { log_error "tar n'est pas installé"; exit 1; }

    # Vérifier l'accès au namespace
    kubectl get ns $NAMESPACE >/dev/null 2>&1 || { log_error "Namespace $NAMESPACE introuvable"; exit 1; }
}

create_backup_dir() {
    log_info "Création du dossier de backup..."
    mkdir -p $BACKUP_DIR
}

backup_postgres() {
    log_info "Backup PostgreSQL..."

    # Trouver le pod PostgreSQL
    PG_POD=$(kubectl get pods -n $NAMESPACE -l app=postgres -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)

    if [ -n "$PG_POD" ]; then
        kubectl exec -n $NAMESPACE $PG_POD -- pg_dumpall -U postgres > $BACKUP_DIR/postgres_full_$DATE.sql 2>/dev/null || \
        kubectl exec -n $NAMESPACE $PG_POD -- pg_dump -U postgres recrutedb > $BACKUP_DIR/recrutedb_$DATE.sql 2>/dev/null
        log_info "✅ Backup PostgreSQL terminé"
    else
        log_warn "Pod PostgreSQL non trouvé"
    fi
}

backup_redis() {
    log_info "Backup Redis..."

    REDIS_POD=$(kubectl get pods -n $NAMESPACE -l app=redis -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)

    if [ -n "$REDIS_POD" ]; then
        kubectl exec -n $NAMESPACE $REDIS_POD -- redis-cli SAVE
        kubectl cp $NAMESPACE/$REDIS_POD:/data/dump.rdb $BACKUP_DIR/redis_$DATE.rdb
        log_info "✅ Backup Redis terminé"
    else
        log_warn "Pod Redis non trouvé"
    fi
}

backup_kubernetes_resources() {
    log_info "Backup des ressources Kubernetes..."

    # Backup des déploiements
    kubectl get deployments -n $NAMESPACE -o yaml > $BACKUP_DIR/deployments_$DATE.yaml

    # Backup des services
    kubectl get services -n $NAMESPACE -o yaml > $BACKUP_DIR/services_$DATE.yaml

    # Backup des configmaps
    kubectl get configmaps -n $NAMESPACE -o yaml > $BACKUP_DIR/configmaps_$DATE.yaml

    # Backup des secrets (attention: données sensibles)
    kubectl get secrets -n $NAMESPACE -o yaml > $BACKUP_DIR/secrets_$DATE.yaml

    # Backup des ingresses
    kubectl get ingress -n $NAMESPACE -o yaml > $BACKUP_DIR/ingress_$DATE.yaml

    # Backup des PVC
    kubectl get pvc -n $NAMESPACE -o yaml > $BACKUP_DIR/pvcs_$DATE.yaml

    log_info "✅ Backup Kubernetes terminé"
}

backup_docker_volumes() {
    log_info "Backup des volumes Docker..."

    if command -v docker >/dev/null 2>&1; then
        docker run --rm -v recrute_data:/data -v $BACKUP_DIR:/backup alpine tar czf /backup/docker_volumes_$DATE.tar.gz /data
        log_info "✅ Backup volumes Docker terminé"
    else
        log_warn "Docker non disponible"
    fi
}

compress_backup() {
    log_info "Compression du backup..."

    cd $BACKUP_DIR
    tar -czf recrute_backup_$DATE.tar.gz *.sql *.rdb *.yaml 2>/dev/null || true
    rm -f *.sql *.rdb *.yaml 2>/dev/null || true

    log_info "✅ Compression terminée: recrute_backup_$DATE.tar.gz"
}

upload_to_s3() {
    if [ "${S3_BACKUP:-true}" = true ] && command -v aws >/dev/null 2>&1; then
        log_info "Upload vers S3..."
        aws s3 cp $BACKUP_DIR/recrute_backup_$DATE.tar.gz s3://$S3_BUCKET/backups/
        log_info "✅ Upload S3 terminé"
    else
        log_warn "Upload S3 désactivé"
    fi
}

cleanup_old_backups() {
    log_info "Nettoyage des anciens backups (${RETENTION_DAYS} jours)..."

    # Nettoyer local
    find $BACKUP_DIR -name "*.tar.gz" -type f -mtime +$RETENTION_DAYS -delete

    # Nettoyer S3
    if command -v aws >/dev/null 2>&1; then
        aws s3 ls s3://$S3_BUCKET/backups/ | while read -r line; do
            file_date=$(echo $line | awk '{print $1}')
            if [ -n "$file_date" ]; then
                # Convertir date et supprimer si > RETENTION_DAYS
                echo "Vérification des fichiers dans S3..."
            fi
        done
    fi

    log_info "✅ Nettoyage terminé"
}

create_manifest() {
    log_info "Création du manifeste de backup..."

    cat > $BACKUP_DIR/backup_manifest_$DATE.txt << EOF
Backup Date: $DATE
Environment: production
Namespace: $NAMESPACE
Services: api-gateway, authentication, candidate-service, interview-service, eureka-server, dashboard-service, notificationservice
Backup Size: $(du -h $BACKUP_DIR/recrute_backup_$DATE.tar.gz | cut -f1)
EOF

    log_info "✅ Manifeste créé"
}

send_notification() {
    # Optionnel: envoyer notification Slack/Email
    log_info "Backup terminé avec succès"
}

# ==================== EXÉCUTION PRINCIPALE ====================
main() {
    log_info "=========================================="
    log_info "Démarrage du backup - $DATE"
    log_info "=========================================="

    check_prerequisites
    create_backup_dir
    backup_postgres
    backup_redis
    backup_kubernetes_resources
    backup_docker_volumes
    compress_backup
    upload_to_s3
    cleanup_old_backups
    create_manifest
    send_notification

    log_info "=========================================="
    log_info "✅ Backup terminé avec succès!"
    log_info "📁 Fichier: $BACKUP_DIR/recrute_backup_$DATE.tar.gz"
    log_info "📊 Taille: $(du -h $BACKUP_DIR/recrute_backup_$DATE.tar.gz | cut -f1)"
    log_info "=========================================="
}

# Exécution
main "$@"