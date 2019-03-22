package com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp;

import android.os.Handler;
import android.os.Looper;

import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.lib.GroupGameTcp;
import com.xueersi.parentsmeeting.modules.livevideo.lib.ReceiveMegCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.lib.TcpConstants;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;

import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class TcpDispatch {
    private Logger logger = LiveLoggerFactory.getLogger("TcpDispatch");
    private ArrayList<InetSocketAddress> addresses = new ArrayList<>();
    private GroupGameTcp groupGameTcp;
    private int addressIndex = 0;
    private String stuId;
    private String xes_rfh;
    private String live_id;
    private String class_id;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isStop = false;
    LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();

    public TcpDispatch(String stuId, String xes_rfh, String live_id, String class_id) {
        this.stuId = stuId;
        this.xes_rfh = xes_rfh;
        this.live_id = live_id;
        this.class_id = class_id;
    }

    public void setAddresses(ArrayList<InetSocketAddress> addresses) {
        this.addresses = addresses;
        InetSocketAddress inetSocketAddress = addresses.get(addressIndex++);
        logger.d("setAddresses:inetSocketAddress=" + inetSocketAddress);
        groupGameTcp = new GroupGameTcp(inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        groupGameTcp.setReceiveMegCallBack(receiveMegCallBack);
        groupGameTcp.start();
    }

    public void stop() {
        isStop = true;
        if (groupGameTcp != null) {
            groupGameTcp.stop();
        }
    }

    private ReceiveMegCallBack receiveMegCallBack = new ReceiveMegCallBack() {

        @Override
        public void onConnect(GroupGameTcp oldGroupGameTcp) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("uid", stuId);
                jsonObject.put("role", 1);
                jsonObject.put("xes_rfh", xes_rfh);
                jsonObject.put("live_id", live_id);
                jsonObject.put("class_id", class_id);
                String bodyStr = jsonObject.toString();
                short type = TcpConstants.LOGIN_TYPE;
                int operation = TcpConstants.LOGIN_OPERATION_SEND;
                groupGameTcp.send(type, operation, bodyStr);
            } catch (Exception e) {

            }
        }

        @Override
        public void onReceiveMeg(short type, int operation, String msg) {
            logger.d("onReceiveMeg:type=" + type + ",operation=" + operation + ",msg=" + msg);
            if (type == TcpConstants.LOGIN_TYPE) {
                if (operation == TcpConstants.LOGIN_OPERATION_REC) {

                }
            }
        }

        @Override
        public void onDisconnect(GroupGameTcp oldGroupGameTcp) {
            oldGroupGameTcp.stop();
            if (isStop) {
                return;
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    liveThreadPoolExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            InetSocketAddress inetSocketAddress = addresses.get(addressIndex++ % addresses.size());
                            groupGameTcp = new GroupGameTcp(inetSocketAddress.getHostName(), inetSocketAddress.getPort());
                            groupGameTcp.setReceiveMegCallBack(receiveMegCallBack);
                            groupGameTcp.start();
                        }
                    });
                }
            }, 1000);
        }
    };
}
