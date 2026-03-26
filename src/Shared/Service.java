package Shared;

import java.sql.Timestamp;

public class Service {
    private String name;
    private String port;
    private Timestamp ultimoHeartBeat;
    private String type;

    public Service(String name, String port, String type, Timestamp ultimoHeartBeat) {
        this.name = name;
        this.port = port;
        this.type = type;
        this.ultimoHeartBeat = ultimoHeartBeat;
    }

    public Service(String name, String port, String type) {
        this.name = name;
        this.port = port;
        this.type = type;
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
}
