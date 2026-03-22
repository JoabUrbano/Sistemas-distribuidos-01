package ValidacaoServer.Server.Templates;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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

                DatagramSocket clientSocket = new DatagramSocket();
                Service service = new Service("localhost", String.valueOf(serverPort));

                while (true) {
                    //Message msg = new Message(2, service.getUrl());

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ObjectOutputStream os = new ObjectOutputStream(outputStream);
                    //os.writeObject(msg);
                    byte[] data = outputStream.toByteArray();

                    DatagramPacket sendPacket = new DatagramPacket(
                            data, data.length, inetAddress, gatewayPort);

                    clientSocket.send(sendPacket);
                    //System.out.println("HeartBeat enviado: " + service.getUrl());

                    Thread.sleep(1000);
                }
            } catch (IOException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }).start();
    }

}
