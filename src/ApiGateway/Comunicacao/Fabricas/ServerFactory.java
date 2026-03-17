package ApiGateway.Comunicacao.Fabricas;

import ApiGateway.Comunicacao.ServerContract;
import ApiGateway.Comunicacao.Estrategias.UDPServer;

public class ServerFactory {
    public static ServerContract createServer(String serverType) {
        String port = System.getenv("GATEWAY_PORT");
        if (port == null || port.isEmpty()) port = "9003";
        UDPServer udpServer = new UDPServer(port);
        return udpServer;
    }
}
