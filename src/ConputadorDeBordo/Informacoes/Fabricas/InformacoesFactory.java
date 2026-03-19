package ConputadorDeBordo.Informacoes.Fabricas;

import ConputadorDeBordo.Informacoes.InformacoesInterface;
import ConputadorDeBordo.Informacoes.Estrategias.UDPInformacoes;

public class InformacoesFactory {
    public static InformacoesInterface createInformacoes() {

        UDPInformacoes udpInformacoes = new UDPInformacoes(9004);
        return udpInformacoes;
    }
}
