# Distribuida — middleware (fases A–G)

Projeto multimódulo: a **plataforma de middleware** compila como JAR (`middleware-platform`) e a **aplicação** (`distribuida-app`) declara dependência explícita sobre esse JAR. O comportamento do fluxo gateway → sensoriamento → validação permanece o da primeira unidade; nesta fase a mudança é principalmente de **organização e build**.

## Pré-requisitos

- JDK **21**
- **Maven** 3.9+ (recomendado)

## Build (na raiz do repositório)

Compila o middleware e, em seguida, a aplicação que o consome:

```bash
mvn clean install
```

O artefato da plataforma fica em `middleware-platform/target/middleware-platform-1.0-SNAPSHOT.jar`. A aplicação empacota/usa essa dependência via Maven.

## Ordem de execução dos processos

1. **Gateway** (porta **9003** — HTTP mínimo exposto pelo servidor do gateway)
2. **Sensoriamento** (porta **9010**)
3. **Validação** (porta **9005**)

Cada um é um `main` distinto no módulo `distribuida-app`.

### Terminal 1 — Gateway

Na raiz do projeto:

```bash
mvn -pl distribuida-app exec:java -Dexec.mainClass=distribuida.app.GatewayMain
```

### Terminal 2 — Sensoriamento

```bash
mvn -pl distribuida-app exec:java -Dexec.mainClass=Sensoriamento.main
```

### Terminal 3 — Servidor de validação

```bash
mvn -pl distribuida-app exec:java -Dexec.mainClass=ValidacaoServer.main
```

Alternativa: entrar em `distribuida-app/` e usar `mvn exec:java` com o mesmo `-Dexec.mainClass=...`.

## Onde está o “middleware” e onde está o gateway

- **Middleware** (`middleware-platform`): biblioteca com peças **reutilizáveis de comunicação** — por exemplo contrato/fábrica do servidor do gateway (`ServerContract`, `ServerFactory`), estratégias TCP/UDP do gateway e utilitários compartilhados (`HttpMinimalParser`, `Service`). Ele **não** é um processo separado; é dependência compilada.
- **Gateway**: continua sendo o **processo** iniciado por `GatewayMain` na aplicação. Esse `main` só **monta e sobe** o servidor usando as classes do middleware (porta 9003). Ou seja: o gateway não “virou só middleware”; o middleware **implementa a camada de comunicação** que o gateway usa.

As regras de negócio de sensoriamento e validação permanecem em `distribuida-app` nos pacotes `Sensoriamento` e `ValidacaoServer`.

## Estrutura de módulos

| Módulo               | Conteúdo principal                                              |
|---------------------|------------------------------------------------------------------|
| `middleware-platform` | JAR da plataforma: gateway genérico, HTTP mínimo, shared        |
| `distribuida-app`     | Mains, sensoriamento, validação; depende de `middleware-platform` |

Documentação das fases do middleware: `docs/middleware/`.

## Configuração JVM (gateway e workers)

| Propriedade | Valor padrão | Descrição |
|-------------|--------------|-----------|
| `distribuida.gateway.port` | `9003` | Porta do gateway para heartbeat e clients HTTP. |
| `distribuida.gateway.protocol` | `TCP` | `TCP` = gateway HTTP sobre TCP (`TCPServer`); `UDP` = gateway sobre datagramas (`UDPServer`). |

Exemplo gateway UDP na porta 9003:

```bash
mvn -pl distribuida-app exec:java -Dexec.mainClass=distribuida.app.GatewayMain \
  -Ddistribuida.gateway.protocol=UDP
```

## O que foi implementado nas fases B–G (resumo)

- **B — Modelo de componentes**: anotações `@RemoteComponent` e `@RemoteOperation`; registro em tempo de execução via `AnnotationInvocationRegistry` + `ReflectionInvoker`; despacho HTTP preservando o corpo texto `;` para o JMeter.
- **C — Basic remoting**: `GatewayBroker` (Broker), `TCPServer`/`UDPServer` como **Server Request Handler** (`ServerRequestHandler`), `Invoker`, `Marshaller` (sensor/validação), objetos remotos (`Sensoriamento`, `Validador`), `RemotingException` e subclasses.
- **D — Identification**: `ObjectId`, `AbsoluteObjectReference`, `QuackMessage` com quarto campo opcional; `ServiceLookup` no `ServerTemplate`; logs `[lookup]` com AOR.
- **E — Lifecycle**: **Static Instance** (`Sensoriamento` singleton por processo), **Per-Request Instance** (`Validador` novo por requisição nos TCP/UDP de validação), **Lazy Acquisition** (`LazyHolder` para marshallers), **Leasing** (heartbeat + expiração no template, logs `[leasing]`).
- **F — Extension**: `InvocationContext` (correlation id), `InterceptorChain` + `LoggingInvocationInterceptor`, plug-ins de protocolo `GatewayTransport` (TCP vs UDP) selecionáveis por `-Ddistribuida.gateway.protocol`.
- **G — JMeter**: plano `docs/jmeter/distribuida-gateway-post.jmx` e `docs/jmeter/README.md` com metodologia para knee/usable capacity.

## Pacotes novos no middleware (`middleware-platform`)

| Pacote | Papel |
|--------|--------|
| `middleware.remoting.annotations` | Anotações do modelo de componentes |
| `middleware.remoting.invocation` | Registry + `ReflectionInvoker` + fábricas |
| `middleware.remoting.marshal` | Marshallers texto `;` |
| `middleware.remoting.gateway` | Broker, Quack/AOR, Lookup |
| `middleware.remoting.extension` | Interceptores |
| `middleware.remoting.identification` | Object Id, AOR |
| `middleware.remoting.lifecycle` | Políticas e `LazyHolder` |
| `middleware.remoting.worker` | Pipelines sensor/validação |
| `middleware.remoting.protocol` | `GatewayTransport` (plug-in TCP/UDP) |
