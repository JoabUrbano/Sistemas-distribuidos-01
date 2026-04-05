package ApiGateway.Comunicacao.Estrategias;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ApiGateway.Comunicacao.Template.ServerTemplate;
import Shared.HttpMinimalParser;
import Shared.HttpMinimalParser.HttpTextResponse;
import Shared.Service;

public class TCPServer extends ServerTemplate {

    private final ServerSocket serverSocket;
    private static final int BUFFER = 1024;
    private static final int MAX_BODY = BUFFER * 32;
    private static final int BACKEND_TIMEOUT_MS = 1000;
    private final int serverPort;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public TCPServer(int serverPort) {
        System.out.println("TCP HTTP Server (gateway)");
        this.serverPort = serverPort;
        try {
            this.serverSocket = new ServerSocket(serverPort, 500);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start() {
        clearServices();
        System.out.println("TCP HTTP Server Started on port " + serverPort);
        try {
            while (true) {
                Socket remote = serverSocket.accept();
                executor.submit(() -> tratarConexaoHttp(remote));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private record GatewayClientResponse(Integer status, String body) {
        static GatewayClientResponse quack() {
            return new GatewayClientResponse(null, null);
        }

        boolean isQuack() {
            return status == null;
        }
    }

    private void tratarConexaoHttp(Socket socket) {
        try {
            String payload = HttpMinimalParser.readPostBodyUtf8(socket, MAX_BODY);
            if (payload == null) {
                HttpMinimalParser.writeTextHttpResponse(socket, 500, "ApiGateway-TCPServer", "erro;requisicao_vazia");
                return;
            }

            GatewayClientResponse outcome = processarPayloadGateway(payload);
            if (outcome.isQuack()) {
                HttpMinimalParser.writeTextHttpResponse(socket, 204, "ApiGateway-TCPServer", "");
            } else {
                HttpMinimalParser.writeTextHttpResponse(socket, outcome.status(), "ApiGateway-TCPServer", outcome.body());
            }
        } catch (IOException e) {
            try {
                HttpMinimalParser.writeTextHttpResponse(socket, 500, "ApiGateway-TCPServer",
                        "Confirmo Recebimento de:erro;gateway;" + e.getMessage());
            } catch (IOException ignored) {}
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    private GatewayClientResponse processarPayloadGateway(String payload) {
        try {
            String[] p = payload.split(";", 3);
            if (p.length == 0) {
                return new GatewayClientResponse(400, "erro;payload_vazio");
            }
            String valor = p[0].trim();
            if (valor.equals("Quack")) {
                if (p.length < 3) {
                    return new GatewayClientResponse(400, "erro;quack_incompleto");
                }
                String[] url = p[2].split(":", 2);
                if (url.length < 2) {
                    return new GatewayClientResponse(400, "erro;url_invalida");
                }
                Service service = new Service(url[0], url[1], p[1], new Timestamp(System.currentTimeMillis()));

                if (p[1].equals("Validacao")) {
                    addServiceValidacao(service);
                } else if (p[1].equals("Sensoriamento")) {
                    addServiceSensoriamento(service);
                }
                return GatewayClientResponse.quack();
            }

            Service serviceSensoriamento = getRandomServiceSensoriamento();
            if (serviceSensoriamento == null) {
                return new GatewayClientResponse(503, "Erro: Nenhum serviço de sensoriamento disponível");
            }

            HttpTextResponse sensorResp = HttpMinimalParser.postPlainTextReadFullResponse(
                    serviceSensoriamento.getName(),
                    Integer.parseInt(serviceSensoriamento.getPort()),
                    payload,
                    MAX_BODY,
                    BACKEND_TIMEOUT_MS);

            if (sensorResp.statusCode() < 200 || sensorResp.statusCode() > 299) {
                return new GatewayClientResponse(502,
                        "Confirmo Recebimento de:erro;sensoriamento_http_" + sensorResp.statusCode() + ";"
                                + sensorResp.body());
            }

            Service serviceValidacao = getRandomServiceValidacao();
            if (serviceValidacao == null) {
                return new GatewayClientResponse(503, "Erro: Nenhum serviço de validação disponível");
            }

            HttpTextResponse validacaoResp = HttpMinimalParser.postPlainTextReadFullResponse(
                    serviceValidacao.getName(),
                    Integer.parseInt(serviceValidacao.getPort()),
                    sensorResp.body(),
                    MAX_BODY,
                    BACKEND_TIMEOUT_MS);

            return new GatewayClientResponse(validacaoResp.statusCode(), validacaoResp.body());

        } catch (SocketTimeoutException e) {
            return new GatewayClientResponse(504, "Confirmo Recebimento de:erro;timeout_backend;9004");
        } catch (IOException e) {
            return new GatewayClientResponse(502, "Confirmo Recebimento de:erro;gateway;" + e.getMessage());
        }
    }
}
