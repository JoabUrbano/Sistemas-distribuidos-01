package ValidacaoServer.Validador.Implementacao;

import ValidacaoServer.Validador.ValidadorContract;

public class Validador implements ValidadorContract {
    public boolean validarVelocidade(int velocidade) {
        if (velocidade < 100 && velocidade > 0) {
            return true;
        } else {
            return false;
        }
    }
}
