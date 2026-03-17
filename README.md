Projeto de distribuida

## Docker

Suba todos os serviços:

```bash
docker compose up --build -d
```

- **ApiGateway**: escuta UDP na porta **9003** (exposta no host).
- **Computadores de bordo**: computador1 (9004), computador2 (9005), computador3 (9006).

Gateway: localhost:9003

Parar:

```bash
docker compose down
```