package middleware.shared;

import java.sql.Timestamp;

import middleware.remoting.identification.AbsoluteObjectReference;
import middleware.remoting.identification.ObjectId;
import middleware.remoting.identification.ObjectIds;

public class Service {
    private String name;
    private String port;
    private Timestamp ultimoHeartBeat;
    private String type;
    private String objectId;
    private String transportProtocol;

    public Service(String name, String port, String type, Timestamp ultimoHeartBeat) {
        this.name = name;
        this.port = port;
        this.type = type;
        this.ultimoHeartBeat = ultimoHeartBeat;
        this.transportProtocol = "TCP_HTTP";
    }

    public Service(String name, String port, String type) {
        this.name = name;
        this.port = port;
        this.type = type;
        this.transportProtocol = "TCP_HTTP";
    }

    public String getName() {
        return name;
    }

    public String getPort() {
        return port;
    }

    public String getUrl() {
        return name + ":" + port;
    }

    public String getType() {
        return type;
    }

    public Timestamp getUltimoHeartBeat() {
        return ultimoHeartBeat;
    }

    public void setUltimoHeartBeat(Timestamp ultimoHeartBeat) {
        this.ultimoHeartBeat = ultimoHeartBeat;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getTransportProtocol() {
        return transportProtocol != null ? transportProtocol : "TCP_HTTP";
    }

    public void setTransportProtocol(String transportProtocol) {
        this.transportProtocol = transportProtocol;
    }

    /**
     * Representação como Absolute Object Reference (Fase D).
     */
    public AbsoluteObjectReference toAbsoluteReference() {
        int p = Integer.parseInt(port);
        ObjectId oid = objectId != null ? ObjectId.of(objectId) : ObjectIds.deterministic(name, p, type);
        return new AbsoluteObjectReference(getTransportProtocol(), name, p, type, oid);
    }
}
