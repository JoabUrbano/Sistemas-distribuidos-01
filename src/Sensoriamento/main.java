package Sensoriamento;

import Sensoriamento.Server.SensoriamentoContrato;
import Sensoriamento.Server.Fabricas.SensoriamentoServer;

public class main {
    public static void main(String[] args) {
        SensoriamentoContrato sensoriamentoServer = SensoriamentoServer.createSensoriamentoServer();
        sensoriamentoServer.start();
    }
}
