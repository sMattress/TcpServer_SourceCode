package application.controller.user;

import application.model.ApplicationMsg;
import org.springframework.stereotype.Controller;
import wtf.socket.controller.WTFSocketController;

import wtf.socket.WTFSocket;
import wtf.socket.protocol.WTFSocketMsg;
import wtf.socket.routing.item.WTFSocketRoutingFormalItem;

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

    public void work(WTFSocketMsg msg, List<WTFSocketMsg> responses) {

        final WTFSocketRoutingFormalItem item = WTFSocket.ROUTING.FORMAL_MAP.getItem(msg.getFrom());
        if (item != null)
            item.logout();

        final WTFSocketMsg response = msg.makeResponse();
        response.setBody(new ApplicationMsg().setFlag(1));
        responses.add(response);
    }
}
