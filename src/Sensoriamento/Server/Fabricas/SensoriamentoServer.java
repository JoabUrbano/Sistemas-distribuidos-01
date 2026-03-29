package Sensoriamento.Server.Fabricas;

import Sensoriamento.Server.SensoriamentoContrato;
import Sensoriamento.Server.Estrategias.UDPSensoriamentoServer;

public class SensoriamentoServer {
    public static SensoriamentoContrato createSensoriamentoServer() {
        return new UDPSensoriamentoServer();
    }
}
