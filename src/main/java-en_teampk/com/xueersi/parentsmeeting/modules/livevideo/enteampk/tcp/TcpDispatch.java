package com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp;

import android.os.Handler;
import android.os.Looper;

import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.lib.GroupGameTcp;
import com.xueersi.parentsmeeting.modules.livevideo.lib.ReceiveMegCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class TcpDispatch {
    private Logger logger = LiveLoggerFactory.getLogger("TcpDispatch");
    private ArrayList<InetSocketAddress> addresses = new ArrayList<>();
    private GroupGameTcp groupGameTcp;
    private int addressIndex = 0;
    private String stuId;
    private String xes_rfh;
    private Handler handler = new Handler(Looper.getMainLooper());

    public TcpDispatch(String stuId, String xes_rfh) {
        this.stuId = stuId;
        this.xes_rfh = xes_rfh;
    }

    public void setAddresses(ArrayList<InetSocketAddress> addresses) {
        this.addresses = addresses;
        InetSocketAddress inetSocketAddress = addresses.get(addressIndex++);
        logger.d("setAddresses:inetSocketAddress=" + inetSocketAddress);
        groupGameTcp = new GroupGameTcp(inetSocketAddress.getHostName(), inetSocketAddress.getPort(), stuId, xes_rfh);
        groupGameTcp.setReceiveMegCallBack(receiveMegCallBack);
        groupGameTcp.start();
    }

    public void stop() {
        if (groupGameTcp != null) {
            groupGameTcp.stop();
        }
    }

    private ReceiveMegCallBack receiveMegCallBack = new ReceiveMegCallBack() {
        @Override
        public void onReceiveMeg(short type, int operation, String msg) {
            logger.d("onReceiveMeg:type=" + type + ",operation=" + operation + ",msg=" + msg);
        }

        @Override
        public void onDisconnect(GroupGameTcp oldGroupGameTcp) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InetSocketAddress inetSocketAddress = addresses.get(addressIndex++ % addresses.size());
                    groupGameTcp = new GroupGameTcp(inetSocketAddress.getHostName(), inetSocketAddress.getPort(), stuId, xes_rfh);
                    groupGameTcp.setReceiveMegCallBack(receiveMegCallBack);
                    groupGameTcp.start();
                }
            }, 1000);
        }
    };
}
