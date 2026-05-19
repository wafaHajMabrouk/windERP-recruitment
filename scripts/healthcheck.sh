#!/bin/bash

# ==================== CONFIGURATION ====================
set -euo pipefail

NAMESPACE="recrute"
LOG_FILE="/var/log/recrute-healthcheck.log"
ALERT_THRESHOLD=2

# Couleurs
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Compteurs
TOTAL_SERVICES=0
HEALTHY_SERVICES=0
UNHEALTHY_SERVICES=0

# ==================== FONCTIONS ====================
log_info() { echo -e "${GREEN}[$(date '+%Y-%m-%d %H:%M:%S')] INFO: $1${NC}" | tee -a $LOG_FILE; }
log_error() { echo -e "${RED}[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}" | tee -a $LOG_FILE; }
log_warn() { echo -e "${YELLOW}[$(date '+%Y-%m-%d %H:%M:%S')] WARN: $1${NC}" | tee -a $LOG_FILE; }

print_header() {
    echo ""
    echo "=========================================="
    echo "   RECRUTE - HEALTHCHECK DASHBOARD"
    echo "   $(date '+%Y-%m-%d %H:%M:%S')"
    echo "=========================================="
    echo ""
}

check_kubectl() {
    command -v kubectl >/dev/null 2>&1 || { log_error "kubectl n'est pas installé"; exit 1; }
    kubectl cluster-info >/dev/null 2>&1 || { log_error "Impossible de se connecter au cluster"; exit 1; }
}

check_namespace() {
    if ! kubectl get ns $NAMESPACE >/dev/null 2>&1; then
        log_error "Namespace $NAMESPACE introuvable"
        exit 1
    fi
}

check_pod_health() {
    local pod=$1
    local status=$(kubectl get pod $pod -n $NAMESPACE -o jsonpath='{.status.phase}')
    local ready=$(kubectl get pod $pod -n $NAMESPACE -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}')
    local restarts=$(kubectl get pod $pod -n $NAMESPACE -o jsonpath='{.status.containerStatuses[0].restartCount}')

    TOTAL_SERVICES=$((TOTAL_SERVICES + 1))

    if [ "$status" = "Running" ] && [ "$ready" = "True" ]; then
        if [ $restarts -gt $ALERT_THRESHOLD ]; then
            echo -e "  ${YELLOW}⚠️  $pod: Running (${restarts} restarts)${NC}"
            UNHEALTHY_SERVICES=$((UNHEALTHY_SERVICES + 1))
        else
            echo -e "  ${GREEN}✅ $pod: Running (0 restarts)${NC}"
            HEALTHY_SERVICES=$((HEALTHY_SERVICES + 1))
        fi
        return 0
    else
        echo -e "  ${RED}❌ $pod: $status (Ready: $ready, Restarts: $restarts)${NC}"
        UNHEALTHY_SERVICES=$((UNHEALTHY_SERVICES + 1))
        return 1
    fi
}

check_service_endpoint() {
    local service=$1
    local port=$2

    if kubectl get svc $service -n $NAMESPACE >/dev/null 2>&1; then
        echo -e "  ${GREEN}✅ $service:${NC} port $port"
        return 0
    else
        echo -e "  ${RED}❌ $service:${NC} service introuvable"
        return 1
    fi
}

check_deployment_status() {
    local deployment=$1
    local available=$(kubectl get deployment $deployment -n $NAMESPACE -o jsonpath='{.status.availableReplicas}')
    local desired=$(kubectl get deployment $deployment -n $NAMESPACE -o jsonpath='{.spec.replicas}')

    if [ "$available" = "$desired" ]; then
        echo -e "  ${GREEN}✅ $deployment:${NC} $available/$desired replicas"
        return 0
    else
        echo -e "  ${RED}❌ $deployment:${NC} $available/$desired replicas disponibles"
        return 1
    fi
}

check_eureka_registrations() {
    echo -e "\n${BLUE}📋 Services enregistrés dans Eureka:${NC}"

    EUREKA_POD=$(kubectl get pods -n $NAMESPACE -l app=eureka-server -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)

    if [ -n "$EUREKA_POD" ]; then
        kubectl exec -n $NAMESPACE $EUREKA_POD -- curl -s http://localhost:8761/eureka/apps 2>/dev/null | \
            grep -o '<name>[^<]*</name>' | sed 's/<name>//g;s/<\/name>//g' | while read service; do
            echo "  - $service"
        done || echo "  Aucun service enregistré"
    else
        echo "  Pod Eureka non trouvé"
    fi
}

