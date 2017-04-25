package application.actions.debug;

import application.model.AppMsg;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import wtf.apis.WTFSocketAPIsAction;
import wtf.socket.WTFSocket;
import wtf.socket.protocol.WTFSocketMsg;
import wtf.socket.routing.item.WTFSocketRoutingFormalItem;
import wtf.socket.routing.item.WTFSocketRoutingItem;

import java.util.List;

public class DebugListAllUsersAction_1_0 implements WTFSocketAPIsAction {

    public void doAction(WTFSocketMsg msg, List<WTFSocketMsg> responses) {

        WTFSocketMsg response = msg.makeResponse();
        AppMsg body = msg.getBody(AppMsg.class);
        AppMsg responseBody = new AppMsg().setFlag(1);

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

        for (WTFSocketRoutingItem item : WTFSocket.ROUTING.getFormalMap().values()) {

            WTFSocketRoutingFormalItem user = (WTFSocketRoutingFormalItem) item;

            boolean isFilter = true;

            if (connectTypeFilter != null) {
                if (!StringUtils.equals("*", connectTypeFilter)) {
                    boolean typeThough = false;
                    String[] types = connectTypeFilter.split(",");
                    for (String type : types) {
                        typeThough = typeThough || StringUtils.equals(type, item.getTerm().getConnectType().toString());
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
