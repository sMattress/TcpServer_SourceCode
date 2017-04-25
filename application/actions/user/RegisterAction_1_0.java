package application.actions.user;

import application.model.AppMsg;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.commons.lang.StringUtils;
import wtf.apis.WTFSocketAPIsAction;
import wtf.socket.WTFSocket;
import wtf.socket.protocol.WTFSocketMsg;

import wtf.socket.routing.item.WTFSocketRoutingItem;

import java.io.IOException;
import java.util.List;

/**
 * 注册功能
 */
public class RegisterAction_1_0 implements WTFSocketAPIsAction {

    public void doAction(WTFSocketMsg msg, List<WTFSocketMsg> responses) {

        final AppMsg body = msg.getBody(AppMsg.class);
        final WTFSocketRoutingItem item = WTFSocket.ROUTING.getTmpMap().getItem(msg.getIoTag());

        if (item == null) {
            final WTFSocketMsg response = msg.makeResponse();
            response.setBody(new AppMsg().setFlag(1));
            responses.add(response);
            return;
        }

        item.setAddress(msg.getFrom());
        item.setAccept(msg.getVersion());

        if (body.getParams() != null) {
            final JSONObject param = body.getParams().getJSONObject(0);
            final String itemType = param.getString("deviceType");
            item.setType(itemType);
        }else {
            item.setType("Unknown");
        }


        if (StringUtils.startsWith(msg.getFrom(), "Debug_")) {
            WTFSocket.ROUTING.getTmpMap().shift(item, WTFSocket.ROUTING.getDebugMap());
        }else {
            WTFSocket.ROUTING.getTmpMap().shift(item, WTFSocket.ROUTING.getFormalMap());
            notifyWeb(msg);
        }

        final WTFSocketMsg response = msg.makeResponse();
        response.setBody(new AppMsg().setFlag(1));
        responses.add(response);
    }

    private void notifyWeb(WTFSocketMsg msg) {
        String url = "http://smartmattress.lesmarthome.com/v1/hardware/connect?name=" + msg.getFrom();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                System.out.println(e.getMessage());
            }

            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("notify connect => " + response.body().string());
            }
        });
    }
}
