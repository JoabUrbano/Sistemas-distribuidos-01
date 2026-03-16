package imd.ufrn.ApiGateway;


import imd.ufrn.ApiGateway.Comunicacao.ServerContract;
import imd.ufrn.ApiGateway.Comunicacao.ServerFactory;

public class main {
    public static void main(String[] args) { 
        ServerContract server = ServerFactory.createServer("UDP");    
        server.start();
    }
}
