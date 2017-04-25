package wtf.socket.routing;

import wtf.socket.io.term.impl.WTFSocketDefaultTerm;
import wtf.socket.routing.item.WTFSocketRoutingFormalItem;

/**
 *
 * Created by zfly on 2017/4/25.
 */
public class WTFSocketRouting {

    private final static WTFSocketRoutingMap TMP = new WTFSocketRoutingMap("TMP");

    private final static WTFSocketRoutingMap FORMAL = new WTFSocketRoutingMap("FORMAL") {{
        register(new WTFSocketRoutingFormalItem(new WTFSocketDefaultTerm()) {{
            // 默认添加 server 对象
            // server 对象代表服务器，不可被覆盖
            setCover(false);
            setAddress("server");
        }});
    }};

    private final static WTFSocketRoutingMap DEBUG = new WTFSocketRoutingMap("DEBUG");

    public WTFSocketRoutingMap getTmpMap() {
        return TMP;
    }

    public WTFSocketRoutingMap getFormalMap() {
        return FORMAL;
    }

    public WTFSocketRoutingMap getDebugMap() {
        return DEBUG;
    }

    public WTFSocketRoutingMap[] values() {
        return new WTFSocketRoutingMap[] {TMP, FORMAL, DEBUG};
    }
}
