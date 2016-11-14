package application.actions.debug;

import application.model.AppMsg;
import io.netty.channel.Channel;
import wtf.apis.WTFSocketAPIsAction;
import wtf.socket.protocols.templates.WTFSocketProtocol;
import wtf.socket.protocols.templates.WTFSocketProtocol_2_0;
import wtf.socket.registry.WTFSocketRegistry;
import wtf.socket.registry.items.WTFSocketRegistryDebugItem;

import java.util.List;

public class DebugAddFilterAction_1_0 implements WTFSocketAPIsAction {

    @Override
    public void doAction(Channel ctx, WTFSocketProtocol protocol, List<WTFSocketProtocol> responses) {

        WTFSocketRegistryDebugItem debugItem = (WTFSocketRegistryDebugItem) WTFSocketRegistry.get(protocol.getFrom());

        AppMsg msg = protocol.getBody(AppMsg.class);

        if (msg.getParams() == null) {
            WTFSocketProtocol_2_0 errResponse = WTFSocketProtocol_2_0.makeResponse(protocol);
            errResponse.setBody(AppMsg.failure(33, "lack necessary attr => <params>"));
            responses.add(errResponse);
            return;
        }

        for (int i = 0 ; i < msg.getParams().size(); i++) {
            String name = msg.getParams().getString(i);
            debugItem.addFilterGrep(name);
        }

        WTFSocketProtocol_2_0 response = WTFSocketProtocol_2_0.makeResponse(protocol);
        response.setBody(new AppMsg().setFlag(1));
        responses.add(response);
    }
}
