package Sensoriamento.Server.Estrategias;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
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
import middleware.shared.Service;

public class UDPSensoriamentoServer implements SensoriamentoContrato {

    private int serverPort;
    private DatagramSocket serverSocket;
    private static final int BUFFER = 1024;
    private final Sensoriamento sensoriamento = new Sensoriamento();
    private final middleware.remoting.Invoker invoker =
            InvokerFactories.staticInvoker(sensoriamento, Sensoriamento.class);
    private final LazyHolder<SensorSemicolonMarshaller> marshaller =
            new LazyHolder<>(SensorSemicolonMarshaller::new);
    private final InterceptorChain interceptorChain = InterceptorChain.defaults();
    private final ExecutorService virtualThreads = Executors.newVirtualThreadPerTaskExecutor();

    public UDPSensoriamentoServer(int serverPort) {
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
        System.out.println("UDP Sensoriamento Server Started");
        try {
            while (true) {
                byte[] buf = new byte[BUFFER];
                DatagramPacket in = new DatagramPacket(buf, buf.length);
                serverSocket.receive(in);
                String raw = new String(in.getData(), 0, in.getLength()).trim();
                InetAddress clientAddr = in.getAddress();
                int clientPort = in.getPort();

                virtualThreads.submit(() -> responder(clientAddr, clientPort, raw));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void responder(InetAddress clientAddr, int clientPort, String raw) {
        try {
            String reply = processar(raw);
            byte[] out = reply.getBytes(StandardCharsets.UTF_8);
            DatagramPacket resp = new DatagramPacket(out, out.length, clientAddr, clientPort);
            synchronized (serverSocket) {
                serverSocket.send(resp);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                    Service service = new Service("localhost", String.valueOf(serverPort), "Sensoriamento");
                    String oid = ObjectIds.deterministic("localhost", serverPort, "Sensoriamento").value();
                    service.setObjectId(oid);

                    while (true) {
                        String msg = "Quack;Sensoriamento;" + service.getUrl() + ";" + oid;
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
