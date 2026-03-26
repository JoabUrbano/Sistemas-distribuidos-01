package ValidacaoServer.Server.Estrategias;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

import Shared.Service;
import ValidacaoServer.Server.Templates.ValidacaoServerTemplate;
import ValidacaoServer.Validador.Implementacao.Validador;

public class UDPValidacaoServer extends ValidacaoServerTemplate {
	private int serverPort;
	private DatagramSocket serverSocket;
    private static final int BUFFER = 1024;
	private Validador validador = new Validador();
    
    public UDPValidacaoServer(int serverPort) {
		try {
            this.serverSocket = new DatagramSocket(serverPort);
            this.setServerPort(serverPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        startHeartBeat();
        System.out.println("UDP Validacao Server Started");
        try {
			while (true) {
				byte[] buf = new byte[BUFFER];
				DatagramPacket in = new DatagramPacket(buf, buf.length);
				serverSocket.receive(in);
				String raw = new String(in.getData(), 0, in.getLength()).trim();
				
                String reply = processar(raw);
				byte[] out = reply.getBytes();
				DatagramPacket resp = new DatagramPacket(out, out.length, in.getAddress(), in.getPort());
				serverSocket.send(resp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
            
    }

    private String processar(String message) {
		String resultadoOp = message;
		try {
			String[] p = message.split(";", 3);
			if (p.length < 3) {
				return "Confirmo Recebimento de:erro;formato_invalido;esperado operacao;conta;valor";
			}
			int valor = Integer.parseInt(p[0].trim());
			int valorMinimo = Integer.parseInt(p[1].trim());
			int valorMaximo = Integer.parseInt(p[2].trim());

            resultadoOp = validador.validar(valor, valorMinimo, valorMaximo);

			return "Confirmo Recebimento de:" + resultadoOp;
		} catch (NumberFormatException nfe) {
			return "Um erro ocorreu durante a operação";
		}
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
