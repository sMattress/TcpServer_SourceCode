package application;

import application.actions.debug.*;
import application.actions.sys.CheckoutTimeAction_1_0;
import application.actions.user.RegisterAction_1_0;
import application.actions.user.UnregisterAction_1_0;
import okhttp3.*;
import wtf.apis.WTFSocketAPIsManager;
import wtf.apis.WTFSocketAPIsTrigger;
import application.model.AppMsg;

import wtf.socket.Listener.WTFSocketHeartbeatBreakListener;
import wtf.socket.main.WTFSocketConfig;
import wtf.socket.main.WTFSocketServer;

import wtf.socket.protocols.templates.WTFSocketProtocol;
import wtf.socket.protocols.templates.WTFSocketProtocol_1_0;
import wtf.socket.protocols.templates.WTFSocketProtocol_2_0;

import wtf.socket.protocols.parser.WTFSocketProtocolParser;
import wtf.socket.registry.WTFSocketRegistry;
import wtf.socket.registry.items.WTFSocketRegistryItem;

import java.io.IOException;


public class MyApplication {

    public static void main(String[] args) {

        initProtocolParser();
        initApplication();

        WTFSocketServer.addHeartbeatBreakListener(
                new WTFSocketHeartbeatBreakListener() {
                    public void heartbeatBreak(WTFSocketRegistryItem item) {
                        String url = "http://smartmattress.lesmarthome.com/v1/hardware/disconnect?name=" + item.getName();
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
                });

        WTFSocketServer.run(
                new WTFSocketConfig()
                .setTcpPort(1234)
                .setWebSocketPort(4321)
                .setUseDebug(true)
                .setCheckHeartbeat(true)
        );
    }

    private static void initProtocolParser() {
        WTFSocketProtocolParser.addProtocol(WTFSocketProtocol_1_0.class);
        WTFSocketProtocolParser.addProtocol(WTFSocketProtocol_2_0.class);
    }

    private static void initApplication() {

        WTFSocketAPIsManager apis = new WTFSocketAPIsManager();

        apis.createVersion("1.0")

                /* 注册操作 cmd: 64 */
                .addAction(new WTFSocketAPIsTrigger() {
                    @Override
                    public boolean when(WTFSocketProtocol protocol) {
                        AppMsg body = protocol.getBody(AppMsg.class);
                        return body.getCmd() != null &&
                                body.getCmd() == 64;
                    }
                }, RegisterAction_1_0.class)

                /* 注销操作 cmd: 65 */
                .addAction(new WTFSocketAPIsTrigger() {
                    @Override
                    public boolean when(WTFSocketProtocol protocol) {
                        AppMsg body = protocol.getBody(AppMsg.class);
                        return body.getCmd() != null &&
                                body.getCmd() == 65;
                    }
                }, UnregisterAction_1_0.class)

                /* 校时操作 cmd: 66 */
                .addAction(new WTFSocketAPIsTrigger() {
                    @Override
                    public boolean when(WTFSocketProtocol protocol) {
                        AppMsg body = protocol.getBody(AppMsg.class);
                        return body.getCmd() != null &&
                                body.getCmd() == 66;
                    }
                }, CheckoutTimeAction_1_0.class)

                /* 为Debug账号添加过滤规则 cmd: 128 */
                .addAction(new WTFSocketAPIsTrigger() {
                    @Override
                    public boolean when(WTFSocketProtocol protocol) {
                        AppMsg body = protocol.getBody(AppMsg.class);
                        return WTFSocketRegistry.isDebug(protocol.getFrom()) &&
                                body.getCmd() != null &&
                                body.getCmd() == 128;
                    }
                }, DebugAddFilterAction_1_0.class)

                /* 删除Debug账号过滤规则 cmd: 129 */
                .addAction(new WTFSocketAPIsTrigger() {
                    @Override
                    public boolean when(WTFSocketProtocol protocol) {
                        AppMsg body = protocol.getBody(AppMsg.class);
                        return WTFSocketRegistry.isDebug(protocol.getFrom()) &&
                                body.getCmd() != null &&
                                body.getCmd() == 129;
                    }
                }, DebugRemoveFilterAction_1_0.class)

                /* 查询指定用户是否在线 cmd: 130 */
                .addAction(new WTFSocketAPIsTrigger() {
                    @Override
                    public boolean when(WTFSocketProtocol protocol) {
                        AppMsg body = protocol.getBody(AppMsg.class);
                        return WTFSocketRegistry.isDebug(protocol.getFrom()) &&
                                body.getCmd() != null &&
                                body.getCmd() == 130;
                    }
                }, DebugCheckUserActiveAction_1_0.class)

                /* 查询所有在线用户 cmd: 131 */
                .addAction(new WTFSocketAPIsTrigger() {
                    @Override
                    public boolean when(WTFSocketProtocol protocol) {
                        AppMsg body = protocol.getBody(AppMsg.class);
                        return WTFSocketRegistry.isDebug(protocol.getFrom()) &&
                                body.getCmd() != null &&
                                body.getCmd() == 131;
                    }
                }, DebugListAllUsersAction_1_0.class)

                /* 为Debug账户打开输出心跳包功能 cmd: 132 */
                .addAction(new WTFSocketAPIsTrigger() {
                    @Override
                    public boolean when(WTFSocketProtocol protocol) {
                        AppMsg body = protocol.getBody(AppMsg.class);
                        return WTFSocketRegistry.isDebug(protocol.getFrom()) &&
                                body.getCmd() != null &&
                                body.getCmd() == 132;
                    }
                }, DebugOpenShowHeartbeatAction_1_0.class)

                /* 为Debug账户关闭输出心跳包功能 cmd: 133 */
                .addAction(new WTFSocketAPIsTrigger() {
                    @Override
                    public boolean when(WTFSocketProtocol protocol) {
                        AppMsg body = protocol.getBody(AppMsg.class);
                        return WTFSocketRegistry.isDebug(protocol.getFrom()) &&
                                body.getCmd() != null &&
                                body.getCmd() == 133;
                    }
                }, DebugOpenShowHeartbeatAction_1_0.class);

        WTFSocketServer.setHandler(apis);
    }
}
