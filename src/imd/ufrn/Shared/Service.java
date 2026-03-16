package imd.ufrn.Shared;

public class Service {
    private String name;
    private String port;

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
}
