package controller.sys;

import model.ApplicationMsg;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import wtf.socket.controller.WTFSocketController;
import wtf.socket.protocol.WTFSocketMsg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class ValidationTimeController implements WTFSocketController {

    @Override
    public boolean isResponse(WTFSocketMsg msg) {
        ApplicationMsg body = msg.getBody(ApplicationMsg.class);
        return body.getCmd() != null &&
                body.getCmd() == 66;
    }

    public void work(WTFSocketMsg msg, List<WTFSocketMsg> responses) {

        final DateFormat df = new SimpleDateFormat("HH-mm-ss");
        final String time[] = df.format(new Date()).split("-");
        final int hour = Integer.valueOf(time[0]);
        final int minute = Integer.valueOf(time[1]);
        final int second = Integer.valueOf(time[2]);

        final WTFSocketMsg response = msg.makeResponse();
        response.setBody(new ApplicationMsg()
                .setFlag(1)
                .addParam(new JSONObject() {{
                    put("hour", hour);
                    put("minute", minute);
                    put("second", second);
        }}));
        responses.add(response);
    }
}
