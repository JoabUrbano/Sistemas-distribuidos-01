package ValidacaoServer;

import ValidacaoServer.Server.Fabricas.ValidacaoServerFactory;
import ValidacaoServer.Server.ValidacaoServerInterface;

public class main {
    public static void main(String[] args) {
        ValidacaoServerInterface validacaoServer = ValidacaoServerFactory.createValidacaoServer();
        validacaoServer.start();
    }
}
