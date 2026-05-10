package middleware.remoting.gateway;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Optional;

import middleware.gateway.template.ServerTemplate;
import middleware.remoting.InvocationContext;
import middleware.remoting.extension.InterceptorChain;
import middleware.shared.HttpMinimalParser;
import middleware.shared.HttpMinimalParser.HttpTextResponse;
import middleware.shared.Service;

/**
 * Broker (Fase C): centraliza decisão Quack vs encaminhamento sensor → validação.
 */
public final class GatewayBroker {

    private final ServerTemplate registry;
    private final InterceptorChain interceptors;
    private final int maxBodyBytes;
    private final int backendTimeoutMs;

    public GatewayBroker(ServerTemplate registry, InterceptorChain interceptors, int maxBodyBytes, int backendTimeoutMs) {
        this.registry = registry;
        this.interceptors = interceptors;
        this.maxBodyBytes = maxBodyBytes;
        this.backendTimeoutMs = backendTimeoutMs;
    }

    public record DispatchOutcome(Integer httpStatus, String bodyUtf8, boolean quackAck) {
        public static DispatchOutcome quack() {
            return new DispatchOutcome(null, "", true);
        }
    }

    public DispatchOutcome dispatch(InvocationContext ctx, String payload) throws Exception {
        return (DispatchOutcome) interceptors.execute(ctx, () -> dispatchInner(payload));
    }

    private Object dispatchInner(String payload) throws IOException {
        try {
            if (payload == null || payload.trim().isEmpty()) {
                return new DispatchOutcome(400, "erro;payload_vazio", false);
            }
            Optional<Service> quack = QuackMessage.tryParse(payload);
            if (quack.isPresent()) {
                Service service = quack.get();
                String tipo = service.getType();
                if ("Validacao".equals(tipo)) {
                    registry.addServiceValidacao(service);
                } else if ("Sensoriamento".equals(tipo)) {
                    registry.addServiceSensoriamento(service);
                }
                return DispatchOutcome.quack();
            }

            Service sensor = registry.getRandomServiceSensoriamento();
            if (sensor == null) {
                return new DispatchOutcome(503, "Erro: Nenhum serviço de sensoriamento disponível", false);
            }

            HttpTextResponse sensorResp = HttpMinimalParser.postPlainTextReadFullResponse(
                    sensor.getName(),
                    Integer.parseInt(sensor.getPort()),
                    payload,
                    maxBodyBytes,
                    backendTimeoutMs);

            if (sensorResp.statusCode() < 200 || sensorResp.statusCode() > 299) {
                return new DispatchOutcome(502,
                        "Confirmo Recebimento de:erro;sensoriamento_http_" + sensorResp.statusCode() + ";"
                                + sensorResp.body(),
                        false);
            }

            Service validacao = registry.getRandomServiceValidacao();
            if (validacao == null) {
                return new DispatchOutcome(503, "Erro: Nenhum serviço de validação disponível", false);
            }

            HttpTextResponse validacaoResp = HttpMinimalParser.postPlainTextReadFullResponse(
                    validacao.getName(),
                    Integer.parseInt(validacao.getPort()),
                    sensorResp.body(),
                    maxBodyBytes,
                    backendTimeoutMs);

            return new DispatchOutcome(validacaoResp.statusCode(), validacaoResp.body(), false);

        } catch (SocketTimeoutException e) {
            return new DispatchOutcome(504, "Confirmo Recebimento de:erro;timeout_backend;9004", false);
        } catch (IOException e) {
            return new DispatchOutcome(502, "Confirmo Recebimento de:erro;gateway;" + e.getMessage(), false);
        }
    }
}
