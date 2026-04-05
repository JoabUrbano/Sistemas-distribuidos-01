package Sensoriamento.Server.Estrategias;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Sensoriamento.Sensoriamento.Sensoriamento;
import Sensoriamento.Server.SensoriamentoContrato;
import Shared.HttpMinimalParser;
import Shared.Service;

public class TCPSensoriamentoServer implements SensoriamentoContrato {

    private final int serverPort;
    private ServerSocket serverSocket;
    private static final int BUFFER = 1024;
    private static final int MAX_BODY = BUFFER * 16;
    private static final int GATEWAY_TIMEOUT_MS = 3000;
    private final Sensoriamento sensoriamento = new Sensoriamento();
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public TCPSensoriamentoServer(int serverPort) {
        this.serverPort = serverPort;
        try {
            this.serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        startHeartBeat();
        System.out.println("TCP (HTTP) Sensoriamento Server Started on port " + serverPort);
        try {
            while (true) {
                Socket client = serverSocket.accept();
                executor.submit(() -> atenderClienteHttp(client));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void atenderClienteHttp(Socket socket) {
        try {
            String payload = HttpMinimalParser.readPostBodyUtf8(socket, MAX_BODY);
            if (payload == null) {
                HttpMinimalParser.writeTextHttpResponse(socket, 400, "TCPSensoriamentoServer", "erro;requisicao_vazia");
                return;
            }
            if (payload.isEmpty()) {
                HttpMinimalParser.writeTextHttpResponse(socket, 400, "TCPSensoriamentoServer",
                        "erro;corpo_vazio;esperado_post_com_content_length");
                return;
            }

            String resposta = processar(payload);
            HttpMinimalParser.writeTextHttpResponse(socket, 200, "TCPSensoriamentoServer", resposta);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    private String processar(String message) {
        String resultadoOp = message;
        try {
            String[] p = message.split(";", 3);
            if (p.length < 3) {
                return "erro;formato_invalido;esperado";
            }
            String operacao = p[0].trim();

            if (operacao.equals("temperatura")) {
                resultadoOp = String.valueOf(sensoriamento.geTemperatura());
            } else if (operacao.equals("velocidade")) {
                resultadoOp = String.valueOf(sensoriamento.getVelocidade());
            }
            return resultadoOp + ";" + p[1].trim() + ";" + p[2].trim();
        } catch (NumberFormatException nfe) {
            return "Um erro ocorreu durante a operação";
        }
    }

    public void startHeartBeat() {
        new Thread(() -> {
            String gatewayHost = "localhost";
            int gatewayPort = 9003;
            Service service = new Service("localhost", String.valueOf(serverPort), "Sensoriamento");

            while (true) {
                try {
                    String msg = "Quack;Sensoriamento;" + service.getUrl();
                    HttpMinimalParser.postPlainTextReadBody(gatewayHost, gatewayPort, msg, BUFFER, GATEWAY_TIMEOUT_MS);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }
}
