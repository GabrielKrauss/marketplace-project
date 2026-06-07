param()

$ErrorActionPreference = 'Stop'

$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$workspaceRoot = Split-Path -Parent $scriptRoot

$backendImage = 'marketplace-project:latest'
$frontendImage = 'marketplace-app:latest'
$networkName = 'marketplace-network'

$backendDockerfileDir = $scriptRoot
$frontendDockerfileDir = Join-Path $workspaceRoot 'marketplace-app'

$dbManifest = Join-Path $scriptRoot 'db-deployment.yml'
$keycloakManifest = Join-Path $scriptRoot 'kc-deployment.yml'
$backendManifest = Join-Path $scriptRoot 'marketplace-deployment.yml'
$frontendManifest = Join-Path $frontendDockerfileDir 'angular-deployment.yaml'

$localContainers = @(
    @{ Name = 'marketplace-postgres'; Alias = 'postgresql' }
    @{ Name = 'marketplace-keycloak'; Alias = 'keycloak' }
    @{ Name = 'marketplace-backend'; Alias = 'marketplace-backend' }
    @{ Name = 'marketplace-frontend'; Alias = 'marketplace-frontend' }
)

function Test-CommandExists {
    param([string]$CommandName)
    return $null -ne (Get-Command $CommandName -ErrorAction SilentlyContinue)
}

function Invoke-ExternalCommand {
    param(
        [string]$FilePath,
        [string[]]$ArgumentList,
        [string]$Description
    )

    Write-Host "`n> $Description" -ForegroundColor Cyan
    Write-Host ("{0} {1}" -f $FilePath, ($ArgumentList -join ' ')) -ForegroundColor DarkGray

    & $FilePath @ArgumentList
    if ($LASTEXITCODE -ne 0) {
        throw "Falha ao executar: $Description"
    }
}

function Write-Step {
    param([string]$Message)
    Write-Host "`n=== $Message ===" -ForegroundColor Cyan
}

function Get-KubernetesCurrentContext {
    if (-not (Test-CommandExists 'kubectl')) {
        return $null
    }

    $prev = $ErrorActionPreference
    try {
        $ErrorActionPreference = 'Continue'
        $contextOutput = & kubectl config current-context 2>$null
        if ($LASTEXITCODE -ne 0) { return $null }
        $currentContext = ($contextOutput | Out-String).Trim()
        if ([string]::IsNullOrWhiteSpace($currentContext)) { return $null }
        return $currentContext
    }
    finally {
        $ErrorActionPreference = $prev
    }
}

function Get-LocalContainerStatus {
    param([string]$ContainerName)

    try {
        $status = docker inspect --format '{{.State.Status}}' $ContainerName 2>$null
        return $status
    }
    catch {
        return $null
    }
}

function Show-Status {
    Write-Host ''
    Write-Host '=== Status do ambiente ===' -ForegroundColor Magenta

    if (Test-CommandExists 'docker') {
        foreach ($entry in $localContainers) {
            $name = $entry.Name
            $status = Get-LocalContainerStatus -ContainerName $name
            $label = $name -replace '^marketplace-', ''
            if ($status -eq 'running') {
                Write-Host "  [ON]  $label - rodando" -ForegroundColor Green
            } elseif ($status) {
                Write-Host "  [OFF] $label - $status" -ForegroundColor Yellow
            } else {
                Write-Host "  [--]  $label - nao encontrado" -ForegroundColor DarkGray
            }
        }
    }
    else {
        Write-Host 'Docker nao encontrado.' -ForegroundColor Red
    }

    $ctx = Get-KubernetesCurrentContext
    if ($ctx) {
        Write-Host "Kubernetes context: $ctx" -ForegroundColor Cyan
    } else {
        Write-Host 'Kubernetes context: ausente' -ForegroundColor Yellow
    }
    Write-Host ''
}

function Ensure-DockerNetwork {
    if (-not (Test-CommandExists 'docker')) { throw 'Docker e necessario para o modo local.' }
    $existing = docker network ls --format '{{.Name}}' 2>$null
    if (-not ($existing -contains $networkName)) {
        Invoke-ExternalCommand -FilePath 'docker' -ArgumentList @('network', 'create', $networkName) -Description 'Criando rede Docker local'
    }
}

function Stop-DockerContainerIfExists {
    param([string]$ContainerName)
    $exists = docker ps -aq --filter "name=$ContainerName" 2>$null
    if ($exists) {
        Invoke-ExternalCommand -FilePath 'docker' -ArgumentList @('rm', '-f', $ContainerName) -Description "Removendo container $ContainerName"
    }
}

