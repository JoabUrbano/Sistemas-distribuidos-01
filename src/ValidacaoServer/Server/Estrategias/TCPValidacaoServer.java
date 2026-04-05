package ValidacaoServer.Server.Estrategias;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Shared.HttpMinimalParser;
import Shared.Service;
import ValidacaoServer.Server.Templates.ValidacaoServerTemplate;
import ValidacaoServer.Validador.Implementacao.Validador;
import ValidacaoServer.Validador.ValidacaoResult;

public class TCPValidacaoServer extends ValidacaoServerTemplate {

    private int serverPort;
    private ServerSocket serverSocket;
    private static final int BUFFER = 1024;
    private static final int MAX_BODY = BUFFER * 16;
    private static final int GATEWAY_TIMEOUT_MS = 3000;
    private final Validador validador = new Validador();
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public TCPValidacaoServer(int serverPort) {
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
        System.out.println("TCP (HTTP) Validacao Server Started on port " + serverPort);
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
                HttpMinimalParser.writeTextHttpResponse(socket, 400, "TCPValidacaoServer", "erro;requisicao_vazia");
                return;
            }
            if (payload.isEmpty()) {
                HttpMinimalParser.writeTextHttpResponse(socket, 400, "TCPValidacaoServer",
                        "erro;corpo_vazio;esperado_post_com_content_length");
                return;
            }

            ProcessamentoHttp resultado = processar(payload);
            HttpMinimalParser.writeTextHttpResponse(socket, resultado.status(), "TCPValidacaoServer", resultado.corpo());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    private record ProcessamentoHttp(int status, String corpo) {
    }

    private ProcessamentoHttp processar(String message) {
        try {
            String[] p = message.split(";", 3);
            if (p.length < 3) {
                return new ProcessamentoHttp(400, "Confirmo Recebimento de:erro;formato_invalido;esperado");
            }
            int valor = Integer.parseInt(p[0].trim());
            int valorMinimo = Integer.parseInt(p[1].trim());
            int valorMaximo = Integer.parseInt(p[2].trim());

            ValidacaoResult r = validador.validarComResultado(valor, valorMinimo, valorMaximo);
            String corpo = "Confirmo Recebimento de:" + r.mensagem();
            if (r.dentroDoIntervalo()) {
                return new ProcessamentoHttp(200, corpo);
            }
            return new ProcessamentoHttp(422, corpo);
        } catch (NumberFormatException nfe) {
            return new ProcessamentoHttp(400, "Um erro ocorreu durante a operação");
        }
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void startHeartBeat() {
        new Thread(() -> {
            String gatewayHost = "localhost";
            int gatewayPort = 9003;
            Service service = new Service("localhost", String.valueOf(serverPort), "Validacao");

            while (true) {
                try {
                    String msg = "Quack;Validacao;" + service.getUrl();
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
