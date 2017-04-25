package wtf.socket.event;

import wtf.socket.routing.item.WTFSocketRoutingItem;

/**
 * 断开连接监听
 *
 * Created by zfly on 2017/4/24.
 */
@FunctionalInterface
public interface WTFSocketDisconnectEventListener {
    void invoke(WTFSocketRoutingItem item);
}
