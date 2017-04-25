package wtf.socket.event;

/**
 *
 * Created by zfly on 2017/4/25.
 */
public class WTFSocketEventsGroup {

    private WTFSocketDisconnectEventListener disconnectEventListener = item -> {
        // do nothing
    };

    public WTFSocketDisconnectEventListener getDisconnectEventListener() {
        return disconnectEventListener;
    }

    public void setDisconnectEventListener(WTFSocketDisconnectEventListener disconnectEventListener) {
        this.disconnectEventListener = disconnectEventListener;
    }
}
