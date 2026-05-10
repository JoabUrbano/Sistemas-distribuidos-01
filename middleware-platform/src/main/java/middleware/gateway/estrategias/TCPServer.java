package middleware.gateway.estrategias;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import middleware.gateway.template.ServerTemplate;
import middleware.remoting.InvocationContext;
import middleware.remoting.extension.InterceptorChain;
import middleware.remoting.gateway.GatewayBroker;
import middleware.remoting.protocol.GatewayTransport;
import middleware.remoting.ServerRequestHandler;
import middleware.shared.HttpMinimalParser;

public class TCPServer extends ServerTemplate implements GatewayTransport, ServerRequestHandler {

    private final ServerSocket serverSocket;
    private static final int BUFFER = 1024;
    private static final int MAX_BODY = BUFFER * 32;
    private static final int BACKEND_TIMEOUT_MS = 1000;
    private final int serverPort;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final GatewayBroker gatewayBroker =
            new GatewayBroker(this, InterceptorChain.defaults(), MAX_BODY, BACKEND_TIMEOUT_MS);

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
            } catch (IOException ignored) {
                // ignore
            }
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
                // ignore
            }
        }
    }

    private GatewayClientResponse processarPayloadGateway(String payload) {
        InvocationContext ctx = InvocationContext.newCorrelation();
        try {
            GatewayBroker.DispatchOutcome out = gatewayBroker.dispatch(ctx, payload);
            if (out.quackAck()) {
                return GatewayClientResponse.quack();
            }
            return new GatewayClientResponse(out.httpStatus(), out.bodyUtf8());
        } catch (Exception e) {
            return new GatewayClientResponse(500, "Confirmo Recebimento de:erro;gateway;" + e.getMessage());
        }
    }
}
