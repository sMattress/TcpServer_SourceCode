package controller.user;

import model.ApplicationMsg;
import org.springframework.stereotype.Controller;
import wtf.socket.controller.WTFSocketController;

import wtf.socket.WTFSocket;
import wtf.socket.exception.WTFSocketException;
import wtf.socket.protocol.WTFSocketMsg;
import wtf.socket.routing.item.WTFSocketRoutingFormalItem;
import wtf.socket.routing.item.WTFSocketRoutingItem;
import wtf.socket.routing.item.WTFSocketRoutingTmpItem;

import java.util.List;

/**
 * 注销功能
 */
@Controller
public class UnRegisterController implements WTFSocketController {

    @Override
    public boolean isResponse(WTFSocketMsg msg) {
        ApplicationMsg body = msg.getBody(ApplicationMsg.class);
        return body.getCmd() != null &&
                body.getCmd() == 65;
    }

    public void work(WTFSocketRoutingItem item, WTFSocketMsg msg, List<WTFSocketMsg> responses) throws WTFSocketException{
        if (item != null)
            item.logout();

        final WTFSocketMsg response = msg.makeResponse();
        response.setBody(new ApplicationMsg().setFlag(1));
        responses.add(response);
    }
}
