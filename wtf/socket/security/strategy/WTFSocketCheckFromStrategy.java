package wtf.socket.security.strategy;

import org.apache.commons.lang.StringUtils;
import wtf.socket.WTFSocket;
import wtf.socket.exception.WTFSocketFakeSourceException;
import wtf.socket.exception.WTFSocketInvalidSourceException;
import wtf.socket.protocol.WTFSocketMsg;
import wtf.socket.routing.item.WTFSocketRoutingFormalItem;

/**
 *
 * Created by zfly on 2017/4/25.
 */
@FunctionalInterface
public interface WTFSocketCheckFromStrategy extends WTFSocketSecurityStrategy {

    default void fakeServer(WTFSocketMsg msg) throws WTFSocketFakeSourceException {
        if (StringUtils.equals("server", msg.getFrom()))
            throw new WTFSocketFakeSourceException(msg.getFrom());
    }

    default void containsFrom(WTFSocketMsg msg) throws WTFSocketInvalidSourceException{
        if (!WTFSocket.ROUTING.getFormalMap().contains(msg.getFrom()))
            throw new WTFSocketInvalidSourceException(msg.getFrom());
    }

    default void fakeFrom(WTFSocketMsg msg) throws WTFSocketFakeSourceException {
        final WTFSocketRoutingFormalItem source = (WTFSocketRoutingFormalItem) WTFSocket.ROUTING.getFormalMap().getItem(msg.getFrom());
        if (source == null || !StringUtils.equals(source.getTerm().getIoTag(), msg.getIoTag()))
            throw new WTFSocketFakeSourceException(msg.getFrom());
    }
}
