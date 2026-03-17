package ApiGateway.Comunicacao;

import ApiGateway.Comunicacao.Estrategias.UDPServer;

public class ServerFactory {
    public static ServerContract createServer(String serverType) {
        UDPServer udpServer = new UDPServer("9003");
        return udpServer;
    }
}
