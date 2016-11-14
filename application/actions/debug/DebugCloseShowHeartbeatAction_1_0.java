package application.actions.debug;

import application.model.AppMsg;
import io.netty.channel.Channel;
import wtf.apis.WTFSocketAPIsAction;
import wtf.socket.protocols.templates.WTFSocketProtocol;
import wtf.socket.protocols.templates.WTFSocketProtocol_2_0;
import wtf.socket.registry.WTFSocketRegistry;
import wtf.socket.registry.items.WTFSocketRegistryDebugItem;

import java.util.List;

public class DebugCloseShowHeartbeatAction_1_0 implements WTFSocketAPIsAction {
    @Override
    public void doAction(Channel ctx, WTFSocketProtocol protocol, List<WTFSocketProtocol> responses) {

        WTFSocketRegistryDebugItem debugItem = (WTFSocketRegistryDebugItem) WTFSocketRegistry.get(protocol.getFrom());

        debugItem.setShowHeartbeatMsg(false);

        WTFSocketProtocol_2_0 response = WTFSocketProtocol_2_0.makeResponse(protocol);
        response.setBody(new AppMsg().setFlag(1));
        responses.add(response);

    }
}
