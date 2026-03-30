package Sensoriamento.Sensoriamento;

import java.util.concurrent.ThreadLocalRandom;

public class Sensoriamento {
    public int geTemperatura() {
        int temperatura = ThreadLocalRandom.current().nextInt(20, 30);
        return temperatura;
    }
}
