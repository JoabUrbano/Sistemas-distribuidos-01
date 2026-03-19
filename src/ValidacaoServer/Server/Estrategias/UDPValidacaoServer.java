package ValidacaoServer.Server.Estrategias;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import Shared.Message;
import ValidacaoServer.Server.Templates.ValidacaoServerTemplate;
import ValidacaoServer.Validador.Implementacao.Validador;

public class UDPValidacaoServer extends ValidacaoServerTemplate {
    private DatagramSocket serverSocket;
    
    public UDPValidacaoServer(int serverPort) {
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
                    Validador validator = new Validador();
                    boolean isValid = validator.validarVelocidade(Integer.parseInt(msg.getContent()));
                    if (isValid) {
                        System.out.println("Velocidade valida");
                    } else {
                        System.out.println("Velocidade invalida");
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
            
    }

}
