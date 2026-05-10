package Sensoriamento;

import Sensoriamento.Server.Fabricas.SensoriamentoServer;
import Sensoriamento.Server.SensoriamentoContrato;

public class main {
    public static void main(String[] args) {
        SensoriamentoContrato sensoriamentoServer = SensoriamentoServer.createSensoriamentoServer();
        sensoriamentoServer.start();
    }
}
