package main;

import parser.UserDefinedRegisterProtocolParser;
import remote.WebServer;
import wtf.socket.WTFSocketConfig;
import wtf.socket.WTFSocketServer;
import wtf.socket.event.WTFSocketEventsType;
import wtf.socket.secure.delegate.WTFSocketSecureDelegateType;


public class SMattressMsgServer {

    public static void main(String[] args) {

        WTFSocketServer server = WTFSocketServer.newNettyServer();

        server.getProtocolFamily().registerParser(new UserDefinedRegisterProtocolParser());

        server.getEventGroup().addEventListener((item, msg) -> WebServer.INSTANCE.hardwareOffline(item.getAddress()), WTFSocketEventsType.Disconnect);

        server.getSecureDelegateGroup().addDelegate((msg) -> WebServer.INSTANCE.hasSendPermission(msg.getFrom(), msg.getTo()), WTFSocketSecureDelegateType.SEND_PERMISSION);

        server.run(new WTFSocketConfig()
                .setTcpPort(1234)
                .setUseDebug(true));
    }
}
