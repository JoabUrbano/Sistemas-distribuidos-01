package ConputadorDeBordo.Informacoes.Fabricas;

import ConputadorDeBordo.Informacoes.InformacoesInterface;
import ConputadorDeBordo.Informacoes.Estrategias.UDPInformacoes;

public class InformacoesFactory {
    public static InformacoesInterface createInformacoes() {
        String port = System.getenv("LISTEN_PORT");
        if (port == null || port.isEmpty()) port = "9004";
        UDPInformacoes udpInformacoes = new UDPInformacoes(port);
        return udpInformacoes;
    }
}
