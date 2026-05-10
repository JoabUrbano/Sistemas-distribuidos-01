# JMeter — teste de carga (Fase G)

Plano de exemplo: `distribuida-gateway-post.jmx` na mesma pasta.

## Como rodar

1. Suba gateway (9003), sensor (9010) e validação (9005).
2. Abra o `.jmx` no JMeter ou linha de comando:
   `jmeter -n -t docs/jmeter/distribuida-gateway-post.jmx -l resultados.jtl`
3. Ajuste threads, ramp-up e corpo POST conforme o roteiro acordado com o professor.

## Capacidade (knee e usable)

Defina no relatório:

- **Métricas**: ex. throughput (req/s), latência média/p95, % de erro HTTP.
- **Knee capacity**: faixa de carga onde a latência ou os erros começam a degradar de forma acentuada (joelho da curva).
- **Usable capacity**: maior carga em que o sistema ainda atende um **SLO** escolhido (ex.: taxa de erro menor que 1% e p95 menor que 500 ms).

Registre hardware/OS da máquina de teste para reprodutibilidade.
