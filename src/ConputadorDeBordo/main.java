package ConputadorDeBordo;

import ConputadorDeBordo.Informacoes.InformacoesInterface;
import ConputadorDeBordo.Informacoes.Fabricas.InformacoesFactory;

public class main {
    public static void main(String[] args) { 
        InformacoesInterface informacoes = InformacoesFactory.createInformacoes();
        informacoes.start();
    }
    
}
