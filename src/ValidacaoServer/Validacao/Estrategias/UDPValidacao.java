package ValidacaoServer.Validacao.Estrategias;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import Shared.Message;
import ValidacaoServer.Validacao.Templates.InformacoesTemplate;

public class UDPValidacao extends InformacoesTemplate {
    private DatagramSocket serverSocket;
    
    public UDPValidacao(int serverPort) {
        try {
            this.serverSocket = new DatagramSocket(serverPort);
            super.setServerPort(serverPort);
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

}
