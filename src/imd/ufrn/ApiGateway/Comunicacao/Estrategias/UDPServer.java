package imd.ufrn.ApiGateway.Comunicacao.Estrategias;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import imd.ufrn.ApiGateway.Comunicacao.ServerContract;
import imd.ufrn.Shared.Message;

public class UDPServer implements ServerContract {
	private DatagramSocket serverSocket;

	public UDPServer(String serverPort) {
		try {
			this.serverSocket = new DatagramSocket(Integer.parseInt(serverPort));
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		System.out.println("UDP Server Started");
		try {
			while (true) {
				byte[] receiveMessage = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveMessage, receiveMessage.length);
				this.serverSocket.receive(receivePacket);
								
				byte[] data = receivePacket.getData();
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				ObjectInputStream is = new ObjectInputStream(in);
				try {
					Message msg = (Message) is.readObject();
					System.out.println("Msg recebida com tipo de operação = "+msg.getType()+", e conteudo:"+msg.getContent());
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					ObjectOutputStream os = new ObjectOutputStream(outputStream);
					os.writeObject(msg);
					byte[] dataSend = outputStream.toByteArray();
					InetAddress inetAddress = InetAddress.getByName("localhost");
					DatagramPacket sendPacket = new DatagramPacket(
								dataSend, dataSend.length,	inetAddress, 9004);
					this.serverSocket.send(sendPacket);
				} catch (ClassNotFoundException e) {
				e.printStackTrace();
				}
				
			}
		}catch (IOException e) {
				e.printStackTrace();
				System.out.println("UDP Server Terminating");		
		}
	}
}
