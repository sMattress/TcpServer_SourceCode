package wtf.socket.security.strategy;

import wtf.socket.protocol.WTFSocketMsg;

/**
 *
 * Created by zfly on 2017/4/25.
 */
public interface WTFSocketSecurityStrategy {

    boolean invoke(WTFSocketMsg msg);

}
