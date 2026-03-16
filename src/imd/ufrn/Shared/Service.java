package imd.ufrn.Shared;

import java.security.Timestamp;

public class Service {
    private String name;
    private String port;
    private Timestamp ultimoHeartBeat;

    public Service(String name, String port) {
        this.name = name;
        this.port = port;
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

    public Timestamp getUltimoHeartBeat() {
        return ultimoHeartBeat;
    }

    public void setUltimoHeartBeat(Timestamp ultimoHeartBeat) {
        this.ultimoHeartBeat = ultimoHeartBeat;
    }
}
