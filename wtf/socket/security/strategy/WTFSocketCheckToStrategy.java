package wtf.socket.security;

import wtf.socket.WTFSocket;
import wtf.socket.exception.WTFSocketInvalidTargetException;
import wtf.socket.protocol.WTFSocketMsg;

/**
 *
 * Created by zfly on 2017/4/25.
 */
@FunctionalInterface
public interface WTFSocketCheckToStrategy extends WTFSocketSecurityStrategy {

    default void containsTo(WTFSocketMsg msg) throws WTFSocketInvalidTargetException {
        if (!WTFSocket.ROUTING.getFormalMap().contains(msg.getTo()))
            throw (WTFSocketInvalidTargetException) new WTFSocketInvalidTargetException(msg.getTo()).setOriginalMsg(msg);
    }

}
