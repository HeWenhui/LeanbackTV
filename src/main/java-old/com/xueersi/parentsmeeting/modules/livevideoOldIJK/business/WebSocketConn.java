package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

import android.text.TextUtils;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

/**
 * webSocket发送类
 * Created by ZouHao on 2017/8/2.
 */
public class WebSocketConn {
    static String TAG = "WebSocketASR";
    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private WebSocketClient mWebSocketClient;

    private WebSocketCallBack mCallBack;

    /** 连接超时 */
    private static final int CONNECTING_TIME_OUT = 15000;

    public WebSocketConn() {
    }

    /**
     * 连网回调
     */
    public interface WebSocketCallBack {
        void onOpen();

        void onMessage(String result);

        void onClose();

        void onError(Throwable throwable);
    }

    public void connect(String url, WebSocketCallBack callBack) {
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        if (callBack == null || TextUtils.isEmpty(url)) {
            return;
        }
        mCallBack = callBack;
        try {
            if (mWebSocketClient != null) {
                mWebSocketClient.close();
            }

            mWebSocketClient = new WebSocketClient(new URI(url)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    mCallBack.onOpen();
                }

                @Override
                public void onMessage(String message) {
                    mCallBack.onMessage(message);
                }

                @Override
                public void onMessage(ByteBuffer bytes) {
                    mCallBack.onMessage(getString(bytes));
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    mCallBack.onClose();
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                    logger.i( "onError : "+ex.toString());
                    mCallBack.onError(ex);
                }
            };
            if (url.startsWith("wss")) {
                //如果是wss的协议
                SSLContext sslContext = null;
                try {
                    sslContext = SSLContext.getInstance("TLS");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                try {
                    sslContext.init(null, new TrustManager[]{
                            new X509TrustManager() {

                                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                                }

                                @Override
                                public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                                        throws CertificateException {
                                }

                                public void checkServerTrusted(X509Certificate[] certs,
                                                               String authType) {
                                }

                                @Override
                                public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                                        throws CertificateException {
                                }

                                @Override
                                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                    return null;
                                }
                            }
                    }, new SecureRandom());
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                }
                SSLSocketFactory factory = sslContext.getSocketFactory();
                mWebSocketClient.setSocket(factory.createSocket());
            }
            mWebSocketClient.connect();
            mWebSocketClient.setConnectionLostTimeout(CONNECTING_TIME_OUT);
        } catch (Exception e) {
            mCallBack.onError(e);
        }

    }

    private static String getString(ByteBuffer buffer) {

        Charset charset = null;

        CharsetDecoder decoder = null;

        CharBuffer charBuffer = null;

        try {
            charset = Charset.forName("UTF-8");
            decoder = charset.newDecoder();
            charBuffer = decoder.decode(buffer.asReadOnlyBuffer());

            return charBuffer.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
            return "error";

        }
    }

    /**
     * 发送
     *
     * @param data
     */
    public void sendMsg(byte[] data) {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            mWebSocketClient.send(data);
        } else {
            mCallBack.onError(null);
        }
    }

    public void close() {
        if (mWebSocketClient != null && mWebSocketClient.isOpen()) {
            mWebSocketClient.close();
        }
    }

    public boolean isOpen() {
        return mWebSocketClient != null && mWebSocketClient.isOpen();
    }
}
