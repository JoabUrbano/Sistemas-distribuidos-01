package ApiGateway.Comunicacao.Fabricas;

import ApiGateway.Comunicacao.ServerContract;
import ApiGateway.Comunicacao.Estrategias.TCPServer;
import ApiGateway.Comunicacao.Estrategias.UDPServer;

public class ServerFactory {
    public static ServerContract createServer(String serverType) {

        return new UDPServer(9003); 
        //return new TCPServer(9003);
    }
}
