package middleware.remoting.gateway;

import java.sql.Timestamp;
import java.util.Optional;

import middleware.remoting.identification.ObjectIds;
import middleware.shared.Service;

/**
 * Parse compatível com {@code Quack;Tipo;host:port} ou com quarto campo opcional {@code objectId}.
 */
public final class QuackMessage {

    private QuackMessage() {}

    public static Optional<Service> tryParse(String payload) {
        String[] p = payload.split(";", -1);
        if (p.length < 3 || !"Quack".equals(p[0].trim())) {
            return Optional.empty();
        }
        String tipo = p[1].trim();
        String[] url = p[2].trim().split(":", 2);
        if (url.length < 2) {
            return Optional.empty();
        }
        String host = url[0].trim();
        String portStr = url[1].trim();
        int portNum;
        try {
            portNum = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
        String oid = p.length >= 4 && !p[3].trim().isEmpty()
                ? p[3].trim()
                : ObjectIds.deterministic(host, portNum, tipo).value();

        Service service = new Service(host, portStr, tipo, new Timestamp(System.currentTimeMillis()));
        service.setObjectId(oid);
        service.setTransportProtocol("TCP_HTTP");
        return Optional.of(service);
    }
}
