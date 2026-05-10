# Fase F — Extension Patterns (Interceptor, Invocation Context, Protocol Plug-In)

## Objetivo

Completar os **padrões de extensão** exigidos: **Invocation Interceptor**, **Invocation Context** e **Protocol Plug-In**, sendo que o plug-in de protocolo deve suportar **dois protocolos** (o PDF cita explicitamente **UDP e TCP** como exemplo). Esta fase torna a plataforma extensível sem espalhar `if (tcp) ... else (udp)` pelo código da aplicação.

## Padrões desta fase

| Padrão | Papel resumido |
|--------|----------------|
| **Invocation Interceptor** | Cadeia de interceptadores antes/depois do invoke (auditoria, métricas, validação transversal). |
| **Invocation Context** | Estrutura que acompanha uma invocação (ids de correlação, metadados, opcionalmente id de cliente para lifecycle). |
| **Protocol Plug-In** | Interface comum para transporte; implementações **TCP** e **UDP** plugáveis na inicialização do servidor/broker. |

## O que fazer (passo a passo conceitual)

1. **Invocation Context**  
   - Definir um objeto mutável ou record estendido passado do SRH até o Invoker.  
   - Preencher correlation id (UUID por requisição) para rastreio em logs.

2. **Invocation Interceptor**  
   - Interface `intercept(InvocationContext, Chain)` ou lista ordenada de interceptores registrados no middleware.  
   - Incluir pelo menos um interceptor real (ex.: log de tempo de processamento).

3. **Protocol Plug-In**  
   - Contrato único: `bind`, `receive`, `send` ou equivalente abstrato sobre mensagens já unmarshalled.  
   - Duas implementações: uma usando o caminho socket HTTP atual (TCP) e outra datagrama (UDP).  
   - As fábricas (`ServerFactory`, etc.) passam a selecionar **plugin** por configuração, não por duplicar classes monolíticas.

4. **JMeter**  
   - O cliente de teste continua em HTTP em direção ao gateway; o plug-in TCP/UDP vale para **comunicação interna** ou gateway↔worker conforme sua arquitetura. Deixe explícito no relatório **onde** cada protocolo é usado.

## Critérios de conclusão da fase

- Duas implementações de protocolo coexistem e são selecionáveis por configuração.  
- Pelo menos um interceptor é executado em todo fluxo de invocação remota demonstrado.  
- O contexto de invocação atravessa SRH → (interceptors) → Invoker.

---

## Pontos do relatório cobertos por esta fase

### Critério de avaliação (PDF)

- **Extension Patterns (3,0)** — critério principal desta fase: Interceptor, Invocation Context e Protocol Plug-In com **dois protocolos**.

### Requisitos do enunciado

- **Extension Patterns** da lista obrigatória.  
- **Protocol Plug-In** com **dois protocolos** (ex.: UDP e TCP — conforme PDF).

### Integração com apresentação

- Na demonstração, vale mostrar troca de configuração (ou duas execuções) que evidencie os dois protocolos sem quebrar o roteiro JMeter acordado com o professor.

### Observação

- Se Lifecycle usar **Client-Dependent Instance**, o **Invocation Context** é o lugar natural para carregar o id do cliente.
