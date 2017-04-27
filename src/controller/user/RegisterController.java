package controller.user;

import model.ApplicationMsg;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import remote.WebServer;
import wtf.socket.controller.WTFSocketController;
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

    public void work(WTFSocketRoutingItem item, WTFSocketMsg msg, List<WTFSocketMsg> responses) {

        final ApplicationMsg body = msg.getBody(ApplicationMsg.class);

        if (!(item instanceof WTFSocketRoutingTmpItem)) {
            final WTFSocketMsg response = msg.makeResponse();
            response.setBody(ApplicationMsg.failure(128, "Had registered <" + item.getAddress() + ">"));
            responses.add(response);
            return;
        }

        item.setAddress(msg.getFrom());
        item.setAccept(msg.getVersion());

        if (body.hasParams())
            item.setType(body.firstParam().getString("deviceType"));

        if (StringUtils.startsWith(msg.getFrom(), "Debug_")) {
            ((WTFSocketRoutingTmpItem) item).shiftToDebug();
        }else {
            ((WTFSocketRoutingTmpItem) item).shiftToFormal();
            WebServer.INSTANCE.hardwareOnline(msg.getFrom());
        }

        final WTFSocketMsg response = msg.makeResponse();
        response.setBody(ApplicationMsg.success());
        responses.add(response);
    }
}
