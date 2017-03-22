package application.actions.debug;

import application.model.AppMsg;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import org.apache.commons.lang.StringUtils;
import wtf.apis.WTFSocketAPIsAction;
import wtf.socket.protocols.templates.WTFSocketProtocol;
import wtf.socket.protocols.templates.WTFSocketProtocol_2_0;
import wtf.socket.registry.WTFSocketRegistry;
import wtf.socket.registry.items.WTFSocketRegistryItem;
import wtf.socket.registry.items.WTFSocketRegistryUserItem;
import wtf.socket.registry.items.WTFSocketUserType;

import java.util.List;

public class DebugListAllUsersAction_1_0 implements WTFSocketAPIsAction {

    public void doAction(Channel ctx, WTFSocketProtocol protocol, List<WTFSocketProtocol> responses) {

        WTFSocketProtocol_2_0 response = WTFSocketProtocol_2_0.makeResponse(protocol);
        AppMsg body = protocol.getBody(AppMsg.class);
        AppMsg responseBody = new AppMsg().setFlag(1);

        String connectTypeFilter = null;
        String protocolTypeFilter = null;
        String deviceTypeFilter = null;

        if (body.getParams() != null) {
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
        }

        for (WTFSocketRegistryItem item : WTFSocketRegistry.values(WTFSocketUserType.USER)) {

            WTFSocketRegistryUserItem user = (WTFSocketRegistryUserItem) item;

            boolean isFilter = true;

            if (connectTypeFilter != null) {
                if (!StringUtils.equals("*", connectTypeFilter)) {
                    boolean typeThough = false;
                    String[] types = connectTypeFilter.split(",");
                    for (String type : types) {
                        typeThough = typeThough || StringUtils.equals(type, item.getConnectType().toString());
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
                            typeThough = typeThough || StringUtils.equals(type, user.getDeviceType());
                        }
                        isFilter = isFilter && typeThough;
                    }
                }
            }

            if (isFilter) {
                JSONObject param = new JSONObject();
                param.put("name", user.getName());
                param.put("state", "online");
                param.put("connectType", user.getConnectType());
                param.put("protocolType", user.getAccept());
                param.put("deviceType", user.getDeviceType());
                responseBody.addParam(param);
            }
        }

        response.setBody(responseBody);
        responses.add(response);
    }
}
