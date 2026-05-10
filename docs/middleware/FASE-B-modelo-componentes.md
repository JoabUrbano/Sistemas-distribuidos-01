# Fase B — Modelo de componentes (anotações + invocação HTTP)

## Objetivo

Atender o requisito central do enunciado: **definir um objeto remoto por meio de anotações** e **viabilizar a execução dos métodos desse objeto remoto por invocações HTTP**. Esta fase introduz o **descritor** do serviço (metadados em tempo de compilação ou via scan em tempo de execução) e o **despacho** da requisição HTTP até o método correto, substituindo aos poucos o roteamento manual por `split` fixo — sem quebrar o contrato que o JMeter já envia.

## Relação com a nota “Modelo de Componentes”

O PDF compara dois níveis de sofisticação:

1. **Mais simples**: parâmetros e retornos genéricos (ex.: JSON ou texto estruturado).  
2. **Mais sofisticado**: **assinatura do método preservada** (tipos, nomes de parâmetros), com marshalling adequado.

Planeje para pelo menos o nível (1) na primeira entrega desta fase; evolua para (2) se houver tempo, pois o critério valoriza o modelo mais rico.

## O que fazer (passo a passo conceitual)

1. **Definir as anotações da sua plataforma** (nomes são seus; exemplos ilustrativos):  
   - Marcação da classe exportada como serviço remoto.  
   - Marcação dos métodos expostos (nome lógico da operação, verbo HTTP se quiser, tipo de conteúdo).

2. **Registro em tempo de execução**  
   - Scan do classpath ou registro explícito na inicialização do servidor de aplicação.  
   - Montar um **mapa**: nome da operação (ex.: `temperatura`, `velocidade`) → `Method` + instância alvo + conversão de parâmetros.

3. **Camada de invocação HTTP**  
   - O corpo HTTP atual (texto com `;`) pode continuar sendo o formato aceito pelo gateway JMeter.  
   - Internamente, o servidor traduz o payload para uma chamada de método via reflexão ou handles gerados.

4. **Validação e erros de formato**  
   - Respostas HTTP e mensagens alinhadas ao que já retornam `TCPSensoriamentoServer` / `TCPValidacaoServer` para não exigir mudança imediata no plano JMeter.

5. **Documentar o modelo**  
   - Tabela no relatório: anotação → significado → exemplo de classe de negócio anotada.

## Critérios de conclusão da fase

- Pelo menos uma classe de negócio (sensor ou validador) está **anotada** e é invocada **via** essas anotações, não só por `if/else` em string.  
- O fluxo JMeter → gateway → backends continua produzindo respostas **equivalentes** às anteriores para o mesmo payload de teste.  
- O relatório pode exibir um trecho de código “antes/depois” ou o exemplo canônico da plataforma.

## Dependências de outras fases

- **Fase A** recomendada: anotações e loaders ficam no JAR do middleware; classes de negócio ficam na aplicação.  
- Padrões **Invoker** e **Marshaller** serão **refinados** na Fase C; nesta fase pode haver um despacho mínimo interno que depois você nomeia como Invoker.

---

## Pontos do relatório cobertos por esta fase

### Critério de avaliação (PDF)

- **Modelo de Componentes (2,0)** — este é o critério **principal** da Fase B: descrever como o objeto remoto é definido (anotações), como o método é escolhido e como a requisição HTTP chega até ele. Inclua exemplos de código e, se possível, justifique o nível de sofisticação (genérico vs assinatura preservada).

### Requisitos do enunciado

- **Modelo de Componente capaz de definir um objeto por anotações** — cumprido pela especificação das anotações + processo de registro.  
- **Viabilizar execução dos métodos por invocações HTTP** — cumprido pelo fluxo gateway/servidor que recebe POST e despacha para o método anotado.

### Relação com outros critérios

- **Basic Remoting**: você pode **mencionar** antecipadamente Invoker/Marshaller como “próximos passos”, mas a pontuação específica de Basic Remoting é consolidada na **Fase C**.  
- **Apresentação**: demonstre uma classe de negócio anotada e uma chamada via JMeter passando pelo middleware.
