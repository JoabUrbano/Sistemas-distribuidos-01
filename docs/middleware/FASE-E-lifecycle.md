# Fase E — Lifecycle Management (duas famílias de padrões)

## Objetivo

Implementar **dois padrões** do primeiro grupo (instâncias de objeto remoto) e **dois padrões** do segundo grupo (gerenciamento de recursos), conforme o PDF. Escolha combinações que façam sentido no seu domínio e que você consiga **mostrar no código e na apresentação**.

## Primeiro grupo — instância do objeto remoto (escolha **2 de 3**)

| Padrão | Ideia | Exemplo de uso no seu projeto |
|--------|--------|-------------------------------|
| **Static Instance** | Uma única instância do remote object por processo JVM. | `Sensoriamento` ou `Validador` como singleton no servidor. |
| **Per-Request Instance** | Novo objeto por requisição (útil para estado zero ou testabilidade). | Criar validador novo a cada POST (se stateless). |
| **Client-Dependent Instance** | Estado ou instância associada a um cliente (id de sessão, tenant). | Exige propagar id de cliente no HTTP ou no invocation context (combina com Fase F). |

**Documente no relatório quais dois você adotou** e por quê.

## Segundo grupo — recursos (escolha **2 de 4**)

| Padrão | Ideia | Exemplo de uso no seu projeto |
|--------|--------|-------------------------------|
| **Lazy Acquisition** | Obter recurso caro só na primeira necessidade. | Abrir pool de conexões ou parser pesado na primeira invocação. |
| **Pooling** | Reutilizar objetos/conn/consumidores limitados. | Pool de `DatagramSocket` ou threads para backends (com cuidado). |
| **Leasing** | Registro ou recurso expira se não renovado. | Heartbeat `Quack` como lease do serviço no broker (já próximo do código atual). |
| **Passivation** | Desativar estado pouco usado para liberar memória. | Menos natural neste trabalho pequeno; use só se tiver estado volumoso. |

**Documente no relatório quais dois você adotou** e onde estão no código.

## O que fazer (passo a passo conceitual)

1. Escolher **2 + 2** padrões e registrar a decisão antes de codificar em excesso.  
2. Implementar o mínimo necessário para cada padrão ser **verificável** (logs, API pública ou teste unitário).  
3. Alinhar **Leasing** ao tempo de remoção de serviços sem heartbeat no gateway, se for uma das escolhas.  
4. Evitar conflito com JMeter: mudanças não devem alterar o formato esperado dos testes sem atualizar o plano `.jmx`.

## Critérios de conclusão da fase

- Quatro padrões distintos aparecem no relatório com **nome do livro + trecho de código ou classe**.  
- Nenhum padrão fica só “de gaveta”; prefira menos padrões bem demonstrados a muitos vagos.

---

## Pontos do relatório cobertos por esta fase

### Critério de avaliação (PDF)

- **Lifecycle Management Patterns (2,0)** — coberto ao demonstrar **dois** padrões do grupo instância **e dois** do grupo recurso, com explicação e implementação.

### Requisitos do enunciado

- **Lifecycle Management (Static / Per-Request / Client-Dependent) — 2 de 3**.  
- **Lifecycle Management (Lazy Acquisition, Pooling, Leasing, Passivation) — 2 de 4**.

### Apresentação

- Ser capaz de explicar em um slide cada padrão escolhido e apontar onde o código o materializa.
