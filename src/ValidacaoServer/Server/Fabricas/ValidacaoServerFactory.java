package ValidacaoServer.Server.Fabricas;

import ValidacaoServer.Server.ValidacaoServerInterface;
import ValidacaoServer.Server.Estrategias.TCPValidacaoServer;
import ValidacaoServer.Server.Estrategias.UDPValidacaoServer;

public class ValidacaoServerFactory {
    public static ValidacaoServerInterface createValidacaoServer() {

        //UDPValidacaoServer udpValidacaoServer = new UDPValidacaoServer(9004);
        //return udpValidacaoServer;
        return new TCPValidacaoServer(9004);
    }
}
