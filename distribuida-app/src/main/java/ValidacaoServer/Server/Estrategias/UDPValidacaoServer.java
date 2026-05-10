package ValidacaoServer.Server.Estrategias;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

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
import middleware.shared.Service;

public class UDPValidacaoServer implements ValidacaoServerInterface {
    private int serverPort;
    private DatagramSocket serverSocket;
    private static final int BUFFER = 1024;
    private final middleware.remoting.Invoker invoker =
            InvokerFactories.perRequestInvoker(Validador::new, Validador.class);
    private final LazyHolder<ValidationSemicolonMarshaller> marshaller =
            new LazyHolder<>(ValidationSemicolonMarshaller::new);
    private final InterceptorChain interceptorChain = InterceptorChain.defaults();

    public UDPValidacaoServer(int serverPort) {
        try {
            this.serverSocket = new DatagramSocket(serverPort);
            this.setServerPort(serverPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        startHeartBeat();
        System.out.println("UDP Validacao Server Started");
        try {
            while (true) {
                byte[] buf = new byte[BUFFER];
                DatagramPacket in = new DatagramPacket(buf, buf.length);
                serverSocket.receive(in);
                String raw = new String(in.getData(), 0, in.getLength()).trim();

                String reply = processar(raw);
                byte[] out = reply.getBytes(StandardCharsets.UTF_8);
                DatagramPacket resp = new DatagramPacket(out, out.length, in.getAddress(), in.getPort());
                serverSocket.send(resp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String processar(String message) {
        InvocationContext ctx = InvocationContext.newCorrelation();
        try {
            Object raw = ValidationWorkerInvoker.invoke(message, ctx, invoker, marshaller.get(), interceptorChain);
            ValidacaoResult r = (ValidacaoResult) raw;
            return "Confirmo Recebimento de:" + r.mensagem();
        } catch (MarshallingRemotingException e) {
            return "Confirmo Recebimento de:erro;formato_invalido;esperado";
        } catch (Exception e) {
            return "Um erro ocorreu durante a operação";
        }
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void startHeartBeat() {
        new Thread(() -> {
            try {
                String gatewayHost = "localhost";
                int gatewayPort = Integer.getInteger("distribuida.gateway.port", 9003);

                InetAddress inetAddress = InetAddress.getByName(gatewayHost);

                try (DatagramSocket clientSocket = new DatagramSocket()) {
                    Service service = new Service("localhost", String.valueOf(serverPort), "Validacao");
                    String oid = ObjectIds.deterministic("localhost", serverPort, "Validacao").value();
                    service.setObjectId(oid);

                    while (true) {
                        String msg = "Quack;Validacao;" + service.getUrl() + ";" + oid;
                        byte[] data = msg.getBytes(StandardCharsets.UTF_8);

                        DatagramPacket sendPacket = new DatagramPacket(
                                data, data.length, inetAddress, gatewayPort);

                        clientSocket.send(sendPacket);

                        Thread.sleep(1000);
                    }
                }
            } catch (IOException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }).start();
    }

}
