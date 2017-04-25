package application.actions.debug;

import application.model.AppMsg;
import wtf.apis.WTFSocketAPIsAction;
import wtf.socket.WTFSocket;
import wtf.socket.protocol.WTFSocketMsg;
import wtf.socket.routing.item.WTFSocketRoutingDebugItem;

import java.util.List;

public class DebugRemoveFilterAction_1_0 implements WTFSocketAPIsAction {

    public void doAction(WTFSocketMsg msg, List<WTFSocketMsg> responses) {

        final WTFSocketRoutingDebugItem item = (WTFSocketRoutingDebugItem) WTFSocket.ROUTING.getDebugMap().getItem(msg.getFrom());
        final AppMsg body = msg.getBody(AppMsg.class);

        for (int i = 0 ; i < body.getParams().size(); i++) {
            String name = body.getParams().getString(i);
            item.removeFilterGrep(name);
        }

        WTFSocketMsg response = msg.makeResponse();
        response.setBody(new AppMsg().setFlag(1));
        responses.add(response);
    }
}
