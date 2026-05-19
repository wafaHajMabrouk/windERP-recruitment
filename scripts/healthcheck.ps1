#!/usr/bin/env pwsh
# HEALTHCHECK RECRUTE - Version PowerShell

Write-Host @"
╔══════════════════════════════════════════════════════════════╗
║                    HEALTHCHECK RECRUTE                        ║
║                    $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")                    ║
╚══════════════════════════════════════════════════════════════╝
"@ -ForegroundColor Cyan

$servicesOk = 0
$servicesTotal = 0

# 1. Vérification Docker
Write-Host "`n📦 CONTENEURS DOCKER:" -ForegroundColor Yellow
$containers = docker ps --format "table {{.Names}}\t{{.Status}}" 2>$null
if ($containers) {
    $containers | ForEach-Object { Write-Host "  $_" }
    $servicesTotal++
    $servicesOk++
} else {
    Write-Host "  ❌ Aucun conteneur en cours" -ForegroundColor Red
}

# 2. Vérification Eureka
Write-Host "`n🔍 SERVICE DISCOVERY (Eureka):" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8762/actuator/health" -UseBasicParsing -TimeoutSec 3
    if ($response.StatusCode -eq 200) {
        Write-Host "  ✅ Eureka: UP" -ForegroundColor Green
        $servicesOk++
    }
} catch {
    Write-Host "  ❌ Eureka: DOWN" -ForegroundColor Red
}
$servicesTotal++

# 3. Vérification API Gateway
Write-Host "`n🌐 API GATEWAY:" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8082/actuator/health" -UseBasicParsing -TimeoutSec 3
    if ($response.StatusCode -eq 200) {
        Write-Host "  ✅ API Gateway: UP" -ForegroundColor Green
        $servicesOk++
    }
} catch {
    Write-Host "  ❌ API Gateway: DOWN" -ForegroundColor Red
}
$servicesTotal++

# 4. Vérification Prometheus
Write-Host "`n📊 PROMETHEUS:" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:9090/-/healthy" -UseBasicParsing -TimeoutSec 3
    if ($response.Content -like "*Healthy*") {
        Write-Host "  ✅ Prometheus: UP" -ForegroundColor Green
        $servicesOk++
    }
} catch {
    Write-Host "  ❌ Prometheus: DOWN" -ForegroundColor Red
}
$servicesTotal++

# 5. Vérification Grafana
Write-Host "`n📈 GRAFANA:" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:3000/api/health" -UseBasicParsing -TimeoutSec 3
    if ($response.Content -like "*ok*") {
        Write-Host "  ✅ Grafana: UP (admin/admin)" -ForegroundColor Green
        $servicesOk++
    }
} catch {
    Write-Host "  ❌ Grafana: DOWN" -ForegroundColor Red
}
$servicesTotal++

# 6. Vérification Loki
Write-Host "`n📝 LOKI:" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:3100/ready" -UseBasicParsing -TimeoutSec 3
    if ($response.Content -eq "ready") {
        Write-Host "  ✅ Loki: UP" -ForegroundColor Green
        $servicesOk++
    }
} catch {
    Write-Host "  ❌ Loki: DOWN" -ForegroundColor Red
}
$servicesTotal++

# 7. Vérification des services backend
Write-Host "`n⚙️ SERVICES BACKEND:" -ForegroundColor Yellow
$backends = @(
    @{Name="Candidate"; URL="http://localhost:8089/actuator/health"},
    @{Name="Interview"; URL="http://localhost:8091/actuator/health"},
    @{Name="Dashboard"; URL="http://localhost:8088/actuator/health"},
    @{Name="Notification"; URL="http://localhost:8090/actuator/health"}
)

foreach ($backend in $backends) {
    try {
        $response = Invoke-WebRequest -Uri $backend.URL -UseBasicParsing -TimeoutSec 3
        if ($response.StatusCode -eq 200) {
            Write-Host "  ✅ $($backend.Name): UP" -ForegroundColor Green
            $servicesOk++
        }
    } catch {
        Write-Host "  ⚠️ $($backend.Name): Indisponible" -ForegroundColor Yellow
    }
    $servicesTotal++
}

# Résumé final
Write-Host @"

╔══════════════════════════════════════════════════════════════╗
║                         RÉSUMÉ                                ║
╠══════════════════════════════════════════════════════════════╣
║  Services OK: $servicesOk / $servicesTotal
╚══════════════════════════════════════════════════════════════╝
"@ -ForegroundColor Cyan

if ($servicesOk -eq $servicesTotal) {
    Write-Host "🎉 TOUS LES SERVICES SONT OPÉRATIONNELS !" -ForegroundColor Green
} else {
    Write-Host "⚠️ $($servicesTotal - $servicesOk) service(s) nécessite(nt) une attention" -ForegroundColor Yellow
}
