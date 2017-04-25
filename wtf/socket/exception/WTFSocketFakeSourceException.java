package wtf.socket.exception;

/**
 *
 * Created by zfly on 2017/4/25.
 */
public class WTFSocketFakeFromException extends WTFSocketCommonException{
    public WTFSocketFakeFromException(String msg) {
        super("Fake source => <" + msg + ">");
    }

    @Override
    public int getErrCode() {
        return 69;
    }
}