check_metrics() {
    echo -e "\n${BLUE}📊 Métriques système:${NC}"

    # CPU et Mémoire des pods
    kubectl top pods -n $NAMESPACE 2>/dev/null | while read line; do
        echo "  $line"
    done || echo "  metrics-server non installé"
}

check_database_connection() {
    echo -e "\n${BLUE}🗄️  Base de données:${NC}"

    PG_POD=$(kubectl get pods -n $NAMESPACE -l app=postgres -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)

    if [ -n "$PG_POD" ]; then
        if kubectl exec -n $NAMESPACE $PG_POD -- pg_isready -U postgres >/dev/null 2>&1; then
            echo "  ${GREEN}✅ PostgreSQL: UP${NC}"
        else
            echo "  ${RED}❌ PostgreSQL: DOWN${NC}"
        fi
    else
        echo "  ${YELLOW}⚠️  PostgreSQL: non installé${NC}"
    fi
}

check_redis_connection() {
    echo -e "\n${BLUE}📦 Cache Redis:${NC}"

    REDIS_POD=$(kubectl get pods -n $NAMESPACE -l app=redis -o jsonpath='{.items[0].metadata.name}' 2>/dev/null)

    if [ -n "$REDIS_POD" ]; then
        if kubectl exec -n $NAMESPACE $REDIS_POD -- redis-cli PING >/dev/null 2>&1; then
            echo "  ${GREEN}✅ Redis: UP${NC}"
        else
            echo "  ${RED}❌ Redis: DOWN${NC}"
        fi
    else
        echo "  ${YELLOW}⚠️  Redis: non installé${NC}"
    fi
}

check_endpoints_http() {
    echo -e "\n${BLUE}🌐 Endpoints HTTP:${NC}"

    # Port-forward API Gateway pour tester
    kubectl port-forward -n $NAMESPACE svc/api-gateway 8080:80 >/dev/null 2>&1 &
    PF_PID=$!
    sleep 2

    ENDPOINTS=(
        "/actuator/health"
        "/actuator/info"
        "/actuator/metrics"
    )

    for endpoint in "${ENDPOINTS[@]}"; do
        if curl -s -f http://localhost:8080$endpoint >/dev/null 2>&1; then
            echo "  ${GREEN}✅ $endpoint${NC}"
        else
            echo "  ${RED}❌ $endpoint${NC}"
        fi
    done

    kill $PF_PID 2>/dev/null
}

print_summary() {
    echo ""
    echo "=========================================="
    echo "              RÉSUMÉ"
    echo "=========================================="
    echo -e "Total services: ${BLUE}$TOTAL_SERVICES${NC}"
    echo -e "Services sains: ${GREEN}$HEALTHY_SERVICES${NC}"
    echo -e "Services malsains: ${RED}$UNHEALTHY_SERVICES${NC}"
    echo "=========================================="

    if [ $UNHEALTHY_SERVICES -eq 0 ]; then
        echo -e "${GREEN}🎉 Tous les services sont sains!${NC}"
        return 0
    else
        echo -e "${YELLOW}⚠️  $UNHEALTHY_SERVICES service(s) nécessite(nt) une attention${NC}"
        return 1
    fi
}

# ==================== EXÉCUTION PRINCIPALE ====================
main() {
    print_header

    check_kubectl
    check_namespace

    # Services à vérifier
    SERVICES=(
        "api-gateway"
        "authentication"
        "candidate-service"
        "interview-service"
        "eureka-server"
        "dashboard-service"
        "notificationservice"
    )

    # Vérification des pods
    echo -e "${BLUE}🔍 Vérification des pods:${NC}"
    for service in "${SERVICES[@]}"; do
        pods=$(kubectl get pods -n $NAMESPACE -l app=$service -o jsonpath='{.items[*].metadata.name}' 2>/dev/null)
        if [ -n "$pods" ]; then
            for pod in $pods; do
                check_pod_health $pod
            done
        else
            echo -e "  ${YELLOW}⚠️  $service: aucun pod trouvé${NC}"
            UNHEALTHY_SERVICES=$((UNHEALTHY_SERVICES + 1))
        fi
    done

    # Vérification des déploiements
    echo -e "\n${BLUE}📦 Vérification des déploiements:${NC}"
    for service in "${SERVICES[@]}"; do
        check_deployment_status $service || true
    done

    # Vérification des services
    echo -e "\n${BLUE}🔌 Vérification des services:${NC}"
    for service in "${SERVICES[@]}"; do
        check_service_endpoint $service 8080 || true
    done

    check_eureka_registrations
    check_metrics
    check_database_connection
    check_redis_connection
    check_endpoints_http

    print_summary
    exit $?
}

# Exécution
main "$@"