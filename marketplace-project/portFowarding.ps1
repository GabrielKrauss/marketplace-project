# Getting pod names
$POD_FRONTEND = kubectl get pods | Select-String "angular-app" | ForEach-Object { ($_ -split "\s+")[0] }
$POD_BACKEND = kubectl get pods | Select-String "marketplace-deployment" | ForEach-Object { ($_ -split "\s+")[0] }
$POD_KEYCLOAK = kubectl get pods | Select-String "keycloak-deployment" | ForEach-Object { ($_ -split "\s+")[0] }
$POD_DATABASE = kubectl get pods | Select-String "postgresql" | ForEach-Object { ($_ -split "\s+")[0] }

# Verify if all pods were found
if (-not $POD_FRONTEND -or -not $POD_BACKEND -or -not $POD_KEYCLOAK -or -not $POD_DATABASE) {
    Write-Host "Erro: Não foi possível encontrar todos os pods necessários!"
    exit 1
}

# Port-forwarding frontend
Write-Host "Port-forwarding frontend ($POD_FRONTEND)..."
Start-Process kubectl -ArgumentList "port-forward pod/$POD_FRONTEND 4200:4200"

# Port-forwarding backend
Write-Host "Port-forwarding backend ($POD_BACKEND)..."
Start-Process kubectl -ArgumentList "port-forward pod/$POD_BACKEND 8080:8080"

# Port-forwarding keycloak
Write-Host "Port-forwarding keycloak ($POD_KEYCLOAK)..."
Start-Process kubectl -ArgumentList "port-forward pod/$POD_KEYCLOAK 8081:8081"

# Port-forwarding database
Write-Host "Port-forwarding database ($POD_DATABASE)..."
Start-Process kubectl -ArgumentList "port-forward pod/$POD_DATABASE 5432:5432"

Write-Host "Todos os port-forwards foram iniciados."