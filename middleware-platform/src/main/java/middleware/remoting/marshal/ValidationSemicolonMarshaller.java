package middleware.remoting.marshal;

import middleware.remoting.MarshallingRemotingException;

/**
 * Marshaller para validação (valor;min;max).
 */
public final class ValidationSemicolonMarshaller implements middleware.remoting.Marshaller {

    public record ValidationParts(int valor, int minimo, int maximo) {}

    public ValidationParts parse(String payload) throws MarshallingRemotingException {
        try {
            String[] p = payload.split(";", 3);
            if (p.length < 3) {
                throw new MarshallingRemotingException("formato_invalido");
            }
            return new ValidationParts(
                    Integer.parseInt(p[0].trim()),
                    Integer.parseInt(p[1].trim()),
                    Integer.parseInt(p[2].trim()));
        } catch (NumberFormatException e) {
            throw new MarshallingRemotingException("numeros_invalidos", e);
        }
    }
}
