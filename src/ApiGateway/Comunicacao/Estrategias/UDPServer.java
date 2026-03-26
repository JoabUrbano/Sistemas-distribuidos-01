package ApiGateway.Comunicacao.Estrategias;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ApiGateway.Comunicacao.Template.ServerTemplate;
import Shared.Service;

public class UDPServer extends ServerTemplate {
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
				// System.out.println("Payload: " + payload);

				virtualThreads.submit(() -> encaminharEResponder(serverSocket, payload, clientAddr, clientPort));		
			}
		}catch (IOException e) {
				e.printStackTrace();
				System.out.println("UDP Server Terminating");		
		}
	}

	private void encaminharEResponder(DatagramSocket gatewaySocket, String payload, InetAddress clientAddr, int clientPort) {
		try (DatagramSocket upstream = new DatagramSocket()) {
			upstream.setSoTimeout(100);

			String[] p = payload.split(";", 3);
			String valor = p[0].trim();
			if(valor.equals("Quack")) {
				String[] url = p[2].split(":", 2);
				Service service = new Service(url[0], url[1], p[1], new Timestamp(System.currentTimeMillis()));
				
				if(p[1].equals("Validacao")) {
					addServiceValidacao(service);
				} else if(p[1].equals("Sensoriamento")) {
					addServiceSensoriamento(service);
				}

				return;
			}
			Service serviceValidacao = this.getRandomServiceValidacao();

			InetAddress service = InetAddress.getByName(serviceValidacao.getName());
			byte[] req = payload.getBytes();
			DatagramPacket toBackend = new DatagramPacket(req, req.length, service, Integer.parseInt(serviceValidacao.getPort()));
			upstream.send(toBackend);

			byte[] respBuf = new byte[BUFFER];
			DatagramPacket fromBackend = new DatagramPacket(respBuf, respBuf.length);
			upstream.receive(fromBackend);

			String backendReply = new String(fromBackend.getData(), 0, fromBackend.getLength());
			byte[] toClient = backendReply.getBytes();
			DatagramPacket clientReply = new DatagramPacket(toClient, toClient.length, clientAddr, clientPort);

			synchronized (gatewaySocket) {
				gatewaySocket.send(clientReply);
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
