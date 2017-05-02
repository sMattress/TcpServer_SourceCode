package controller.user;

import model.ApplicationMsg;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import remote.WebServer;
import wtf.socket.controller.WTFSocketController;
import wtf.socket.exception.WTFSocketException;
import wtf.socket.exception.fatal.WTFSocketInvalidSourceException;
import wtf.socket.protocol.WTFSocketMsg;
import wtf.socket.routing.item.WTFSocketRoutingItem;
import wtf.socket.routing.item.WTFSocketRoutingTmpItem;

import java.rmi.server.RemoteServer;
import java.util.List;

/**
 * 注册功能
 */
@Controller
public class RegisterController implements WTFSocketController {

    @Override
    public boolean isResponse(WTFSocketMsg msg) {
        ApplicationMsg body = msg.getBody(ApplicationMsg.class);
        return body.getCmd() != null &&
                body.getCmd() == 64;
    }

    public boolean work(WTFSocketRoutingItem item, WTFSocketMsg msg, List<WTFSocketMsg> responses) throws WTFSocketException{

        final ApplicationMsg body = msg.getBody(ApplicationMsg.class);

        if (!(item instanceof WTFSocketRoutingTmpItem)) {
            throw new WTFSocketInvalidSourceException(msg.getFrom());
        }

        item.setAddress(msg.getFrom());
        item.setAccept(msg.getVersion());

        if (body.hasParams())
            item.setDeviceType(body.firstParam().getString("deviceType"));

        if (StringUtils.startsWith(msg.getFrom(), "Debug_")) {
            ((WTFSocketRoutingTmpItem) item).shiftToDebug();
        }else {
            ((WTFSocketRoutingTmpItem) item).shiftToFormal();
            WebServer.INSTANCE.hardwareOnline(msg.getFrom());
        }

        final WTFSocketMsg response = msg.makeResponse();
        response.setBody(ApplicationMsg.success());
        responses.add(response);

        return true;
    }
}
