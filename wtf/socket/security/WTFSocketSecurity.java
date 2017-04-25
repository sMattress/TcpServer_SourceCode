package wtf.socket.security;

import wtf.socket.security.strategy.WTFSocketCheckFromStrategy;
import wtf.socket.security.strategy.WTFSocketCheckToStrategy;
import wtf.socket.security.strategy.WTFSocketSendPermissionStrategy;

/**
 *
 * Created by zfly on 2017/4/24.
 */
public class WTFSocketSecurity {

    private WTFSocketSendPermissionStrategy sendPermissionStrategy = msg -> true;

    private WTFSocketCheckFromStrategy checkFromStrategy = msg -> false;

    private WTFSocketCheckToStrategy checkToStrategy = msg -> false;

    public WTFSocketSendPermissionStrategy getSendPermissionStrategy() {
        return sendPermissionStrategy;
    }

    public void setSendPermissionStrategy(WTFSocketSendPermissionStrategy sendPermissionStrategy) {
        this.sendPermissionStrategy = sendPermissionStrategy;
    }

    public WTFSocketCheckFromStrategy getCheckFromStrategy() {
        return checkFromStrategy;
    }

    public void setCheckFromStrategy(WTFSocketCheckFromStrategy checkFromStrategy) {
        this.checkFromStrategy = checkFromStrategy;
    }

    public WTFSocketCheckToStrategy getCheckToStrategy() {
        return checkToStrategy;
    }

    public void setCheckToStrategy(WTFSocketCheckToStrategy checkToStrategy) {
        this.checkToStrategy = checkToStrategy;
    }
}
