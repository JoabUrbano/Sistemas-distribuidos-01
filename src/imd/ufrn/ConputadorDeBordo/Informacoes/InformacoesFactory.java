package imd.ufrn.ConputadorDeBordo.Informacoes;

import imd.ufrn.ConputadorDeBordo.Informacoes.Estrategias.UDPInformacoes;

public class InformacoesFactory {
    public static InformacoesInterface createInformacoes() {
        UDPInformacoes udpInformacoes = new UDPInformacoes("9004");
        return udpInformacoes;
    }
}
