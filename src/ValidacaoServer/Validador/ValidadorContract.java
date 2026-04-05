package ValidacaoServer.Validador;

public interface ValidadorContract {
    String validar(int valor, int valorMinimo, int valorMaximo);

    ValidacaoResult validarComResultado(int valor, int valorMinimo, int valorMaximo);
}
