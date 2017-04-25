package application.actions.debug;

import application.model.AppMsg;
import wtf.apis.WTFSocketAPIsAction;
import wtf.socket.WTFSocket;
import wtf.socket.protocol.WTFSocketMsg;
import wtf.socket.routing.item.WTFSocketRoutingDebugItem;


import java.util.List;

public class DebugCloseShowHeartbeatAction_1_0 implements WTFSocketAPIsAction {

    public void doAction(WTFSocketMsg msg, List<WTFSocketMsg> responses) {

        final WTFSocketRoutingDebugItem item = (WTFSocketRoutingDebugItem) WTFSocket.ROUTING.getDebugMap().getItem(msg.getFrom());

        item.setShowHeartbeatMsg(false);

        WTFSocketMsg response = msg.makeResponse();
        response.setBody(new AppMsg().setFlag(1));
        responses.add(response);
    }
}
