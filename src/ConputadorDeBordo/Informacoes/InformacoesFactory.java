package ConputadorDeBordo.Informacoes;

import ConputadorDeBordo.Informacoes.Estrategias.UDPInformacoes;

public class InformacoesFactory {
    public static InformacoesInterface createInformacoes() {
        UDPInformacoes udpInformacoes = new UDPInformacoes("9004");
        return udpInformacoes;
    }
}