function Start-LocalPostgres {
    Ensure-DockerNetwork
    Stop-DockerContainerIfExists -ContainerName 'marketplace-postgres'
    Invoke-ExternalCommand -FilePath 'docker' -ArgumentList @(
        'run', '-d', '--name', 'marketplace-postgres', '--network', $networkName, '--network-alias', 'postgresql',
        '-p', '5432:5432',
        '-e', 'POSTGRES_DB=mydb', '-e', 'POSTGRES_USER=admin', '-e', 'POSTGRES_PASSWORD=admin',
        '-v', 'marketplace-postgres-data:/var/lib/postgresql/data', 'postgres:14'
    ) -Description 'Iniciando PostgreSQL local'
}

function Start-LocalKeycloak {
    Ensure-DockerNetwork
    Stop-DockerContainerIfExists -ContainerName 'marketplace-keycloak'
    Invoke-ExternalCommand -FilePath 'docker' -ArgumentList @(
        'run', '-d', '--name', 'marketplace-keycloak', '--network', $networkName, '--network-alias', 'keycloak',
        '-p', '8081:8081',
        '-v', 'marketplace-keycloak-data:/opt/keycloak/data',
        '-e', 'KC_DB=postgres', '-e', 'KC_DB_URL=jdbc:postgresql://postgresql:5432/mydb',
        '-e', 'KC_DB_USERNAME=admin', '-e', 'KC_DB_PASSWORD=admin',
        '-e', 'KC_BOOTSTRAP_ADMIN_USERNAME=admin', '-e', 'KC_BOOTSTRAP_ADMIN_PASSWORD=admin',
        '-e', 'KC_HTTP_PORT=8081',
        '-e', 'KC_HOSTNAME=localhost', '-e', 'KC_HOSTNAME_STRICT=false',
        'quay.io/keycloak/keycloak:26.1.0', 'start-dev'
    ) -Description 'Iniciando Keycloak local'
}

function Start-LocalBackend {
    Ensure-DockerNetwork
    Stop-DockerContainerIfExists -ContainerName 'marketplace-backend'
    Invoke-ExternalCommand -FilePath 'docker' -ArgumentList @(
        'run', '-d', '--name', 'marketplace-backend', '--network', $networkName,
        '-p', '8080:8080',
        '-e', 'SPRING_DATASOURCE_URL=jdbc:postgresql://postgresql:5432/mydb',
        '-e', 'SPRING_DATASOURCE_USERNAME=admin',
        '-e', 'SPRING_DATASOURCE_PASSWORD=admin',
        '-e', 'SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=http://localhost:8081/realms/GamerHeaven',
        '-e', 'SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI=http://keycloak:8081/realms/GamerHeaven/protocol/openid-connect/certs',
        '-e', 'KEYCLOAK_URL=http://keycloak:8081',
        '-e', 'KEYCLOAK_REALM=GamerHeaven',
        '-e', 'KEYCLOAK_CLIENT_ID=GamerHeaven-app',
        '-e', 'KEYCLOAK_CLIENT_SECRET=',
        $backendImage
    ) -Description 'Iniciando backend local'
}

function Start-LocalFrontend {
    Ensure-DockerNetwork
    Stop-DockerContainerIfExists -ContainerName 'marketplace-frontend'
    Invoke-ExternalCommand -FilePath 'docker' -ArgumentList @(
        'run', '-d', '--name', 'marketplace-frontend', '--network', $networkName,
        '-p', '4200:4200',
        $frontendImage
    ) -Description 'Iniciando frontend local'
}

function Start-LocalFrontendDev {
    Ensure-DockerNetwork
    Stop-DockerContainerIfExists -ContainerName 'marketplace-frontend'
    Invoke-ExternalCommand -FilePath 'docker' -ArgumentList @(
        'run', '-d', '--name', 'marketplace-frontend',
        '-p', '4200:4200',
        '-v', "${frontendDockerfileDir}/src:/app/src",
        '-v', "${frontendDockerfileDir}/public:/app/public",
        '-v', "${frontendDockerfileDir}/tsconfig.app.json:/app/tsconfig.app.json",
        '-v', "${frontendDockerfileDir}/angular.json:/app/angular.json",
        '-v', 'marketplace-frontend-node-modules:/app/node_modules',
        '-e', 'CHOKIDAR_USEPOLLING=true',
        '-e', 'WATCHPACK_POLLING=true',
        'node:18',
        'sh', '-c', 'cd /app && npm install && npx ng serve --host 0.0.0.0 --port 4200 --poll 2000'
    ) -Description 'Iniciando frontend em modo desenvolvimento (live reload com volumes mapeados)'
}

