package application.actions;

import application.model.AppMsg;
import com.alibaba.fastjson.JSONObject;
import wtf.apis.WTFSocketAPIsAction;
import wtf.socket.protocols.templates.WTFSocketProtocol;
import wtf.socket.protocols.templates.WTFSocketProtocol_2_0;
import wtf.socket.registry.WTFSocketRegistry;

import io.netty.channel.Channel;

import java.util.List;

/**
 * 注册功能
 */
public class RegisterAction_1_0 implements WTFSocketAPIsAction {

    @Override
    public void doAction(Channel ctx, WTFSocketProtocol protocol, List<WTFSocketProtocol> responses) {

        String deviceType = "Unknown";

        AppMsg body = protocol.getBody(AppMsg.class);

        if (body.getParams() != null ) {
            JSONObject param = body.getParams().getJSONObject(0);
            if (param.containsKey("deviceType")) {
                deviceType = param.getString("deviceType");
            }
        }

        WTFSocketRegistry.register(
                protocol.getFrom(),
                ctx,
                protocol.getConnectType(),
                protocol.getVersion(),
                deviceType
        );

        WTFSocketProtocol_2_0 response = WTFSocketProtocol_2_0.makeResponse(protocol);
        response.setBody(new AppMsg().setFlag(1));

        responses.add(response);
    }
}
