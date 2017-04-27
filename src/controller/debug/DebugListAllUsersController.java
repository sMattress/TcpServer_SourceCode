package controller.debug;

import com.alibaba.fastjson.JSONObject;
import model.ApplicationMsg;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import wtf.socket.controller.WTFSocketController;
import wtf.socket.WTFSocket;
import wtf.socket.protocol.WTFSocketMsg;
import wtf.socket.routing.item.WTFSocketRoutingFormalItem;
import wtf.socket.routing.item.WTFSocketRoutingItem;

import java.util.List;

@Controller
public class DebugListAllUsersController implements WTFSocketController {

    @Override
    public boolean isResponse(WTFSocketMsg msg) {
        final ApplicationMsg body = msg.getBody(ApplicationMsg.class);
        return StringUtils.startsWith(msg.getFrom(), "Debug_") &&
                body.getCmd() != null &&
                body.getCmd() == 131;
    }

    public void work(WTFSocketRoutingItem item, WTFSocketMsg msg, List<WTFSocketMsg> responses) {

        final WTFSocketMsg response = msg.makeResponse();
        final ApplicationMsg body = msg.getBody(ApplicationMsg.class);
        final ApplicationMsg responseBody = new ApplicationMsg().setFlag(1);

        String connectTypeFilter = null;
        String protocolTypeFilter = null;
        String deviceTypeFilter = null;

        JSONObject param = body.getParams().getJSONObject(0);
        if (param.containsKey("connectType")) {
            connectTypeFilter = param.getString("connectType");
        }
        if (param.containsKey("protocolType")) {
            protocolTypeFilter = param.getString("protocolType");
        }
        if (param.containsKey("deviceType")) {
            deviceTypeFilter = param.getString("deviceType");
        }

        for (WTFSocketRoutingItem formalItem : WTFSocket.ROUTING.FORMAL_MAP.values()) {

            WTFSocketRoutingFormalItem user = (WTFSocketRoutingFormalItem) formalItem;

            boolean isFilter = true;

            if (connectTypeFilter != null) {
                if (!StringUtils.equals("*", connectTypeFilter)) {
                    boolean typeThough = false;
                    String[] types = connectTypeFilter.split(",");
                    for (String type : types) {
                        typeThough = typeThough || StringUtils.equals(type, formalItem.getTerm().getConnectType().toString());
                    }
                    isFilter = typeThough;
                }
            }

            if (protocolTypeFilter != null) {
                if (!StringUtils.equals("*", protocolTypeFilter)) {
                    boolean typeThough = false;
                    String[] types = protocolTypeFilter.split(",");
                    for (String type : types) {
                        typeThough = typeThough || StringUtils.equals(type, user.getAccept());
                    }
                    isFilter = isFilter && typeThough;
                }
            }

            if (deviceTypeFilter != null) {
                if (!StringUtils.equals("*", deviceTypeFilter)) {
                    if (!StringUtils.equals("*", deviceTypeFilter)) {
                        boolean typeThough = false;
                        String[] types = deviceTypeFilter.split(",");
                        for (String type : types) {
                            typeThough = typeThough || StringUtils.equals(type, user.getType());
                        }
                        isFilter = isFilter && typeThough;
                    }
                }
            }

            if (isFilter) {
                responseBody.addParam(new JSONObject() {{
                    put("name", user.getAddress());
                    put("state", "online");
                    put("connectType", user.getTerm().getConnectType());
                    put("protocolType", user.getAccept());
                    put("deviceType", user.getType());
                }});
            }
        }

        response.setBody(responseBody);
        responses.add(response);
    }
}
