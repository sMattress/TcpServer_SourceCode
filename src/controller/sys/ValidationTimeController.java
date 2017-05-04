package controller.sys;

import com.alibaba.fastjson.JSONObject;
import model.ApplicationMsg;
import org.springframework.stereotype.Controller;
import wtf.socket.controller.WTFSocketSimpleController;
import wtf.socket.protocol.WTFSocketMessage;
import wtf.socket.routing.client.WTFSocketClient;
import wtf.socket.workflow.response.WTFSocketResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class ValidationTimeController implements WTFSocketSimpleController {

    @Override
    public boolean isResponse(WTFSocketMessage msg) {
        final ApplicationMsg body = msg.getBody(ApplicationMsg.class);
        return body.getCmd() != null &&
                body.getCmd() == 66;
    }

    public boolean work(WTFSocketClient item, WTFSocketMessage msg, WTFSocketResponse response) {

        final DateFormat df = new SimpleDateFormat("HH-mm-ss");
        final String time[] = df.format(new Date()).split("-");
        final int hour = Integer.valueOf(time[0]);
        final int minute = Integer.valueOf(time[1]);
        final int second = Integer.valueOf(time[2]);

        final WTFSocketMessage message = msg.makeResponse();
        message.setBody(new ApplicationMsg()
                .setFlag(1)
                .addParam(new JSONObject() {{
                    put("hour", hour);
                    put("minute", minute);
                    put("second", second);
        }}));
        response.addMessage(message);

        return true;
    }
}
