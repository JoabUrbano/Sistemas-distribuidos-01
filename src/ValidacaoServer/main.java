package ValidacaoServer;

import ValidacaoServer.Server.ValidacaoServerInterface;
import ValidacaoServer.Server.Fabricas.ValidacaoServerFactory;

public class main {
    public static void main(String[] args) { 
        ValidacaoServerInterface validacaoServer = ValidacaoServerFactory.createValidacaoServer();
        validacaoServer.start();
    }
    
}
