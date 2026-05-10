package middleware.gateway.fabricas;

import middleware.gateway.ServerContract;
import middleware.gateway.estrategias.TCPServer;
import middleware.gateway.estrategias.UDPServer;

public final class ServerFactory {
    private ServerFactory() {}

    /**
     * Seleciona o plug-in de protocolo do gateway (Fase F). Use {@code -Ddistribuida.gateway.protocol=UDP} ou {@code TCP} (padrão).
     */
    public static ServerContract createGateway(int port) {
        String protocol = System.getProperty("distribuida.gateway.protocol", "TCP");
        if ("UDP".equalsIgnoreCase(protocol)) {
            return new UDPServer(port);
        }
        return new TCPServer(port);
    }

    public static ServerContract createServer(String serverType) {
        int port = Integer.getInteger("distribuida.gateway.port", 9003);
        if (serverType != null && serverType.equalsIgnoreCase("UDP")) {
            System.setProperty("distribuida.gateway.protocol", "UDP");
        }
        return createGateway(port);
    }
}
