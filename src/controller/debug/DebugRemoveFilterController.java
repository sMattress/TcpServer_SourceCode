package controller.debug;

import model.ApplicationMsg;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import wtf.socket.controller.WTFSocketController;
import wtf.socket.protocol.WTFSocketMsg;
import wtf.socket.routing.item.WTFSocketRoutingDebugItem;
import wtf.socket.routing.item.WTFSocketRoutingItem;

import java.util.List;

@Controller
public class DebugRemoveFilterController implements WTFSocketController {

    @Override
    public boolean isResponse(WTFSocketMsg msg) {
        final ApplicationMsg body = msg.getBody(ApplicationMsg.class);
        return StringUtils.startsWith(msg.getFrom(), "Debug_") &&
                body.getCmd() != null &&
                body.getCmd() == 129;
    }

    public void work(WTFSocketRoutingItem item, WTFSocketMsg msg, List<WTFSocketMsg> responses) {

        final ApplicationMsg body = msg.getBody(ApplicationMsg.class);

        for (int i = 0 ; i < body.getParams().size(); i++) {
            String name = body.getParams().getString(i);
            ((WTFSocketRoutingDebugItem) item).removeFilterGrep(name);
        }

        WTFSocketMsg response = msg.makeResponse();
        response.setBody(new ApplicationMsg().setFlag(1));
        responses.add(response);
    }
}
