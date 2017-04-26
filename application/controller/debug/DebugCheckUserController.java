package application.controller.debug;

import application.model.ApplicationMsg;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import wtf.socket.controller.WTFSocketController;
import wtf.socket.WTFSocket;
import wtf.socket.protocol.WTFSocketMsg;
import wtf.socket.routing.item.WTFSocketRoutingFormalItem;

import java.util.List;

@Controller
public class DebugCheckUserController implements WTFSocketController {

    @Override
    public boolean isResponse(WTFSocketMsg msg) {
        ApplicationMsg body = msg.getBody(ApplicationMsg.class);
        return StringUtils.startsWith(msg.getFrom(), "Debug_") &&
                body.getCmd() != null &&
                body.getCmd() == 130;
    }

    public void work(WTFSocketMsg msg, List<WTFSocketMsg> responses) {

        final ApplicationMsg body = msg.getBody(ApplicationMsg.class);
        final WTFSocketMsg response = msg.makeResponse();

        for (int i = 0; i < body.getParams().size(); i++) {
            String name = body.getParams().getString(i);
            JSONObject param = new JSONObject();
            param.put("name", name);
            if (WTFSocket.ROUTING.FORMAL_MAP.contains(name)) {
                final WTFSocketRoutingFormalItem user = WTFSocket.ROUTING.FORMAL_MAP.getItem(name);

                param.put("state",  "online");
                param.put("connect", user.getTerm().getConnectType());
                param.put("protocol", user.getAccept());
                param.put("deviceType", user.getType());
            }else {
                param.put("state",  "offline");
            }

            body.addParam(param);
        }

        response.setBody(body);
        responses.add(response);
    }
}
