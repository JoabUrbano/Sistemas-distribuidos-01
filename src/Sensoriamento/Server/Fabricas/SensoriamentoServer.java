package Sensoriamento.Server.Fabricas;

import Sensoriamento.Server.SensoriamentoContrato;
import Sensoriamento.Server.Estrategias.TCPSensoriamentoServer;
import Sensoriamento.Server.Estrategias.UDPSensoriamentoServer;

public class SensoriamentoServer {
    public static SensoriamentoContrato createSensoriamentoServer() {
        //return new UDPSensoriamentoServer(9010);
        return new TCPSensoriamentoServer(9010);
    }
}
