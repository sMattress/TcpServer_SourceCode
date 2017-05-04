package controller.user;

import model.ApplicationMsg;
import org.springframework.stereotype.Controller;
import wtf.socket.controller.WTFSocketSimpleController;
import wtf.socket.exception.WTFSocketException;
import wtf.socket.protocol.WTFSocketMessage;
import wtf.socket.routing.client.WTFSocketClient;
import wtf.socket.workflow.response.WTFSocketResponse;

/**
 * 注销功能
 */
@Controller
public class UnRegisterController implements WTFSocketSimpleController {

    @Override
    public boolean isResponse(WTFSocketMessage msg) {
        ApplicationMsg body = msg.getBody(ApplicationMsg.class);
        return body.getCmd() != null &&
                body.getCmd() == 65;
    }

    public boolean work(WTFSocketClient source, WTFSocketMessage msg, WTFSocketResponse response) throws WTFSocketException{
        if (source != null)
            source.close();

        final WTFSocketMessage message = msg.makeResponse();
        message.setBody(new ApplicationMsg().setFlag(1));
        response.addMessage(message);

        return true;
    }
}
