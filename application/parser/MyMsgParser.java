package application.parser;

import org.apache.commons.lang.StringUtils;
import wtf.socket.exception.WTFSocketMsgFormatWrongException;
import wtf.socket.protocol.WTFSocketMsg;
import wtf.socket.protocol.WTFSocketParser;

/**
 *
 * Created by zfly on 2017/4/23.
 */
public class MyMsgParser implements WTFSocketParser{
    @Override
    public boolean isResponse(String data) {
        return StringUtils.startsWith(data,"I");
    }

    @Override
    public boolean isResponse(WTFSocketMsg msg) {
        return false;
    }

    @Override
    public WTFSocketMsg parseMsgFromString(String data) throws WTFSocketMsgFormatWrongException {
        return null;
    }

    @Override
    public String packageMsgToString(WTFSocketMsg msg) {
        return null;
    }
}
