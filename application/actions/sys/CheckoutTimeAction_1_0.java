package application.actions.sys;

import application.model.AppMsg;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import wtf.apis.WTFSocketAPIsAction;
import wtf.socket.protocols.templates.WTFSocketProtocol;
import wtf.socket.protocols.templates.WTFSocketProtocol_2_0;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CheckoutTimeAction_1_0 implements WTFSocketAPIsAction {

    @Override
    public void doAction(Channel ctx, WTFSocketProtocol protocol, List<WTFSocketProtocol> responses) {

        DateFormat df = new SimpleDateFormat("HH-mm-ss");
        String time[] = df.format(new Date()).split("-");
        int hour = Integer.valueOf(time[0]);
        int minute = Integer.valueOf(time[1]);
        int second = Integer.valueOf(time[2]);
        WTFSocketProtocol_2_0 response = WTFSocketProtocol_2_0.makeResponse(protocol);

        AppMsg msg = new AppMsg();
        msg.setFlag(1);
        JSONObject obj = new JSONObject();
        obj.put("hour", hour);
        obj.put("minute", minute);
        obj.put("second", second);
        msg.addParam(obj);
        response.setBody(msg);

        responses.add(response);
    }
}
