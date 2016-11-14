package application.actions;

import application.model.AppMsg;
import wtf.apis.WTFSocketAPIsAction;
import wtf.socket.protocols.templates.WTFSocketProtocol;
import wtf.socket.protocols.templates.WTFSocketProtocol_2_0;
import wtf.socket.registry.WTFSocketRegistry;

import io.netty.channel.Channel;

import java.util.List;

/**
 * 注销功能
 */
public class UnregisterAction_1_0 implements WTFSocketAPIsAction {
    @Override
    public void doAction(Channel ctx, WTFSocketProtocol protocol, List<WTFSocketProtocol> responses) {

        WTFSocketRegistry.unRegister(protocol.getFrom());
        WTFSocketProtocol_2_0 response = WTFSocketProtocol_2_0.makeResponse(protocol);
        response.setBody(new AppMsg().setFlag(1));
        responses.add(response);
    }
}
