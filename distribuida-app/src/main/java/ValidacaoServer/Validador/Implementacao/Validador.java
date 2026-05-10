package ValidacaoServer.Validador.Implementacao;

import ValidacaoServer.Validador.ValidacaoResult;
import ValidacaoServer.Validador.ValidadorContract;
import middleware.remoting.annotations.RemoteComponent;
import middleware.remoting.annotations.RemoteOperation;

@RemoteComponent
public class Validador implements ValidadorContract {

    @RemoteOperation("validar")
    @Override
    public ValidacaoResult validarComResultado(int valor, int valorMinimo, int valorMaximo) {
        if (valor < valorMinimo) {
            return new ValidacaoResult(false, "Valor abaixo do minimo esperado");
        }
        if (valor > valorMaximo) {
            return new ValidacaoResult(false, "Valor acima do maximo esperado");
        }
        return new ValidacaoResult(true, "Valor dentro do esperado");
    }

    @Override
    public String validar(int valor, int valorMinimo, int valorMaximo) {
        return validarComResultado(valor, valorMinimo, valorMaximo).mensagem();
    }
}
