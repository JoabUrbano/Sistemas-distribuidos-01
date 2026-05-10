# Plano de implementação do middleware (Remoting Patterns)

Este diretório descreve **uma fase por arquivo**. Execute as fases na ordem indicada; cada documento explica objetivos, escopo, critérios de conclusão e **o que cobrir no relatório** ao terminar a fase.

## Ordem sugerida

| Ordem | Arquivo | Tema breve |
|-------|---------|------------|
| 1 | [FASE-A-modulos-e-jar.md](FASE-A-modulos-e-jar.md) | Separação middleware / aplicação e artefato JAR |
| 2 | [FASE-B-modelo-componentes.md](FASE-B-modelo-componentes.md) | Anotações e despacho HTTP sobre objetos remotos |
| 3 | [FASE-C-broker-basic-remoting.md](FASE-C-broker-basic-remoting.md) | Broker e padrões básicos de remoting |
| 4 | [FASE-D-identification.md](FASE-D-identification.md) | Lookup, Object Id, Absolute Object Reference |
| 5 | [FASE-E-lifecycle.md](FASE-E-lifecycle.md) | Dois padrões de instância + dois de recurso |
| 6 | [FASE-F-extension-patterns.md](FASE-F-extension-patterns.md) | Interceptor, contexto, plug-in de protocolo |
| 7 | [FASE-G-jmeter-capacidade.md](FASE-G-jmeter-capacidade.md) | Testes de carga e análise de capacidade |

## Relação com o enunciado

O trabalho referencia o livro *Remoting Patterns* e exige modelo por anotações, conjunto específico de padrões, JMeter e discussão de capacidade (*knee* e *usable*). A divisão em fases organiza esses requisitos para implementação incremental **sem perder** o comportamento já validado com JMeter (contratos HTTP e fluxo gateway → sensor → validação).

## Como usar

1. Leia o índice e abra apenas a **próxima** fase em aberto.
2. Ao concluir a fase no código, marque os itens de verificação do arquivo correspondente e atualize o relatório com os pontos listados ao final do arquivo.
