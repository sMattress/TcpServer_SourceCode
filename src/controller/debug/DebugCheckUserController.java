package controller.debug;

import com.alibaba.fastjson.JSONObject;
import model.ApplicationMsg;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import wtf.socket.controller.WTFSocketSimpleController;
import wtf.socket.protocol.WTFSocketMessage;
import wtf.socket.routing.client.WTFSocketClient;
import wtf.socket.routing.client.WTFSocketFormalClient;
import wtf.socket.workflow.response.WTFSocketResponse;

@Controller
public class DebugCheckUserController implements WTFSocketSimpleController {

    @Override
    public boolean isResponse(WTFSocketMessage msg) {
        final ApplicationMsg body = msg.getBody(ApplicationMsg.class);
        return StringUtils.startsWith(msg.getFrom(), "Debug_") &&
                body.getCmd() != null &&
                body.getCmd() == 130;
    }

    @Override
    public boolean work(WTFSocketClient source, WTFSocketMessage msg, WTFSocketResponse response) {

        final ApplicationMsg body = msg.getBody(ApplicationMsg.class);
        final WTFSocketMessage message = msg.makeResponse();

        for (int i = 0; i < body.getParams().size(); i++) {
            String name = body.getParams().getString(i);
            JSONObject param = new JSONObject();
            param.put("name", name);
            if (source.getContext().getRouting().getFormalMap().contains(name)) {
                final WTFSocketFormalClient user = source.getContext().getRouting().getFormalMap().getItem(name);

                param.put("state",  "online");
                param.put("connect", user.getTerm().getConnectType());
                param.put("protocol", user.getAccept());
                param.put("deviceType", user.getDeviceType());
            }else {
                param.put("state",  "offline");
            }

            body.addParam(param);
        }

        message.setBody(body);
        response.addMessage(message);

        return true;
    }
}