function Build-Images {
    Write-Step 'Build das imagens Docker'
    Invoke-ExternalCommand -FilePath 'docker' -ArgumentList @('build', '-t', $backendImage, $backendDockerfileDir) -Description 'Build da imagem do backend'
    Invoke-ExternalCommand -FilePath 'docker' -ArgumentList @('build', '-t', $frontendImage, $frontendDockerfileDir) -Description 'Build da imagem do frontend'
}

function Load-ImageIntoClusterIfNeeded {
    param([string]$ImageName)

    $currentContext = Get-KubernetesCurrentContext
    if ([string]::IsNullOrWhiteSpace($currentContext)) {
        return
    }

    if ($currentContext -like 'minikube*' -and (Test-CommandExists 'minikube')) {
        Invoke-ExternalCommand -FilePath 'minikube' -ArgumentList @('image', 'load', $ImageName) -Description "Carregando $ImageName no Minikube"
        return
    }

    if ($currentContext -like 'kind-*' -and (Test-CommandExists 'kind')) {
        Invoke-ExternalCommand -FilePath 'kind' -ArgumentList @('load', 'docker-image', $ImageName) -Description "Carregando $ImageName no Kind"
        return
    }
}

function Apply-KubernetesManifests {
    Write-Step 'Aplicando manifests no Kubernetes'
    Invoke-ExternalCommand -FilePath 'kubectl' -ArgumentList @('apply', '-f', $dbManifest) -Description 'Aplicando manifesto do PostgreSQL'
    Invoke-ExternalCommand -FilePath 'kubectl' -ArgumentList @('apply', '-f', $keycloakManifest) -Description 'Aplicando manifesto do Keycloak'
    Invoke-ExternalCommand -FilePath 'kubectl' -ArgumentList @('apply', '-f', $backendManifest) -Description 'Aplicando manifesto do backend'
    Invoke-ExternalCommand -FilePath 'kubectl' -ArgumentList @('apply', '-f', $frontendManifest) -Description 'Aplicando manifesto do frontend'
}

function Start-KubernetesPortForward {
    Invoke-ExternalCommand -FilePath 'kubectl' -ArgumentList @('port-forward', 'service/angular-service', '4200:4200') -Description 'Port-forward Frontend'
    Invoke-ExternalCommand -FilePath 'kubectl' -ArgumentList @('port-forward', 'service/marketplace-svc', '8080:8080') -Description 'Port-forward Backend'
    Invoke-ExternalCommand -FilePath 'kubectl' -ArgumentList @('port-forward', 'service/keycloak-service', '8081:8081') -Description 'Port-forward Keycloak'
}

function Start-LocalAll {
    Ensure-DockerNetwork
    Stop-DockerContainerIfExists -ContainerName 'marketplace-frontend'
    Stop-DockerContainerIfExists -ContainerName 'marketplace-backend'
    Stop-DockerContainerIfExists -ContainerName 'marketplace-keycloak'
    Stop-DockerContainerIfExists -ContainerName 'marketplace-postgres'
    Invoke-ExternalCommand -FilePath 'docker' -ArgumentList @('volume', 'create', 'marketplace-postgres-data') -Description 'Criando volume do PostgreSQL'
    Invoke-ExternalCommand -FilePath 'docker' -ArgumentList @('volume', 'create', 'marketplace-keycloak-data') -Description 'Criando volume do Keycloak'
    Start-LocalPostgres
    Start-LocalKeycloak
    Start-LocalBackend
    Start-LocalFrontend
    Write-Host ''
    Write-Host 'Todos os containers Docker foram iniciados localmente.' -ForegroundColor Green
    Write-Host 'Frontend: http://localhost:4200' -ForegroundColor White
    Write-Host 'Backend: http://localhost:8080' -ForegroundColor White
    Write-Host 'Keycloak: http://localhost:8081' -ForegroundColor White
}

function Stop-LocalAll {
    foreach ($entry in $localContainers) {
        $name = $entry.Name
        if (Get-LocalContainerStatus -ContainerName $name) {
            Invoke-ExternalCommand -FilePath 'docker' -ArgumentList @('stop', $name) -Description "Parando container $name"
        }
    }
}

function Remove-LocalAll {
    foreach ($entry in $localContainers) {
        $name = $entry.Name
        Stop-DockerContainerIfExists -ContainerName $name
    }
}

