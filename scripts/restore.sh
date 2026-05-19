#!/bin/bash

# ==================== CONFIGURATION ====================
set -euo pipefail

BACKUP_FILE=${1:-}
NAMESPACE="recrute"
RESTORE_DIR="/tmp/recrute-restore"
LOG_FILE="/var/log/recrute-restore.log"

# Couleurs
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# ==================== FONCTIONS ====================
log_info() { echo -e "${GREEN}[$(date '+%Y-%m-%d %H:%M:%S')] INFO: $1${NC}" | tee -a $LOG_FILE; }
log_error() { echo -e "${RED}[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}" | tee -a $LOG_FILE; }

usage() {
    echo "Usage: $0 <backup_file.tar.gz>"
    echo "Exemple: $0 /tmp/recrute-backups/recrute_backup_20241215_020000.tar.gz"
    exit 1
}

check_prerequisites() {
    if [ -z "$BACKUP_FILE" ]; then
        log_error "Aucun fichier de backup spécifié"
        usage
    fi

    if [ ! -f "$BACKUP_FILE" ]; then
        log_error "Fichier de backup introuvable: $BACKUP_FILE"
        exit 1
    fi

    command -v kubectl >/dev/null 2>&1 || { log_error "kubectl n'est pas installé"; exit 1; }
    command -v psql >/dev/null 2>&1 || { log_error "psql n'est pas installé"; exit 1; }
}

extract_backup() {
    log_info "Extraction du backup..."

    rm -rf $RESTORE_DIR
    mkdir -p $RESTORE_DIR
    tar -xzf $BACKUP_FILE -C $RESTORE_DIR

    log_info "✅ Extraction terminée dans $RESTORE_DIR"
}

restore_postgres() {
    log_info "Restauration PostgreSQL..."

    # Trouver le fichier SQL
    SQL_FILE=$(find $RESTORE_DIR -name "*.sql" -type f | head -1)

    if [ -n "$SQL_FILE" ]; then
        PG_POD=$(kubectl get pods -n $NAMESPACE -l app=postgres -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)

        if [ -n "$PG_POD" ]; then
            # Copier le fichier dans le pod
            kubectl cp $SQL_FILE $NAMESPACE/$PG_POD:/tmp/restore.sql
            # Restaurer
            kubectl exec -n $NAMESPACE $PG_POD -- psql -U postgres -f /tmp/restore.sql
            log_info "✅ PostgreSQL restauré"
        else
            log_error "Pod PostgreSQL non trouvé"
        fi
    else
        log_warn "Aucun fichier SQL trouvé"
    fi
}

restore_redis() {
    log_info "Restauration Redis..."

    RDB_FILE=$(find $RESTORE_DIR -name "*.rdb" -type f | head -1)

    if [ -n "$RDB_FILE" ]; then
        REDIS_POD=$(kubectl get pods -n $NAMESPACE -l app=redis -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)

        if [ -n "$REDIS_POD" ]; then
            # Copier le fichier RDB
            kubectl cp $RDB_FILE $NAMESPACE/$REDIS_POD:/data/dump.rdb
            # Redémarrer Redis
            kubectl exec -n $NAMESPACE $REDIS_POD -- redis-cli SHUTDOWN NOSAVE
            sleep 5
            log_info "✅ Redis restauré"
        else
            log_error "Pod Redis non trouvé"
        fi
    else
        log_warn "Aucun fichier RDB trouvé"
    fi
}

restore_kubernetes_resources() {
    log_info "Restauration des ressources Kubernetes..."

    for file in $RESTORE_DIR/*.yaml; do
        if [ -f "$file" ]; then
            kubectl apply -f $file 2>/dev/null || log_warn "Erreur sur $file"
        fi
    done

    log_info "✅ Ressources Kubernetes restaurées"
}

# ==================== EXÉCUTION ====================
main() {
    log_info "=========================================="
    log_info "Démarrage de la restauration"
    log_info "Fichier: $BACKUP_FILE"
    log_info "=========================================="

    check_prerequisites
    extract_backup
    restore_postgres
    restore_redis
    restore_kubernetes_resources

    log_info "=========================================="
    log_info "✅ Restauration terminée!"
    log_info "=========================================="
}

main "$@"