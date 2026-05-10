# Fase G — JMeter, testes de carga e capacidade (knee e usable)

## Objetivo

Cumprir os requisitos experimentais: **testes de carga com JMeter**, investigação da **capacidade do sistema**, identificação da **knee capacity** e da **usable capacity**, com tudo **pronto antes da apresentação**. Esta fase é majoritariamente **medição, documentação e ajuste de parâmetros**, não novos padrões do livro.

## Conceitos (para o relatório)

- **Knee capacity**: região em que o aumento de carga começa a degradar fortemente a latência ou o erro — “joelho” da curva throughput vs carga ou latência vs carga.  
- **Usable capacity**: faixa de operação na qual o sistema atende critérios aceitáveis (latência máxima, taxa de erro), ou seja, capacidade “útil” sob seu SLO.

Defina explicitamente quais métricas você usa (ex.: p95 de latência, % de erros HTTP).

## O que fazer (passo a passo conceitual)

1. **Versionar o plano JMeter**  
   - Arquivo `.jmx` no repositório ou anexo documentado.  
   - URL, método POST, corpo, threads, ramp-up, duração alinhados ao gateway (porta 9003 ou a configurada).

2. **Cenários de teste**  
   - Baseline (poucos usuários), rampa até saturação, e opcionalmente endurance curta.  
   - Registrar hardware/OS da máquina para reprodutibilidade.

3. **Coleta**  
   - Usar listeners JMeter (Summary, Aggregate Report) ou exportar CSV; correlacionar com logs do gateway se útil.

4. **Gráficos / tabelas**  
   - Plotar throughput e latência vs número de threads ou RPS.  
   - Marcar no relatório onde você estima **knee** e qual faixa considera **usable**.

5. **Apresentação**  
   - Roteiro: subir serviços → rodar plano → mostrar resultados.  
   - O enunciado pede que os testes cubram **todos os padrões implementados**: planeje um roteiro de demonstração (mesmo que um único plano JMeter exercite o caminho completo).

## Critérios de conclusão da fase

- Plano JMeter reproduzível por terceiros (instruções no README ou neste doc).  
- Relatório contém números e figuras, não só texto genérico.  
- Definições operacionais de knee e usable **explícitas**.

---

## Pontos do relatório cobertos por esta fase

### Requisitos do enunciado (seção “Testes de Carga”)

- Uso da ferramenta **JMeter** para testes de carga da aplicação sobre a plataforma de middleware.  
- **Investigar capacidade do sistema** e conhecer **knee capacity** e **usable capacity**.

### Apresentação

- **Testes preparados e configurados antecipadamente** no dia da apresentação.  
- Execução cobrindo o fluxo que demonstra **todos os padrões implementados** (combinar com roteiro das Fases B–F).

### Critérios de avaliação (PDF)

- Esta fase não corresponde a um dos pesos “Modelo / Basic / Identification / Lifecycle / Extension”; ela sustenta a **comprovação experimental** exigida pelo trabalho e o **critério de apresentação**.  
- Vale uma subseção “Validação experimental” ou “Avaliação de desempenho” no relatório final.
