package application.actions.user;

import application.model.AppMsg;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import wtf.apis.WTFSocketAPIsAction;
import wtf.socket.protocols.templates.WTFSocketProtocol;
import wtf.socket.protocols.templates.WTFSocketProtocol_2_0;
import wtf.socket.registry.WTFSocketRegistry;

import io.netty.channel.Channel;

import java.io.IOException;
import java.util.List;

/**
 * 注册功能
 */
public class RegisterAction_1_0 implements WTFSocketAPIsAction {

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
        String url = "http://smartmattress.lesmarthome.com/v1/hardware/connect?name=" + protocol.getFrom();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                System.out.println(e.getMessage());
            }

            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());
            }
        });
    }
}
