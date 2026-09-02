# Docker Manager API

API REST para gerenciar containers e imagens Docker. Backend pronto para integrar com um dashboard.

##  Funcionalidades

- ✅ Listar todos os containers (rodando ou inativos)
- ✅ Iniciar container
- ✅ Parar container
- ✅ Remover container
- ✅ Criar novo container a partir de uma imagem
- ✅ Listar todas as imagens Docker
- ✅ Filtrar imagens por nome

##  Tecnologias

- **Java 17+**
- **Spring Boot 3.x**
- **Docker Java API** - Cliente Java oficial pra Docker
- **Maven** - Gerenciador de dependências
- **Conexão Unix Socket** - Via `/var/run/docker.sock`

##  Pré-requisitos

- Docker instalado e rodando
- Java 17 ou superior
- Maven 3.8+
- Linux/macOS (socket Unix) ou Windows com WSL2

##  Como Rodar

### 1. Clone o repositório
```bash
git clone https://github.com/Paccanaro18/Docker-Manager.git
cd Docker-Manager
```

### 2. Configure Docker Socket (se necessário)

A aplicação está configurada pra conectar em `unix:///var/run/docker.sock` com **TLS verification desabilitada**.

Se tiver problemas de permissão no socket:
```bash
# Adicione seu usuário ao grupo docker
sudo usermod -aG docker $USER
newgrp docker
```

### 3. Rode a aplicação
```bash
mvn spring-boot:run
```

Ou com Maven wrapper:
```bash
./mvnw spring-boot:run
```

API estará disponível em **`http://localhost:8080`**

##  Endpoints da API

### Containers - `/api/containers`

| Método | Endpoint | Descrição | Parâmetros |
|--------|----------|-----------|-----------|
| **GET** | `/api/containers` | Lista containers | `all=true` (mostra inativos) |
| **POST** | `/api/containers` | Cria novo container | Body: `{ "imageName": "nginx:latest" }` |
| **POST** | `/api/containers/{id}/start` | Inicia container | Path Variable: container ID |
| **POST** | `/api/containers/{id}/stop` | Para container | Path Variable: container ID |
| **DELETE** | `/api/containers/{id}` | Remove container | Path Variable: container ID |

### Imagens - `/api/images`

| Método | Endpoint | Descrição | Parâmetros |
|--------|----------|-----------|-----------|
| **GET** | `/api/images` | Lista todas as imagens | - |
| **GET** | `/api/images/filter` | Filtra imagens por nome | `filterName=postgres` |

##  Exemplos de Uso

### Listar containers rodando
```bash
curl http://localhost:8080/api/containers?all=false
```

### Listar todos os containers (incluindo inativos)
```bash
curl http://localhost:8080/api/containers?all=true
```

### Criar novo container
```bash
curl -X POST http://localhost:8080/api/containers \
  -H "Content-Type: application/json" \
  -d '{"imageName":"nginx:latest"}'
```

### Iniciar container
```bash
curl -X POST http://localhost:8080/api/containers/abc123def456/start
```

### Parar container
```bash
curl -X POST http://localhost:8080/api/containers/abc123def456/stop
```

### Remover container
```bash
curl -X DELETE http://localhost:8080/api/containers/abc123def456
```

### Listar imagens
```bash
curl http://localhost:8080/api/images
```

### Filtrar imagens por nome
```bash
curl http://localhost:8080/api/images/filter?filterName=postgres
```

## Configuração do Docker Client

A classe `DockerClientConfig` configura a conexão com Docker:

```java
// Docker Host
String dockerHost = "unix:///var/run/docker.sock";

// TLS Verification desabilitada
DockerClient.builder()
    .withDockerHost(dockerHost)
    .withDisableTlsVerification()  // TLS verificação desabilitada
    .build()
```

**Configuração atual:**
- **Docker Host**: `unix:///var/run/docker.sock` (Socket Unix padrão)
- **TLS Verification**: Desabilitada (conexão sem SSL/TLS)
- **HTTP Client**: Apache Docker HTTP Client

##  Detalhes Técnicos

### Dependências Principais

```xml
<!-- Spring Boot -->
<spring-boot-starter-web>
<spring-boot-starter-data-jpa>

<!-- Docker Java API -->
<docker-java>
<docker-java-transport-httpclient5>

<!-- Lombok (opcional) -->
<lombok>
```

### Estrutura de Respostas

Todas as endpoints retornam JSON.

**Listar containers:**
```json
[
  {
    "id": "abc123...",
    "names": ["/meu-container"],
    "image": "nginx:latest",
    "state": "running",
    "status": "Up 2 hours"
  }
]
```

**Listar imagens:**
```json
[
  {
    "id": "sha256:abc123...",
    "repoTags": ["nginx:latest"],
    "created": 1234567890,
    "size": 142000000
  }
]
```

##  Troubleshooting

### Erro: `Cannot connect to Docker daemon`
```bash
# Verifique se Docker está rodando
docker ps

# Verifique permissões do socket
ls -l /var/run/docker.sock

# Adicione usuário ao grupo docker
sudo usermod -aG docker $USER
```

### Erro: `TLS Handshake`
A aplicação já tem TLS verification desabilitada no `DockerClientConfig`, então esse erro não deve ocorrer.

### Porta 8080 já em uso
```bash
# Use outra porta via environment variable
export SERVER_PORT=8081
mvn spring-boot:run
```


[![CI](https://github.com/Paccanaro18/Docker-Manager/actions/workflows/build.yml/badge.svg)](https://github.com/Paccanaro18/Docker-Manager/actions/workflows/build.yml)

##  Referências

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Docker Socket Documentation](https://docs.docker.com/engine/reference/commandline/dockerd/)
