# Fase A — Estrutura de módulos e artefato JAR da plataforma

## Objetivo

Preparar a entrega e o desenvolvimento posteriores conforme o enunciado: **projeto da aplicação separado** do **projeto da plataforma de middleware**, com a aplicação dependendo de uma **biblioteca (.jar)** que contém o middleware. Esta fase é quase toda **organização de build** e limites de pacotes; o comportamento observável (HTTP no gateway, fluxo sensor → validação, JMeter) deve **permanecer equivalente** ao que já funciona.

## Contexto no seu repositório

Hoje o código mistura o papel de “plataforma” (HTTP mínimo, fábricas, protocolos) com o papel de “domínio” (sensoriamento, validação). A Fase A não exige ainda anotações nem todos os padrões do livro; ela cria o **esqueleto** para que as fases seguintes implementem padrões **dentro** do módulo middleware e mantenham a aplicação como **cliente** dessa API.

## O que fazer (passo a passo conceitual)

1. **Definir o módulo `middleware` (ou nome equivalente)**  
   - Pacotes sugeridos: API pública (contratos), implementação interna (transporte, despacho futuro), talvez `spi` para plug-ins de protocolo na Fase F.  
   - Tudo que for “genérico remoting” deve tender a morar aqui.

2. **Definir o módulo `aplicacao` (ou dois mains: gateway-app, worker-app)**  
   - Contém apenas o que é caso de uso do trabalho: registrar serviços de sensoriamento e validação, classes de negócio, pontos de entrada `main`.  
   - Depende do JAR do middleware.

3. **Configurar o build**  
   - Com Maven: dois `pom.xml` em multimódulo ou um multi-módulo `parent` com `<modules>`.  
   - Com Gradle: `settings.gradle` com subprojetos.  
   - O artefato publicado para a app é o JAR do middleware (classifier “plain” ou somente API + impl empacotada, conforme decisão do grupo).

4. **Migrar código sem mudar comportamento**  
   - Mover classes compartilhadas realmente genéricas (`HttpMinimalParser`, contratos futuros) para o middleware.  
   - Manter portas e formato de mensagens iguais para não invalidar JMeter.  
   - Evitar refatoração funcional grande nesta fase; prefira **moves** e ajustes de pacote.

5. **Documentar como rodar**  
   - Ordem de subida: gateway → workers → JMeter (como hoje).  
   - Variáveis de ambiente ou arquivo de config para host/porta (opcional nesta fase, mas recomendado antes da Fase G).

## Critérios de conclusão da fase

- O middleware compila para um **JAR** consumível.
- A aplicação compila **importando** esse JAR (dependência explícita no build).
- Execução manual (ou script) reproduz o fluxo atual com **mesmos endpoints/payloads** usados no JMeter.
- Estrutura de pastas reflete a separação “plataforma” vs “aplicação” de forma clara para o professor avaliar.

## Riscos e mitigação

- **Risco**: quebrar imports e mains. **Mitigação**: migrar em commits pequenos; rodar os três processos após cada passo.  
- **Risco**: duplicar classes entre módulos. **Mitigação**: uma única fonte de verdade no middleware para utilitários compartilhados.

---

## Pontos do relatório cobertos por esta fase

Preencha o relatório com base nesta lista quando a Fase A estiver concluída.

### Enunciado (requisitos textuais)

- **Projeto da aplicação separado do projeto da plataforma de middleware** — estrutura de repositório e build documentados.  
- **Importação de biblioteca (.jar) contendo a plataforma** — dependência declarada e JAR gerado (descrever coordenadas Maven/Gradle ou caminho do artefato).  
- **Base para manter a mesma aplicação da primeira unidade** — justificar que o comportamento funcional foi preservado após a separação.

### Critérios de avaliação (PDF)

- Esta fase **prepara** todos os critérios seguintes, mas **não pontua sozinha** nos itens “Modelo de Componentes”, “Basic Remoting”, etc. Vale registrar como **fundamento da arquitetura** e como **organização do trabalho**.  
- **Apresentação / execução**: facilita mostrar “aqui está o JAR da plataforma; aqui está a app que o usa”.

### Itens que normalmente não ficam completos só na Fase A

- Modelo de componentes por anotações (Fase B).  
- Lista explícita de padrões Remoting no código (Fases C–F).  
- JMeter e knee/usable capacity (Fase G) — apenas mencionar planejamento se ainda não executado.

---

## Estado da implementação no repositório

Implementado conforme esta fase:

- **Pai Maven**: `pom.xml` na raiz (`distribuida-parent`).
- **Middleware**: módulo `middleware-platform/` — pacotes `middleware.shared` e `middleware.gateway.*`.
- **Aplicação**: módulo `distribuida-app/` — dependência Maven em `middleware-platform`; bootstrap do gateway em `distribuida.app.GatewayMain`; pacotes `Sensoriamento` e `ValidacaoServer` preservados.
- **Coordenadas**: `groupId` `distribuida`, `version` `1.0-SNAPSHOT`.

Instruções de build e execução: `README.md` na raiz do projeto.
