package ValidacaoServer.Server.Estrategias;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ValidacaoServer.Server.ValidacaoServerInterface;
import ValidacaoServer.Validador.Implementacao.Validador;
import ValidacaoServer.Validador.ValidacaoResult;
import middleware.remoting.InvocationContext;
import middleware.remoting.MarshallingRemotingException;
import middleware.remoting.extension.InterceptorChain;
import middleware.remoting.identification.ObjectIds;
import middleware.remoting.invocation.InvokerFactories;
import middleware.remoting.lifecycle.LazyHolder;
import middleware.remoting.marshal.ValidationSemicolonMarshaller;
import middleware.remoting.worker.ValidationWorkerInvoker;
import middleware.shared.HttpMinimalParser;
import middleware.shared.Service;

public class TCPValidacaoServer implements ValidacaoServerInterface {

    private int serverPort;
    private ServerSocket serverSocket;
    private static final int BUFFER = 1024;
    private static final int MAX_BODY = BUFFER * 16;
    private static final int GATEWAY_TIMEOUT_MS = 3000;
    /** Per-Request Instance do validador (Fase E). */
    private final middleware.remoting.Invoker invoker =
            InvokerFactories.perRequestInvoker(Validador::new, Validador.class);
    private final LazyHolder<ValidationSemicolonMarshaller> marshaller =
            new LazyHolder<>(ValidationSemicolonMarshaller::new);
    private final InterceptorChain interceptorChain = InterceptorChain.defaults();
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
            } catch (IOException ignored) {
                // ignore
            }
        }
    }

    private record ProcessamentoHttp(int status, String corpo) {
    }

    private ProcessamentoHttp processar(String message) {
        InvocationContext ctx = InvocationContext.newCorrelation();
        try {
            Object raw = ValidationWorkerInvoker.invoke(message, ctx, invoker, marshaller.get(), interceptorChain);
            ValidacaoResult r = (ValidacaoResult) raw;
            String corpo = "Confirmo Recebimento de:" + r.mensagem();
            if (r.dentroDoIntervalo()) {
                return new ProcessamentoHttp(200, corpo);
            }
            return new ProcessamentoHttp(422, corpo);
        } catch (MarshallingRemotingException e) {
            return new ProcessamentoHttp(400, "Confirmo Recebimento de:erro;formato_invalido;esperado");
        } catch (Exception e) {
            return new ProcessamentoHttp(400, "Um erro ocorreu durante a operação");
        }
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void startHeartBeat() {
        new Thread(() -> {
            String gatewayHost = "localhost";
            int gatewayPort = Integer.getInteger("distribuida.gateway.port", 9003);
            Service service = new Service("localhost", String.valueOf(serverPort), "Validacao");
            String oid = ObjectIds.deterministic("localhost", serverPort, "Validacao").value();
            service.setObjectId(oid);

            while (true) {
                try {
                    String msg = "Quack;Validacao;" + service.getUrl() + ";" + oid;
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