function Restart-LocalContainer {
    param([string]$ContainerName)
    if (Get-LocalContainerStatus -ContainerName $ContainerName) {
        Invoke-ExternalCommand -FilePath 'docker' -ArgumentList @('restart', $ContainerName) -Description "Reiniciando container $ContainerName"
    } else {
        Write-Host "Container $ContainerName nao esta em execucao." -ForegroundColor Yellow
    }
}

function Show-ServiceSelection {
    $choices = @(
        New-Object System.Management.Automation.Host.ChoiceDescription '&1 Postgres', 'Iniciar ou reiniciar o Postgres localmente'
        New-Object System.Management.Automation.Host.ChoiceDescription '&2 Keycloak', 'Iniciar ou reiniciar o Keycloak localmente'
        New-Object System.Management.Automation.Host.ChoiceDescription '&3 Backend', 'Iniciar ou reiniciar o backend localmente'
        New-Object System.Management.Automation.Host.ChoiceDescription '&4 Frontend', 'Iniciar ou reiniciar o frontend localmente (produção)'
        New-Object System.Management.Automation.Host.ChoiceDescription '&5 Frontend Dev', 'Iniciar frontend em modo desenvolvimento (live reload)'
    )

    $choice = $Host.UI.PromptForChoice('Servicos', 'Qual servico?', $choices, 0)
    return $choice
}

function Menu {
    do {
        Clear-Host
        Write-Host '==========================================' -ForegroundColor Magenta
        Write-Host '   Marketplace - Gerenciador de Containers' -ForegroundColor Magenta
        Write-Host '==========================================' -ForegroundColor Magenta

        Show-Status

        $options = @(
            New-Object System.Management.Automation.Host.ChoiceDescription '&1 Subir todos', 'Build e subir todos os servicos'
            New-Object System.Management.Automation.Host.ChoiceDescription '&2 Subir especifico', 'Iniciar um servico local especifico'
            New-Object System.Management.Automation.Host.ChoiceDescription '&3 Reiniciar servico', 'Reiniciar um container existente'
            New-Object System.Management.Automation.Host.ChoiceDescription '&4 Remover servico', 'Remover um container local'
            New-Object System.Management.Automation.Host.ChoiceDescription '&5 Derrubar todos', 'Parar e remover todos os containers locais'
            New-Object System.Management.Automation.Host.ChoiceDescription '&6 Status', 'Mostrar status novamente'
            New-Object System.Management.Automation.Host.ChoiceDescription '&7 Sair', 'Encerrar'
        )

        $action = $Host.UI.PromptForChoice('Marketplace', 'Escolha a acao:', $options, 0)

        switch ($action) {
            0 {
                if (Get-KubernetesCurrentContext) {
                    Write-Host 'Kubernetes detectado. Aplicando manifests...' -ForegroundColor Cyan
                    Build-Images
                    Load-ImageIntoClusterIfNeeded -ImageName $backendImage
                    Load-ImageIntoClusterIfNeeded -ImageName $frontendImage
                    Apply-KubernetesManifests
                    Write-Host 'Deploy Kubernetes concluido.' -ForegroundColor Green
                } else {
                    Build-Images
                    Start-LocalAll
                }
            }
            1 {
                $choice = Show-ServiceSelection
                switch ($choice) {
                    0 { Start-LocalPostgres }
                    1 { Start-LocalKeycloak }
                    2 { Start-LocalBackend }
                    3 { Start-LocalFrontend }
                    4 { Start-LocalFrontendDev }
                }
            }
            2 {
                $choice = Show-ServiceSelection
                switch ($choice) {
                    0 { Restart-LocalContainer -ContainerName 'marketplace-postgres' }
                    1 { Restart-LocalContainer -ContainerName 'marketplace-keycloak' }
                    2 { Restart-LocalContainer -ContainerName 'marketplace-backend' }
                    3 { Restart-LocalContainer -ContainerName 'marketplace-frontend' }
                }
            }
            3 {
                $choice = Show-ServiceSelection
                switch ($choice) {
                    0 { Stop-DockerContainerIfExists -ContainerName 'marketplace-postgres' }
                    1 { Stop-DockerContainerIfExists -ContainerName 'marketplace-keycloak' }
                    2 { Stop-DockerContainerIfExists -ContainerName 'marketplace-backend' }
                    3 { Stop-DockerContainerIfExists -ContainerName 'marketplace-frontend' }
                }
            }
            4 {
                Stop-LocalAll
                Remove-LocalAll
            }
            5 {
                Show-Status
            }
            6 {
                break
            }
        }

        Write-Host ''
        Write-Host 'Pressione Enter para voltar ao menu...' -ForegroundColor Yellow
        Read-Host | Out-Null
    } while ($true)
}

Menu
