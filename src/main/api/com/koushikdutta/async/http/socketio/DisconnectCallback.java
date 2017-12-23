package com.koushikdutta.async.http.socketio;

/**
 * Created by koush on 7/2/13.
 */
public interface DisconnectCallback {
    /**
     * @param fromRemote 是不是从远程消息来的。
     * @param e
     */
    void onDisconnect(boolean fromRemote, Exception e);
}
