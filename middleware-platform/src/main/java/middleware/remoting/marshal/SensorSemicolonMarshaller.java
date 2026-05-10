package middleware.remoting.marshal;

import middleware.remoting.MarshallingRemotingException;

/**
 * Marshaller textual para o fluxo do sensor (operação;campo2;campo3).
 */
public final class SensorSemicolonMarshaller implements middleware.remoting.Marshaller {

    public record SensorParts(String operation, String field2, String field3) {}

    public SensorParts parse(String payload) throws MarshallingRemotingException {
        String[] p = payload.split(";", 3);
        if (p.length < 3) {
            throw new MarshallingRemotingException("formato_invalido;esperado_tres_campos");
        }
        return new SensorParts(p[0].trim(), p[1].trim(), p[2].trim());
    }

    public String formatResult(String numericResult, String field2, String field3) {
        return numericResult + ";" + field2 + ";" + field3;
    }
}
