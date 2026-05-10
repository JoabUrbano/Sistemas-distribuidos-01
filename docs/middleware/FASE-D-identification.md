# Fase D — Identification Patterns (Lookup, Object Id, Absolute Object Reference)

## Objetivo

Tornar explícitos os mecanismos pelos quais clientes e broker **encontram** e **referenciam** objetos remotos: não basta um IP e porta implícitos no payload `Quack`. Esta fase formaliza **Lookup**, **Object Id** e **Absolute Object Reference (AOR)** como parte do modelo da plataforma.

## Padrões desta fase

| Padrão | Papel resumido |
|--------|----------------|
| **Lookup** | Serviço de registro e descoberta: registrar um objeto/serviço remoto e obter referência para invocação posterior (pode ser o próprio broker com API de registro). |
| **Object Id** | Identificador lógico estável do objeto remoto dentro do seu tipo de serviço (nome, UUID, etc.). |
| **Absolute Object Reference** | Referência completa para alcançar o objeto: tipicamente protocolo + localização de rede + object id (e opcionalmente endpoints auxiliares). |

## O que fazer (passo a passo conceitual)

1. **Modelar AOR**  
   - Classe ou record imutável com campos mínimos: identificador do protocolo (TCP/UDP), host, porta, **object id**, tipo de serviço (sensor/validação) se necessário para o trabalho.

2. **Object Id**  
   - Gerar ids na subida de cada instância de worker ou usar nome configurável; garantir que o gateway distinga réplicas sem colidir.

3. **Lookup**  
   - API interna do broker: `register(AOR)`, `unregister`, `resolve(objectId)` ou `listByType`. O heartbeat `Quack` passa a construir um **AOR** completo em vez de apenas strings soltas.

4. **Compatibilidade**  
   - Se o JMeter não envia object id, o gateway pode continuar usando balanceamento por tipo; mesmo assim, os workers registram **com** id para demonstrar o padrão na documentação e nos logs.

5. **Documentação**  
   - Diagrama: cliente → Broker → Lookup → AOR → SRH do nó certo.

## Critérios de conclusão da fase

- Toda entrada na tabela de serviços do gateway pode ser explicada como um **AOR** armazenado.  
- O relatório define claramente o que é **Object Id** no seu sistema e mostra um exemplo (string ou UUID).  
- O fluxo JMeter continua operacional; mudanças no formato de registro são retrocompatíveis ou documentadas.

---

## Pontos do relatório cobertos por esta fase

### Critério de avaliação (PDF)

- **Identification Patterns (1,0)** — coberto ao apresentar Lookup, Object Id e Absolute Object Reference com correspondência no código.

### Requisitos do enunciado

- Lista obrigatória: **Lookup**, **Object Id**, **Absolute Object Reference**.

### Outros critérios

- **Broker** (Fase C) e **Lookup** se complementam: descreva como o broker usa o lookup para rotear.
