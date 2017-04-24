package application.actions.debug;

import application.model.AppMsg;
import com.alibaba.fastjson.JSONObject;
import wtf.apis.WTFSocketAPIsAction;
import wtf.socket.protocol.WTFSocketMsg;
import wtf.socket.routing.WTFSocketRoutingMap;
import wtf.socket.routing.item.WTFSocketRoutingFormalItem;

import java.util.List;

public class DebugCheckUserActiveAction_1_0 implements WTFSocketAPIsAction {

    public void doAction(WTFSocketMsg msg, List<WTFSocketMsg> responses) {

        final AppMsg body = msg.getBody(AppMsg.class);
        final WTFSocketMsg response = msg.makeResponse();

        for (int i = 0; i < body.getParams().size(); i++) {
            String name = body.getParams().getString(i);
            JSONObject param = new JSONObject();
            param.put("name", name);
            if (WTFSocketRoutingMap.FORMAL.contains(name)) {
                final WTFSocketRoutingFormalItem user = (WTFSocketRoutingFormalItem) WTFSocketRoutingMap.FORMAL.getItem(name);

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
