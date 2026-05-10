package middleware.gateway.estrategias;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import middleware.gateway.template.ServerTemplate;
import middleware.remoting.gateway.QuackMessage;
import middleware.remoting.protocol.GatewayTransport;
import middleware.remoting.ServerRequestHandler;
import middleware.shared.Service;

public class UDPServer extends ServerTemplate implements GatewayTransport, ServerRequestHandler {
    private DatagramSocket serverSocket;
    private static final int BUFFER = 1024;

    public UDPServer(int serverPort) {
        System.out.println("UDP Server");
        try {
            this.serverSocket = new DatagramSocket(serverPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private final ExecutorService virtualThreads = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public void start() {
        clearServices();
        System.out.println("UDP Server Started");
        try {
            while (true) {
                byte[] buf = new byte[BUFFER];
                DatagramPacket clientPacket = new DatagramPacket(buf, buf.length);
                serverSocket.receive(clientPacket);

                String payload = new String(clientPacket.getData(), 0, clientPacket.getLength()).trim();
                InetAddress clientAddr = clientPacket.getAddress();
                int clientPort = clientPacket.getPort();

                virtualThreads.submit(() -> encaminharEResponder(serverSocket, payload, clientAddr, clientPort));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("UDP Server Terminating");
        }
    }

    private void encaminharEResponder(DatagramSocket gatewaySocket, String payload, InetAddress clientAddr, int clientPort) {
        try (DatagramSocket upstream = new DatagramSocket()) {
            upstream.setSoTimeout(200);

            Optional<Service> registro = QuackMessage.tryParse(payload);
            if (registro.isPresent()) {
                Service service = registro.get();
                if ("Validacao".equals(service.getType())) {
                    addServiceValidacao(service);
                } else if ("Sensoriamento".equals(service.getType())) {
                    addServiceSensoriamento(service);
                }
                System.out.println("[lookup-udp] registrado AOR=" + service.toAbsoluteReference());
                return;
            }

            Service serviceSensoriamento = this.getRandomServiceSensoriamento();
            if (serviceSensoriamento == null) {
                enviarErro(gatewaySocket, clientAddr, clientPort, "Erro: Nenhum serviço de sensoriamento disponível");
                return;
            }

            byte[] req = payload.getBytes();
            InetAddress serviceSensoriamentoAddress = InetAddress.getByName(serviceSensoriamento.getName());
            DatagramPacket toBackendSensoriamento = new DatagramPacket(req, req.length,
                    serviceSensoriamentoAddress, Integer.parseInt(serviceSensoriamento.getPort()));

            upstream.send(toBackendSensoriamento);

            byte[] respBuf = new byte[BUFFER];
            DatagramPacket fromSensoriamento = new DatagramPacket(respBuf, respBuf.length);
            upstream.receive(fromSensoriamento);

            String sensoriamentoReply = new String(
                    fromSensoriamento.getData(), 0, fromSensoriamento.getLength());

            Service serviceValidacao = this.getRandomServiceValidacao();
            if (serviceValidacao == null) {
                enviarErro(gatewaySocket, clientAddr, clientPort, "Erro: Nenhum serviço de validação disponível");
                return;
            }

            byte[] reqValidacao = sensoriamentoReply.getBytes();
            InetAddress serviceValidacaoAddress = InetAddress.getByName(serviceValidacao.getName());
            DatagramPacket toBackendValidacao = new DatagramPacket(reqValidacao, reqValidacao.length,
                    serviceValidacaoAddress, Integer.parseInt(serviceValidacao.getPort()));

            upstream.send(toBackendValidacao);

            byte[] respBufValidacao = new byte[BUFFER];
            DatagramPacket fromValidacao = new DatagramPacket(respBufValidacao, respBufValidacao.length);
            upstream.receive(fromValidacao);

            int respostaLen = fromValidacao.getLength();
            DatagramPacket paraCliente = new DatagramPacket(
                    respBufValidacao, 0, respostaLen, clientAddr, clientPort);

            synchronized (gatewaySocket) {
                gatewaySocket.send(paraCliente);
            }

        } catch (SocketTimeoutException e) {
            enviarErro(gatewaySocket, clientAddr, clientPort, "Confirmo Recebimento de:erro;timeout_backend;9004");
        } catch (IOException e) {
            enviarErro(gatewaySocket, clientAddr, clientPort,
                    "Confirmo Recebimento de:erro;gateway;" + e.getMessage());
        }
    }

    private void enviarErro(DatagramSocket gatewaySocket, InetAddress clientAddr, int clientPort, String msg) {
        try {
            byte[] b = msg.getBytes();
            DatagramPacket p = new DatagramPacket(b, b.length, clientAddr, clientPort);
            synchronized (gatewaySocket) {
                gatewaySocket.send(p);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
