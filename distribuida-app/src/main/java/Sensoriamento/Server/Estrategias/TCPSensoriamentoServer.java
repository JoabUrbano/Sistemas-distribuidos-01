package Sensoriamento.Server.Estrategias;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Sensoriamento.Sensoriamento.Sensoriamento;
import Sensoriamento.Server.SensoriamentoContrato;
import middleware.remoting.InvocationContext;
import middleware.remoting.MarshallingRemotingException;
import middleware.remoting.extension.InterceptorChain;
import middleware.remoting.identification.ObjectIds;
import middleware.remoting.invocation.InvokerFactories;
import middleware.remoting.lifecycle.LazyHolder;
import middleware.remoting.marshal.SensorSemicolonMarshaller;
import middleware.remoting.worker.SensorWorkerInvoker;
import middleware.shared.HttpMinimalParser;
import middleware.shared.Service;

public class TCPSensoriamentoServer implements SensoriamentoContrato {

    private final int serverPort;
    private ServerSocket serverSocket;
    private static final int BUFFER = 1024;
    private static final int MAX_BODY = BUFFER * 16;
    private static final int GATEWAY_TIMEOUT_MS = 3000;
    /** Static Instance do objeto remoto (Fase E). */
    private final Sensoriamento sensoriamento = new Sensoriamento();
    private final middleware.remoting.Invoker invoker =
            InvokerFactories.staticInvoker(sensoriamento, Sensoriamento.class);
    /** Lazy Acquisition do marshaller (Fase E). */
    private final LazyHolder<SensorSemicolonMarshaller> marshaller =
            new LazyHolder<>(SensorSemicolonMarshaller::new);
    private final InterceptorChain interceptorChain = InterceptorChain.defaults();
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
            } catch (IOException ignored) {
                // ignore
            }
        }
    }

    private String processar(String message) {
        InvocationContext ctx = InvocationContext.newCorrelation();
        try {
            return SensorWorkerInvoker.invoke(message, ctx, invoker, marshaller.get(), interceptorChain);
        } catch (MarshallingRemotingException e) {
            return "erro;formato_invalido;esperado";
        } catch (Exception e) {
            return "Um erro ocorreu durante a operação";
        }
    }

    public void startHeartBeat() {
        new Thread(() -> {
            String gatewayHost = "localhost";
            int gatewayPort = Integer.getInteger("distribuida.gateway.port", 9003);
            Service service = new Service("localhost", String.valueOf(serverPort), "Sensoriamento");
            String oid = ObjectIds.deterministic("localhost", serverPort, "Sensoriamento").value();
            service.setObjectId(oid);

            while (true) {
                try {
                    String msg = "Quack;Sensoriamento;" + service.getUrl() + ";" + oid;
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
