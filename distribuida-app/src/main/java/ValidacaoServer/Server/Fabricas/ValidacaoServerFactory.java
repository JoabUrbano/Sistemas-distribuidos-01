package ValidacaoServer.Server.Fabricas;

import ValidacaoServer.Server.Estrategias.TCPValidacaoServer;
import ValidacaoServer.Server.ValidacaoServerInterface;

public final class ValidacaoServerFactory {
    private ValidacaoServerFactory() {}

    public static ValidacaoServerInterface createValidacaoServer() {
        // return new UDPValidacaoServer(9004);
        return new TCPValidacaoServer(9005);
    }
}
