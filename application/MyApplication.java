package application;

import application.actions.debug.*;
import application.actions.sys.CheckoutTimeAction_1_0;
import application.actions.user.RegisterAction_1_0;
import application.actions.user.UnregisterAction_1_0;
import application.parser.MyMsgParser;
import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.apache.commons.lang.StringUtils;
import wtf.apis.WTFSocketAPIsManager;
import wtf.apis.WTFSocketAPIsTrigger;
import application.model.AppMsg;

import wtf.socket.WTFSocket;
import wtf.socket.schedule.WTFSocketConfig;
import wtf.socket.protocol.WTFSocketMsg;

import java.io.IOException;


public class MyApplication {

    public static void main(String[] args) {

        initApplication();

        WTFSocket.PROTOCOL_FAMILY.registerParser(new MyMsgParser());

        WTFSocket.EVENTS_GROUP.setDisconnectEventListener(item -> {
            String url = "http://smartmattress.lesmarthome.com/v1/hardware/disconnect?name=" + item.getAddress();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                public void onFailure(Call call, IOException e) {
                    System.out.println(e.getMessage());
                }

                public void onResponse(Call call, Response response) throws IOException {
                    System.out.println("notify disconnect => " + response.body().string());
                }
            });
        });

        WTFSocket.SECURITY.setSendPermissionStrategy((msg) -> {
            String url = "http://smartmattress.lesmarthome.com/v1/sys/auth?from=" + msg.getFrom() + "&to=" + msg.getTo();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            try {
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                AppMsg body = JSON.parseObject(data, AppMsg.class);
                System.out.println("auth => " + data);
                return body.getFlag() == 1;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        });

        WTFSocket.run(
                new WTFSocketConfig()
                    .setTcpPort(1234)
                    .setUseDebug(true)
        );
    }


    private static void initApplication() {

        WTFSocketAPIsManager apis = new WTFSocketAPIsManager();

        apis.createVersion("1.0")

                /* 注册操作 cmd: 64 */
                .addAction(new WTFSocketAPIsTrigger() {
                    @Override
                    public boolean when(WTFSocketMsg msg) {
                        AppMsg body = msg.getBody(AppMsg.class);
                        return body.getCmd() != null &&
                                body.getCmd() == 64;
                    }
                }, new RegisterAction_1_0())

                /* 注销操作 cmd: 65 */
                .addAction(new WTFSocketAPIsTrigger() {
                    @Override
                    public boolean when(WTFSocketMsg msg) {
                        AppMsg body = msg.getBody(AppMsg.class);
                        return body.getCmd() != null &&
                                body.getCmd() == 65;
                    }
                }, new UnregisterAction_1_0())

                /* 校时操作 cmd: 66 */
                .addAction(new WTFSocketAPIsTrigger() {
                    @Override
                    public boolean when(WTFSocketMsg msg) {
                        AppMsg body = msg.getBody(AppMsg.class);
                        return body.getCmd() != null &&
                                body.getCmd() == 66;
                    }
                }, new CheckoutTimeAction_1_0())

                /* 为Debug账号添加过滤规则 cmd: 128 */
                .addAction(new WTFSocketAPIsTrigger() {
                    @Override
                    public boolean when(WTFSocketMsg msg) {
                        AppMsg body = msg.getBody(AppMsg.class);
                        return StringUtils.startsWith(msg.getFrom(), "Debug_") &&
                                body.getCmd() != null &&
                                body.getCmd() == 128;
                    }
                }, new DebugAddFilterAction_1_0())

                /* 删除Debug账号过滤规则 cmd: 129 */
                .addAction(new WTFSocketAPIsTrigger() {
                    @Override
                    public boolean when(WTFSocketMsg msg) {
                        AppMsg body = msg.getBody(AppMsg.class);
                        return StringUtils.startsWith(msg.getFrom(), "Debug_") &&
                                body.getCmd() != null &&
                                body.getCmd() == 129;
                    }
                }, new DebugRemoveFilterAction_1_0())

                /* 查询指定用户是否在线 cmd: 130 */
                .addAction(new WTFSocketAPIsTrigger() {
                    @Override
                    public boolean when(WTFSocketMsg msg) {
                        AppMsg body = msg.getBody(AppMsg.class);
                        return StringUtils.startsWith(msg.getFrom(), "Debug_") &&
                                body.getCmd() != null &&
                                body.getCmd() == 130;
                    }
                }, new DebugCheckUserActiveAction_1_0())

                /* 查询所有在线用户 cmd: 131 */
                .addAction(new WTFSocketAPIsTrigger() {
                    @Override
                    public boolean when(WTFSocketMsg msg) {
                        AppMsg body = msg.getBody(AppMsg.class);
                        return StringUtils.startsWith(msg.getFrom(), "Debug_") &&
                                body.getCmd() != null &&
                                body.getCmd() == 131;
                    }
                }, new DebugListAllUsersAction_1_0());

        WTFSocket.SCHEDULER.setHandler(apis);
    }
}
