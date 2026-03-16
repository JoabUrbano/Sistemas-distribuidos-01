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
import java.sql.Timestamp;

import imd.ufrn.ApiGateway.Comunicacao.ServerContract;
import imd.ufrn.Shared.Message;
import imd.ufrn.Shared.Service;

public class UDPServer implements ServerContract {
	private DatagramSocket serverSocket;
	private Service[] services = new Service[0];

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
					if (msg.getType() == 1) {
						sendMessage(msg);
					} else if (msg.getType() == 2) {
						String[] serviceSended = msg.getContent().split(":");
						Service service = new Service(serviceSended[0], serviceSended[1], new Timestamp(System.currentTimeMillis()));
						addService(service);
					}
				} catch (ClassNotFoundException e) {
				e.printStackTrace();
				}
				
			}
		}catch (IOException e) {
				e.printStackTrace();
				System.out.println("UDP Server Terminating");		
		}
	}

	public void sendMessage(Message msg) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(outputStream);
			os.writeObject(msg);
			byte[] data = outputStream.toByteArray();
			InetAddress inetAddress = InetAddress.getByName("localhost");
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, inetAddress, 9004);
			this.serverSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("UDP Server Terminating");
		}
	}

	public void addService(Service service) {
	
		for (Service s : services) {
			if (s.getName().equals(service.getName()) && s.getPort().equals(service.getPort())) {
				s.setUltimoHeartBeat(new Timestamp(System.currentTimeMillis()));
				System.out.println("Serviço: "+s.getUrl()+" atualizado");
				System.out.println("Ultimo HeartBeat: "+s.getUltimoHeartBeat());
				return;
			}
		}
		System.out.println("Serviço: "+service.getUrl()+" adicionado");
		
		Service[] newServices = new Service[services.length + 1];
		for (int i = 0; i < services.length; i++) {
			newServices[i] = services[i];
		}
		newServices[services.length] = service;
		services = newServices;
	}

	public void removeService(Service service) {
		Service[] newServices = new Service[services.length - 1];
		for (int i = 0; i < services.length; i++) {
			if (services[i].getName().equals(service.getName())) {
				continue;
			}
			newServices[i] = services[i];
		}
		services = newServices;
	}

	public Service[] getServices() {
		return services;
	}
}
