package imd.ufrn.ConputadorDeBordo.Informacoes.Estrategias;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import imd.ufrn.ConputadorDeBordo.Informacoes.InformacoesInterface;
import imd.ufrn.Shared.Message;

public class UDPInformacoes implements InformacoesInterface {
    private DatagramSocket serverSocket;
    
    public UDPInformacoes(String serverPort) {
        try {
            this.serverSocket = new DatagramSocket(Integer.parseInt(serverPort));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void start(){
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
                } else if (msg.getType() == 2) {
                    heartBeat();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
            
    }

    public void getVelocidade() {
        System.out.println("Velocidade: 100 km/h");
    }
    public void heartBeat() {
        System.out.println("Heart Beat: 100");
    }
}
