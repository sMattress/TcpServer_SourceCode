package application.main;

import application.parser.UserDefinedRegisterProtocolParser;
import application.remote.WebServer;
import wtf.socket.WTFSocket;
import wtf.socket.event.WTFSocketEventsType;
import wtf.socket.schedule.WTFSocketConfig;


public class SMattressMsgServer {

    public static void main(String[] args) {

        WTFSocket.PROTOCOL_FAMILY.registerParser(new UserDefinedRegisterProtocolParser());

        WTFSocket.EVENTS_GROUP.addEventListener((item, msg) -> WebServer.INSTANCE.hardwareOffline(item.getAddress()), WTFSocketEventsType.Disconnect);

        WTFSocket.SECURE.setSendPermissionAuthDelegate((msg) -> WebServer.INSTANCE.hasSendPermission(msg.getFrom(), msg.getTo()));

        WTFSocket.run(new WTFSocketConfig()
                .setTcpPort(1234)
                .setUseDebug(true));
    }
}
