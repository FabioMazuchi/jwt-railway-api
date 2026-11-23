# JWT Railway API

API mínima com Java 17, Spring Boot, Spring Security, JWT, MySQL e Swagger UI.

## Funcionalidades

- Cadastro de usuário: `POST /api/auth/register`
- Login e geração de JWT: `POST /api/auth/login`
- Consultar usuário autenticado: `GET /api/auth/me`
- Health check: `GET /api/health`
- Senhas armazenadas com BCrypt.
- Rotas protegidas por JWT.
- Swagger UI com botão **Authorize**.

## Executar localmente

Requisitos: Java 17+, Maven 3.9+ e MySQL.

Configure as variáveis de ambiente:

```bash
export MYSQLHOST=localhost
export MYSQLPORT=3306
export MYSQLDATABASE=jwt_demo
export MYSQLUSER=root
export MYSQLPASSWORD=sua_senha
export JWT_SECRET='uma-chave-local-com-pelo-menos-32-caracteres'
```

Crie o banco `jwt_demo` no MySQL e execute:

```bash
mvn spring-boot:run
```

Swagger: `http://localhost:8080/swagger-ui.html`

## Deploy no Railway

1. Crie um projeto no Railway.
2. Adicione um serviço **MySQL**.
3. Publique este projeto em um repositório GitHub.
4. No Railway, crie um serviço a partir desse repositório.
5. Selecione o builder **Dockerfile**. O arquivo `Dockerfile` já está na raiz.
6. Nas variáveis do serviço da API, configure:

| Variável | Valor |
|---|---|
| `MYSQLHOST` | `${{MySQL.MYSQLHOST}}` |
| `MYSQLPORT` | `${{MySQL.MYSQLPORT}}` |
| `MYSQLDATABASE` | `${{MySQL.MYSQLDATABASE}}` |
| `MYSQLUSER` | `${{MySQL.MYSQLUSER}}` |
| `MYSQLPASSWORD` | `${{MySQL.MYSQLPASSWORD}}` |
| `JWT_SECRET` | Uma chave aleatória com pelo menos 32 bytes |
| `JWT_EXPIRATION_MS` | `3600000` |

Se o serviço MySQL tiver outro nome, ajuste a referência `${{MySQL...}}` para o nome correto do serviço.

7. Gere um domínio público para o serviço da API em **Settings → Networking → Public Networking**.
8. Acesse `https://SEU-DOMINIO/swagger-ui.html`.

**Importante:** use uma `JWT_SECRET` forte e estável. Não a altere entre deploys, pois os tokens emitidos anteriormente deixarão de ser válidos. Não compartilhe essa chave.

## Teste pelo Swagger

### 1. Cadastrar

`POST /api/auth/register`

```json
{
  "name": "Fabio",
  "email": "fabio@example.com",
  "password": "SenhaForte123"
}
```

### 2. Fazer login

`POST /api/auth/login`

```json
{
  "email": "fabio@example.com",
  "password": "SenhaForte123"
}
```

Copie o valor de `accessToken` da resposta.

### 3. Autorizar no Swagger

Clique em **Authorize** e cole somente o token, sem escrever `Bearer` e sem aspas. O Swagger adiciona o prefixo automaticamente.

### 4. Testar rota protegida

Execute `GET /api/auth/me`. Sem token válido, a resposta deve ser HTTP 401.

## Banco de dados

O Hibernate está configurado com `ddl-auto=update`, então cria a tabela `app_users` quando a aplicação inicia. Para produção de longo prazo, substitua isso por migrations com Flyway ou Liquibase.
