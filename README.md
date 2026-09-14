# Collab Board API

Quadro Kanban colaborativo em tempo real. Vários usuários autenticados atualizam o
mesmo quadro simultaneamente e veem as mudanças uns dos outros instantaneamente,
via WebSocket (STOMP), sem precisar dar refresh.

Este é o segundo projeto Java do portfólio (o primeiro, focado em segurança, é o
Secure Task API). Aqui o foco é provar uma habilidade que nenhum outro projeto do
portfólio demonstra: sincronização de estado em tempo real entre múltiplos clientes.

## Stack

- Java 17
- Spring Boot 4 (Web, Security, Data JPA, Validation, WebSocket)
- PostgreSQL
- JWT (JJWT) para autenticação stateless
- STOMP sobre SockJS para tempo real

## Arquitetura

- **REST** faz as mutações (`/api/boards`, `/api/boards/{id}/cards`) e persiste no
  Postgres via Spring Data JPA.
- Depois de cada mutação bem-sucedida, o backend publica um evento no tópico STOMP
  `/topic/boards/{boardId}` via `SimpMessagingTemplate`. Todo cliente inscrito nesse
  quadro recebe o evento e atualiza a tela na hora — inclusive quem fez a mudança.
- Esse padrão (REST para escrever + broadcast por WebSocket) foi escolhido em vez de
  STOMP `@MessageMapping` fim a fim porque mantém a API testável e simples via HTTP
  comum, ao mesmo tempo que entrega a experiência em tempo real.
- **Autenticação**: login/registro emitem um JWT (endpoints REST, sem sessão). Esse
  mesmo token autentica tanto as chamadas REST (header `Authorization: Bearer`)
  quanto a conexão WebSocket — o handshake HTTP do SockJS é público (`/ws/**`), mas
  o frame STOMP `CONNECT` é validado por um `ChannelInterceptor`
  (`StompAuthChannelInterceptor`) que exige e valida o mesmo Bearer token.

## Rodando localmente

1. Suba um Postgres local (ou ajuste `application.properties`):
   ```
   docker run --name collab-board-db -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=collab_board -p 5432:5432 -d postgres:16
   ```
2. Rode a aplicação:
   ```
   ./mvnw spring-boot:run
   ```
3. Abra `http://localhost:8080` em duas abas (ou dois navegadores) — há um cliente de
   teste estático (`src/main/resources/static/index.html`) com SockJS + Stomp.js
   pronto pra registrar usuário, criar um board e ver os cards sincronizarem ao vivo
   entre as abas.

## Endpoints principais

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/auth/register` | Cria usuário, retorna JWT |
| POST | `/api/auth/login` | Autentica, retorna JWT |
| POST | `/api/boards` | Cria um quadro |
| GET | `/api/boards` | Lista quadros |
| GET | `/api/boards/{id}/cards` | Lista cards de um quadro (colunas fixas: TODO / IN_PROGRESS / DONE) |
| POST | `/api/boards/{id}/cards` | Cria card |
| PUT | `/api/boards/{id}/cards/{cardId}` | Edita título/descrição |
| PATCH | `/api/boards/{id}/cards/{cardId}/move` | Move card entre colunas/posições |
| DELETE | `/api/boards/{id}/cards/{cardId}` | Remove card |
| WS | `/ws` (STOMP) | Handshake SockJS; assinar `/topic/boards/{id}` para eventos ao vivo |

## Próximos passos (fora do escopo do MVP)

- Convite/membership por quadro (hoje qualquer usuário autenticado pode acessar
  qualquer quadro pelo id — aceitável para portfólio, não para produção).
- Presença de usuários (quem está online no quadro agora).
