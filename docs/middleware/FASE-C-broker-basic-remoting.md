# Fase C — Broker e Basic Remoting Patterns

## Objetivo

Formalizar no código e na documentação os **padrões básicos de remoting** do livro *Remoting Patterns*, em torno de um **Broker** central que medeia solicitações e respostas. No seu sistema, o **ApiGateway** já age como um intermediário; esta fase **nomeia responsabilidades**, introduz interfaces estáveis e prepara tratamento uniforme de falhas.

## Padrões desta fase (checklist do enunciado)

| Padrão | Papel resumido |
|--------|----------------|
| **Broker** | Ponto central que recebe requisições de clientes, resolve para qual recurso/servidor enviar e devolve o resultado (ou erro). |
| **Server Request Handler (SRH)** | Aceita conexão (TCP) ou datagrama (UDP), lê bytes, entrega uma mensagem lógica ao restante da pilha e escreve a resposta. |
| **Invoker** | Recebe uma invocação abstrata (nome da operação + argumentos desmaterializados) e executa no **Remote Object**. |
| **Marshaller** | Converte entre representação na rede (texto, JSON, etc.) e objetos Java usados pelo Invoker. |
| **Remote Object** | A instância de negócio que implementa a lógica (ex.: sensor, validador). |
| **Remoting Error** | Erros específicos da camada de remoting (timeout, falha de marshall, serviço indisponível), distintos de erros de domínio. |

## O que fazer (passo a passo conceitual)

1. **Broker**  
   - Extrair do gateway a lógica “recebe payload → decide registro Quack vs encaminha → agrega erros” para classes/interfaces nomeadas (`Broker`, `BrokerRequest`, `BrokerResponse`).  
   - Manter o protocolo HTTP e o formato de corpo aceitos pelo JMeter.

2. **Server Request Handler**  
   - Uma implementação para TCP (socket + leitura HTTP mínima) e outra para UDP (pacote), ambas produzindo o mesmo tipo de mensagem interna antes do Broker ou antes do Invoker local.

3. **Marshaller**  
   - Interface clara (`marshal` / `unmarshal`). A implementação atual pode ser **texto delimitado**; o importante é que a troca futura (JSON) não espalhe parsing pela aplicação.

4. **Invoker**  
   - Entrada: nome da operação + argumentos já convertidos. Saída: resultado ou exceção de remoting. Conecta-se ao modelo de anotações da Fase B.

5. **Remote Object**  
   - Documentar qual instância é o alvo (singleton por serviço, pool, etc. — detalhes de lifecycle ficam na Fase E, mas o Invoker já pode receber o alvo por injeção).

6. **Remoting Error**  
   - Hierarquia de exceções ou códigos (ex.: `ServiceUnavailable`, `BackendTimeout`) mapeados para HTTP 502/503/504 e corpo padronizado.

## Critérios de conclusão da fase

- Em um diagrama de pacotes ou um parágrafo do relatório, cada um dos seis padrões acima aparece **ligado a uma classe ou interface** do seu projeto.  
- O comportamento dos testes JMeter existentes permanece válido.  
- Falhas simuladas (backend parado, timeout) são classificadas como **Remoting Error** onde for apropriado.

## Dependências

- **Fases A e B** facilitam colocar Invoker e Marshaller no middleware enquanto a aplicação só fornece Remote Objects anotados.

---

## Pontos do relatório cobertos por esta fase

### Critério de avaliação (PDF)

- **Basic Remoting Patterns (2,0)** — coberto integralmente ao descrever e implementar: Server Request Handler, Invoker, Marshaller, Remote Object, Remoting Error, em conjunto com o Broker.

### Requisitos do enunciado

- Item da lista obrigatória: **Broker** e todos os **Basic Remoting** nomeados no PDF.

### Outros

- **Identification / Lifecycle / Extension**: cite apenas “integração futura” se ainda não implementados; não misture nota desta fase com as outras seções sem código correspondente.
