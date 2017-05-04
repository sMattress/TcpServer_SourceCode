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
public class DebugListAllUsersController implements WTFSocketSimpleController {

    @Override
    public boolean isResponse(WTFSocketMessage msg) {
        final ApplicationMsg body = msg.getBody(ApplicationMsg.class);
        return StringUtils.startsWith(msg.getFrom(), "Debug_") &&
                body.getCmd() != null &&
                body.getCmd() == 131;
    }

    public boolean work(WTFSocketClient item, WTFSocketMessage msg, WTFSocketResponse response) {

        final WTFSocketMessage message = msg.makeResponse();
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

        for (WTFSocketFormalClient formalClient : item.getContext().getRouting().getFormalMap().values()) {

            boolean isFilter = true;

            if (connectTypeFilter != null) {
                if (!StringUtils.equals("*", connectTypeFilter)) {
                    boolean typeThough = false;
                    String[] types = connectTypeFilter.split(",");
                    for (String type : types) {
                        typeThough = typeThough || StringUtils.equals(type, formalClient.getTerm().getConnectType().toString());
                    }
                    isFilter = typeThough;
                }
            }

            if (protocolTypeFilter != null) {
                if (!StringUtils.equals("*", protocolTypeFilter)) {
                    boolean typeThough = false;
                    String[] types = protocolTypeFilter.split(",");
                    for (String type : types) {
                        typeThough = typeThough || StringUtils.equals(type, formalClient.getAccept());
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
                            typeThough = typeThough || StringUtils.equals(type, formalClient.getDeviceType());
                        }
                        isFilter = isFilter && typeThough;
                    }
                }
            }

            if (isFilter) {
                responseBody.addParam(new JSONObject() {{
                    put("name", formalClient.getAddress());
                    put("state", "online");
                    put("connectType", formalClient.getTerm().getConnectType());
                    put("protocolType", formalClient.getAccept());
                    put("deviceType", formalClient.getDeviceType());
                }});
            }
        }

        message.setBody(responseBody);
        response.addMessage(message);

        return true;
    }
}
