package wtf.socket.security;

import wtf.socket.protocol.WTFSocketMsg;

/**
 *
 * Created by zfly on 2017/4/25.
 */
public interface WTFSocketSecurityStrategy {

    boolean invoke(WTFSocketMsg msg);

}
