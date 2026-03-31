package Sensoriamento.Sensoriamento;

import java.util.concurrent.ThreadLocalRandom;

public class Sensoriamento {
    public int geTemperatura() {
        //int temperatura = ThreadLocalRandom.current().nextInt(20, 30);
        int temperatura = 25;
        return temperatura;
    }

    public int getVelocidade() {
        //int velocidade = ThreadLocalRandom.current().nextInt(0, 100);
        int velocidade = 100;
        return velocidade;
    }
}
