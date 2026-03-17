package ConputadorDeBordo.Informacoes.Estrategias;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import ConputadorDeBordo.Informacoes.InformacoesInterface;
import Shared.Message;
import Shared.Service;

public class UDPInformacoes implements InformacoesInterface {
    private DatagramSocket serverSocket;
    private String serverPort;
    
    public UDPInformacoes(String serverPort) {
        try {
            this.serverSocket = new DatagramSocket(Integer.parseInt(serverPort));
            this.serverPort = serverPort;
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        startHeartBeat();
        System.out.println("UDP Informacoes Started");
        while (true) {
            try {
                byte[] receiveMessage = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveMessage, receiveMessage.length);
                this.serverSocket.receive(receivePacket);
        
                byte[] data = receivePacket.getData();
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                ObjectInputStream is = new ObjectInputStream(in);
        
                Message msg = (Message) is.readObject(); // msg.getValue()
                if (msg.getType() == 1) {
                    getVelocidade();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
            
    }

    public void getVelocidade() {
        System.out.println("Velocidade: 100 km/h");
    }

    public void startHeartBeat() {
        new Thread(() -> {
            try {
                String gatewayHost = System.getenv("GATEWAY_HOST");
                if (gatewayHost == null || gatewayHost.isEmpty()) gatewayHost = "localhost";
                String gatewayPortStr = System.getenv("GATEWAY_PORT");
                if (gatewayPortStr == null || gatewayPortStr.isEmpty()) gatewayPortStr = "9003";
                int gatewayPort = Integer.parseInt(gatewayPortStr);

                String nodeName = System.getenv("NODE_NAME");
                if (nodeName == null || nodeName.isEmpty()) nodeName = "localhost";

                InetAddress inetAddress = resolveGatewayHost(gatewayHost, 15, 2000);

                DatagramSocket clientSocket = new DatagramSocket();
                Service service = new Service(nodeName, serverPort);

                while (true) {
                    Message msg = new Message(2, service.getUrl());

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ObjectOutputStream os = new ObjectOutputStream(outputStream);
                    os.writeObject(msg);
                    byte[] data = outputStream.toByteArray();

                    DatagramPacket sendPacket = new DatagramPacket(
                            data, data.length, inetAddress, gatewayPort);

                    clientSocket.send(sendPacket);
                    System.out.println("HeartBeat enviado: " + service.getUrl());

                    Thread.sleep(1000);
                }
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private InetAddress resolveGatewayHost(String gatewayHost, int maxTries, long delayMs) throws UnknownHostException, InterruptedException {
        UnknownHostException lastException = null;
        for (int i = 0; i < maxTries; i++) {
            try {
                return InetAddress.getByName(gatewayHost);
            } catch (UnknownHostException e) {
                lastException = e;
                if (i < maxTries - 1) {
                    System.out.println("Aguardando resolucao de '" + gatewayHost + "' (tentativa " + (i + 1) + "/" + maxTries + ")");
                    Thread.sleep(delayMs);
                }
            }
        }
        System.out.println("GATEWAY_HOST '" + gatewayHost + "' nao resolvido apos " + maxTries + " tentativas, usando localhost");
        return InetAddress.getByName("localhost");
    }
}
