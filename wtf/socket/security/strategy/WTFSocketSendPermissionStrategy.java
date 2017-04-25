package wtf.socket.security;

import org.apache.commons.lang.StringUtils;
import wtf.socket.WTFSocket;
import wtf.socket.exception.WTFSocketPermissionDeniedException;
import wtf.socket.protocol.WTFSocketMsg;
import wtf.socket.routing.item.WTFSocketRoutingFormalItem;

/**
 * 授权器接口
 *
 * Created by zfly on 2017/4/22.
 */
@FunctionalInterface
public interface WTFSocketSendPermissionStrategy extends WTFSocketSecurityStrategy {

    default void sendPermission(WTFSocketMsg msg) throws WTFSocketPermissionDeniedException {

        final WTFSocketRoutingFormalItem source = (WTFSocketRoutingFormalItem) WTFSocket.ROUTING.getFormalMap().getItem(msg.getTo());

        // 权限校验
        if (!StringUtils.equals(msg.getFrom(), "server") && !source.isAuthTarget(msg.getTo())) {
            if (invoke(msg)) {
                source.addAuthTarget(msg.getTo());
            } else {
                throw (WTFSocketPermissionDeniedException) new WTFSocketPermissionDeniedException(msg.getTo()).setOriginalMsg(msg);
            }
        }
    }
}
