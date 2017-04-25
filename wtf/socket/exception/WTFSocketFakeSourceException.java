package wtf.socket.exception;

/**
 *
 * Created by zfly on 2017/4/25.
 */
public class WTFSocketFakeSourceException extends WTFSocketFatalException{
    public WTFSocketFakeSourceException(String msg) {
        super("Fake source => <" + msg + ">");
    }

    @Override
    public int getErrCode() {
        return 69;
    }
}
