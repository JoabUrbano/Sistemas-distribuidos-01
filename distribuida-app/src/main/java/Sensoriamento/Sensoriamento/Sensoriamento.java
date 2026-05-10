package Sensoriamento.Sensoriamento;

import middleware.remoting.annotations.RemoteComponent;
import middleware.remoting.annotations.RemoteOperation;

@RemoteComponent
public class Sensoriamento {
    @RemoteOperation("temperatura")
    public int geTemperatura() {
        int temperatura = 25;
        return temperatura;
    }

    @RemoteOperation("velocidade")
    public int getVelocidade() {
        int velocidade = 100;
        return velocidade;
    }
}
