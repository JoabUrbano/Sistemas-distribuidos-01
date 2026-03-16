package imd.ufrn.ConputadorDeBordo;

import imd.ufrn.ConputadorDeBordo.Informacoes.InformacoesFactory;
import imd.ufrn.ConputadorDeBordo.Informacoes.InformacoesInterface;

public class main {
    public static void main(String[] args) { 
        InformacoesInterface informacoes = InformacoesFactory.createInformacoes();
        informacoes.start();
    }
    
}
