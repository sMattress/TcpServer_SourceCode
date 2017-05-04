package controller.user;

import model.ApplicationMsg;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import remote.WebServer;
import wtf.socket.controller.WTFSocketSimpleController;
import wtf.socket.exception.WTFSocketException;
import wtf.socket.exception.fatal.WTFSocketInvalidSourceException;
import wtf.socket.protocol.WTFSocketMessage;
import wtf.socket.routing.client.WTFSocketClient;
import wtf.socket.routing.client.WTFSocketTmpClient;
import wtf.socket.workflow.response.WTFSocketResponse;

/**
 * 注册功能
 */
@Controller
public class RegisterController implements WTFSocketSimpleController {

    @Override
    public boolean isResponse(WTFSocketMessage msg) {
        ApplicationMsg body = msg.getBody(ApplicationMsg.class);
        return body.getCmd() != null &&
                body.getCmd() == 64;
    }

    public boolean work(WTFSocketClient source, WTFSocketMessage msg, WTFSocketResponse response) throws WTFSocketException{

        final ApplicationMsg body = msg.getBody(ApplicationMsg.class);

        if (!(source instanceof WTFSocketTmpClient)) {
            throw new WTFSocketInvalidSourceException(msg.getFrom());
        }

        source.setAddress(msg.getFrom());
        source.setAccept(msg.getVersion());

        if (body.hasParams())
            source.setDeviceType(body.firstParam().getString("deviceType"));

        if (StringUtils.startsWith(msg.getFrom(), "Debug_")) {
            ((WTFSocketTmpClient) source).shiftToDebug();
        }else {
            ((WTFSocketTmpClient) source).shiftToFormal();
            WebServer.INSTANCE.hardwareOnline(msg.getFrom());
        }

        final WTFSocketMessage message = msg.makeResponse();
        message.setBody(ApplicationMsg.success());
        response.addMessage(message);

        return true;
    }
}
