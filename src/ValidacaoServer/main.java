package ValidacaoServer;

import ValidacaoServer.Validacao.ValidacaoInterface;
import ValidacaoServer.Validacao.Fabricas.ValidacaoFactory;

public class main {
    public static void main(String[] args) { 
        ValidacaoInterface validacao = ValidacaoFactory.createValidacao();
        validacao.start();
    }
    
}
