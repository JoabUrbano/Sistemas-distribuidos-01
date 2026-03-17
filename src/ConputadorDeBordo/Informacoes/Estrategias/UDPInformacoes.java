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
                DatagramSocket clientSocket = new DatagramSocket();
                InetAddress inetAddress = InetAddress.getByName("localhost");
                Service service = new Service("localhost", serverPort);
    
                while (true) {
                    Message msg = new Message(2, service.getUrl());
    
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ObjectOutputStream os = new ObjectOutputStream(outputStream);
                    os.writeObject(msg);
                    byte[] data = outputStream.toByteArray();
    
                    DatagramPacket sendPacket = new DatagramPacket(
                            data, data.length, inetAddress, 9003);
    
                    clientSocket.send(sendPacket);
                    System.out.println("HeartBeat enviado: " + service.getUrl());
    
                    Thread.sleep(1000);
                }
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}
