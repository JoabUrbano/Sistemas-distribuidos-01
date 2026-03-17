package ConputadorDeBordo;

import ConputadorDeBordo.Informacoes.InformacoesFactory;
import ConputadorDeBordo.Informacoes.InformacoesInterface;

public class main {
    public static void main(String[] args) { 
        InformacoesInterface informacoes = InformacoesFactory.createInformacoes();
        informacoes.start();
    }
    
}
