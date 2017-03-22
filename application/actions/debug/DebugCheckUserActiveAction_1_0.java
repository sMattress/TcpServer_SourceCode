package application.actions.debug;

import application.model.AppMsg;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import wtf.apis.WTFSocketAPIsAction;
import wtf.socket.protocols.templates.WTFSocketProtocol;
import wtf.socket.protocols.templates.WTFSocketProtocol_2_0;
import wtf.socket.registry.WTFSocketRegistry;
import wtf.socket.registry.items.WTFSocketRegistryUserItem;

import java.util.List;

public class DebugCheckUserActiveAction_1_0 implements WTFSocketAPIsAction {

    public void doAction(Channel ctx, WTFSocketProtocol protocol, List<WTFSocketProtocol> responses) {

        AppMsg msg = protocol.getBody(AppMsg.class);
        WTFSocketProtocol_2_0 response = WTFSocketProtocol_2_0.makeResponse(protocol);
        AppMsg body = new AppMsg().setFlag(1);

        if (msg.getParams() == null) {
            WTFSocketProtocol_2_0 errResponse = WTFSocketProtocol_2_0.makeResponse(protocol);
            errResponse.setBody(AppMsg.failure(33, "lack necessary attr => <params>"));
            responses.add(errResponse);
            return;
        }

        for (int i = 0; i < msg.getParams().size(); i++) {
            String name = msg.getParams().getString(i);
            JSONObject param = new JSONObject();
            param.put("name", name);
            if (WTFSocketRegistry.contains(name)) {
                WTFSocketRegistryUserItem user = (WTFSocketRegistryUserItem)WTFSocketRegistry.get(name);
                param.put("state",  "online");
                param.put("connect", user.getConnectType());
                param.put("protocol", user.getAccept());
                param.put("deviceType", user.getDeviceType());
            }else {
                param.put("state",  "offline");
            }

            body.addParam(param);
        }

        response.setBody(body);
        responses.add(response);
    }
}
