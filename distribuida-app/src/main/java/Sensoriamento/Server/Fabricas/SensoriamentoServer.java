package Sensoriamento.Server.Fabricas;

import Sensoriamento.Server.Estrategias.TCPSensoriamentoServer;
import Sensoriamento.Server.SensoriamentoContrato;

public final class SensoriamentoServer {
    private SensoriamentoServer() {}

    public static SensoriamentoContrato createSensoriamentoServer() {
        // return new UDPSensoriamentoServer(9010);
        return new TCPSensoriamentoServer(9011);
    }
}
