package ValidacaoServer.Server.Templates;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import Shared.Service;
import ValidacaoServer.Server.ValidacaoServerInterface;

public class ValidacaoServerTemplate implements ValidacaoServerInterface{
    private int serverPort;

    public void start(){
        System.out.println("InformacoesTemplate Started");
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void startHeartBeat() {
        new Thread(() -> {
            try {
                String gatewayHost = "localhost";
                int gatewayPort = 9003;

                InetAddress inetAddress = InetAddress.getByName(gatewayHost);

                try (DatagramSocket clientSocket = new DatagramSocket()) {
                    Service service = new Service("localhost", String.valueOf(serverPort));

                    while (true) {
                        String msg = ("Quack;" + service.getName() + ";" + service.getPort());
                        byte[] data = msg.getBytes(StandardCharsets.UTF_8);

                        DatagramPacket sendPacket = new DatagramPacket(
                                data, data.length, inetAddress, gatewayPort);

                        clientSocket.send(sendPacket);
                        System.out.println("HeartBeat enviado: " + service.getUrl());

                        Thread.sleep(1000);
                    }
                }
            } catch (IOException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }).start();
    }

}
