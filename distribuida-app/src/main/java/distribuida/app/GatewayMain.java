package distribuida.app;

import middleware.gateway.ServerContract;
import middleware.gateway.fabricas.ServerFactory;

public final class GatewayMain {
    public static void main(String[] args) {
        int port = Integer.getInteger("distribuida.gateway.port", 9003);
        ServerContract server = ServerFactory.createGateway(port);
        server.start();
    }
}
