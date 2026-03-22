package ValidacaoServer.Validador.Implementacao;

import ValidacaoServer.Validador.ValidadorContract;

public class Validador implements ValidadorContract {
    public String validar(int valor, int valorMinimo, int valorMaximo) {
        if (valor < valorMinimo) {
            return "Valor abaixo do minimo esperado";
        }
        else if (valor > valorMaximo) {
            return "Valor acima do maximo esperado";
        }
        else {
            return "Valor dentro do esperado";
        }
    }
}
