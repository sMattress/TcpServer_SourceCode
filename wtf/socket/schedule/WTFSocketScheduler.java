package wtf.socket.schedule;

import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import wtf.socket.WTFSocket;
import wtf.socket.util.WTFSocketLogUtils;
import wtf.socket.exception.*;
import wtf.socket.io.netty.WTFSocketTCPInitializer;
import wtf.socket.io.netty.WTFSocketWebSocketInitializer;
import wtf.socket.protocol.WTFSocketMsg;
import wtf.socket.protocol.WTFSocketConnectType;
import wtf.socket.routing.WTFSocketCleaner;
import wtf.socket.routing.item.WTFSocketRoutingItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 调度器
 */
public class WTFSocketScheduler {

    /**
     * 服务器配置
     */
    private WTFSocketConfig config = null;

    /**
     * 消息处理接口
     */
    private WTFSocketHandler handler = (request, response) -> {
        // nothing to do
    };

    /**
     * 提交一个数据包
     *
     * @param packet 数据包
     * @param ioTag 提交数据包的io的tag
     * @param connectType 提交数据的io的连接类型
     * @throws WTFSocketFatalException 致命异常
     */
    public void submit(String packet, String ioTag, WTFSocketConnectType connectType) throws WTFSocketFatalException{
        try {
            final WTFSocketMsg msg = WTFSocket.PROTOCOL_FAMILY.parseMsgFromString(packet);
            msg.setConnectType(connectType);
            msg.setIoTag(ioTag);

            // 数据源的地址不能为 server
            // server 为服务器保留地址
            WTFSocket.SECURITY.getCheckFromStrategy().fakeServer(msg);

            WTFSocketLogUtils.receive(packet, msg);

            final List<WTFSocketMsg> responses = new ArrayList<>();
            handler.invoke(msg, responses);

            if (responses.isEmpty()) {
                sendMsg(msg);
            }else {
                sendMsg(responses);
            }

        } catch (WTFSocketCommonException e) {
            final WTFSocketMsg errResponse = e.getOriginalMsg().makeResponse();
            errResponse.setFrom("server");
            errResponse.setState(e.getErrCode());
            errResponse.setBody(new JSONObject() {{
                put("cause", e.getMessage());
            }});

            final String data = WTFSocket.PROTOCOL_FAMILY.packageMsgToString(errResponse);
            WTFSocketLogUtils.exception(data, errResponse);
            if (WTFSocket.ROUTING.getFormalMap().contains(errResponse.getTo())) {
                WTFSocket.ROUTING.getFormalMap().getItem(errResponse.getTo()).getTerm().write(data);
            }
        }
    }

    /**
     * 发送消息
     *
     * @param msg 消息对象
     * @throws WTFSocketInvalidSourceException 无效的消息源
     * @throws WTFSocketInvalidTargetException 无效的消息目标
     * @throws WTFSocketUnsupportedProtocolException 不被支持的协议
     * @throws WTFSocketPermissionDeniedException 无发送权限
     */
    public void sendMsg(WTFSocketMsg msg) throws WTFSocketFakeSourceException, WTFSocketInvalidSourceException, WTFSocketInvalidTargetException, WTFSocketUnsupportedProtocolException, WTFSocketPermissionDeniedException {

        WTFSocketRoutingItem target;

        // 目标为DEBUG对象
        if (config.isUseDebug() && WTFSocket.ROUTING.getDebugMap().contains(msg.getTo())) {
            target = WTFSocket.ROUTING.getDebugMap().getItem(msg.getTo());
        }else {
            WTFSocket.SECURITY.getCheckFromStrategy().containsFrom(msg);
            WTFSocket.SECURITY.getCheckFromStrategy().fakeFrom(msg);
            WTFSocket.SECURITY.getSendPermissionStrategy().sendPermission(msg);
            WTFSocket.SECURITY.getCheckToStrategy().containsTo(msg);
            target = WTFSocket.ROUTING.getFormalMap().getItem(msg.getTo());
        }

        msg.setVersion(target.getAccept());
        final String data = WTFSocket.PROTOCOL_FAMILY.packageMsgToString(msg);

        if (config.isUseDebug()) {
            WTFSocketLogUtils.dispatch(data, msg);
        }
        target.getTerm().write(data);
    }

    /**
     * 发送一组消息
     *
     * @param msgs 消息对象数组
     * @throws WTFSocketInvalidSourceException 无效的消息源
     * @throws WTFSocketInvalidTargetException 无效的消息目标
     * @throws WTFSocketUnsupportedProtocolException 不被支持的协议
     * @throws WTFSocketPermissionDeniedException 无发送权限
     */
    public void sendMsg(List<WTFSocketMsg> msgs) throws WTFSocketFakeSourceException, WTFSocketInvalidSourceException, WTFSocketInvalidTargetException, WTFSocketUnsupportedProtocolException, WTFSocketPermissionDeniedException {
        for (WTFSocketMsg protocol : msgs) {
            sendMsg(protocol);
        }
    }

    /**
     * 添加处理器
     *
     * @param handler 处理器
     */
    public void setHandler(WTFSocketHandler handler) {
        if (handler != null) {
            this.handler = handler;
        }
    }

    /**
     * 获取处理器
     *
     * @return 处理器
     */
    public WTFSocketHandler getHandler() {
        return handler;
    }

    /**
     * 移除处理器
     */
    public void removeHandler() {
        handler = (request, response) -> {
            // nothing to do
        };
    }

    /**
     * 启动框架
     *
     * @param config 启动配置
     */
    public void run(WTFSocketConfig config) {

        if (config == null) {
            return;
        }

        this.config = config;

        if (config.getTcpPort() > 0) {
            startTcpServer(config.getTcpPort());
        }

        if (config.getWebSocketPort() > 0) {
            startWebSocketServer(config.getWebSocketPort());
        }

        WTFSocketCleaner.runExpire();
    }

    /**
     * 获取框架配置
     *
     * @return 框架配置
     */
    public WTFSocketConfig getConfig() {
        return config;
    }

    // 开启WebSocket服务器
    private void startWebSocketServer(int port) {
        startServer(
                port,
                new WTFSocketWebSocketInitializer()
        );
    }

    // 开启TCP服务器
    private void startTcpServer(int port) {
        startServer(
                port,
                new WTFSocketTCPInitializer()
        );
    }

    // 开启服务器
    private void startServer(final int port, final ChannelInitializer initializer) {

        new Thread(() -> {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(initializer)
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                ChannelFuture f = b.bind(port).sync();

                f.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }).start();

    }
}
