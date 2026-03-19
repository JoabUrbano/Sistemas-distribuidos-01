package ValidacaoServer.Validacao.Fabricas;

import ValidacaoServer.Validacao.ValidacaoInterface;
import ValidacaoServer.Validacao.Estrategias.UDPValidacao;

public class ValidacaoFactory {
    public static ValidacaoInterface createValidacao() {

        UDPValidacao udpValidacao = new UDPValidacao(9004);
        return udpValidacao;
    }
}
