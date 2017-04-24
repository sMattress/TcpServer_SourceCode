package application.actions.sys;

import application.model.AppMsg;
import com.alibaba.fastjson.JSONObject;
import wtf.apis.WTFSocketAPIsAction;
import wtf.socket.protocol.WTFSocketMsg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CheckoutTimeAction_1_0 implements WTFSocketAPIsAction {

    public void doAction(WTFSocketMsg msg, List<WTFSocketMsg> responses) {

        final DateFormat df = new SimpleDateFormat("HH-mm-ss");
        final String time[] = df.format(new Date()).split("-");
        final int hour = Integer.valueOf(time[0]);
        final int minute = Integer.valueOf(time[1]);
        final int second = Integer.valueOf(time[2]);

        final WTFSocketMsg response = msg.makeResponse();
        response.setBody(new AppMsg()
                .setFlag(1)
                .addParam(new JSONObject() {{
                    put("hour", hour);
                    put("minute", minute);
                    put("second", second);
        }}));
        responses.add(response);
    }
}
