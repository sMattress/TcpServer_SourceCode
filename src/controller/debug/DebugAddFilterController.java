package controller.debug;

import model.ApplicationMsg;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import wtf.socket.controller.WTFSocketSimpleController;
import wtf.socket.protocol.WTFSocketMessage;
import wtf.socket.routing.client.WTFSocketClient;
import wtf.socket.routing.client.WTFSocketDebugClient;
import wtf.socket.workflow.response.WTFSocketResponse;

@Controller
public class DebugAddFilterController implements WTFSocketSimpleController {

    @Override
    public boolean isResponse(WTFSocketMessage msg) {
        final ApplicationMsg body = msg.getBody(ApplicationMsg.class);
        return StringUtils.startsWith(msg.getFrom(), "Debug_") &&
                body.getCmd() != null &&
                body.getCmd() == 128;
    }

    public boolean work(WTFSocketClient item, WTFSocketMessage msg, WTFSocketResponse response) {

        final ApplicationMsg body = msg.getBody(ApplicationMsg.class);

        for (int i = 0 ; i < body.getParams().size(); i++) {
            String name = body.getParams().getString(i);
            ((WTFSocketDebugClient) item).addFilterGrep(name);
        }

        final WTFSocketMessage message = msg.makeResponse();
        message.setBody(new ApplicationMsg().setFlag(1));
        response.addMessage(message);
        return true;
    }
}
