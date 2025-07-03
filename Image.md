mermaid
graph TD
    %% Camada do Cliente
    Cliente[Navegador do Cliente] --> Frontend[Frontend Angular]

    %% Principais Componentes
    subgraph "Frontend"
        Frontend --> InterfaceAdmin[Interface Admin]
        Frontend --> InterfaceCliente[Interface Cliente]
        Frontend --> Autenticacao[Autenticação]
    end

    subgraph "Backend"
        API[API Spring Boot] --> Servicos[Camada de Serviços]
        Servicos --> Repositorios[Repositórios]
        Repositorios --> BancoDados[(PostgreSQL)]
    end

    %% Autenticação
    Autenticacao <--> Keycloak[Keycloak]
    API <--> Keycloak

    %% Conexões
    Frontend <--> API

    %% Infraestrutura
    subgraph "Kubernetes"
        K8s[Cluster Kubernetes]
        K8s --> PodFrontend[Pod Angular]
        K8s --> PodBackend[Pod Spring Boot]
        K8s --> PodDB[Pod PostgreSQL]
        K8s --> PodKeycloak[Pod Keycloak]
    end
