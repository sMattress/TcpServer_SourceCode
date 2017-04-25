package wtf.socket.event;

/**
 *
 * Created by zfly on 2017/4/25.
 */
public class WTFSocketEventGroup {

    private WTFSocketDisconnectEventListener disconnectEventListener;
    
    public WTFSocketDisconnectEventListener getDisconnectEventListener() {
        return disconnectEventListener;
    }

    public void setDisconnectEventListener(WTFSocketDisconnectEventListener disconnectEventListener) {
        this.disconnectEventListener = disconnectEventListener;
    }
}
