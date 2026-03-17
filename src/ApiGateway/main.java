package ApiGateway;


import ApiGateway.Comunicacao.ServerContract;
import ApiGateway.Comunicacao.ServerFactory;

public class main {
    public static void main(String[] args) { 
        ServerContract server = ServerFactory.createServer("UDP");    
        server.start();
    }
}
