package com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.lib.GroupGameTcp;
import com.xueersi.parentsmeeting.modules.livevideo.lib.ReceiveMegCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.lib.SendCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.lib.TcpConstants;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveThreadPoolExecutor;

import org.json.JSONObject;

import java.io.File;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** tcp调度 */
public class TcpDispatch {
    private Logger logger = LiveLoggerFactory.getLogger("TcpDispatch");
    private File logDir;
    private ArrayList<InetSocketAddress> addresses = new ArrayList<>();
    private GroupGameTcp groupGameTcp;
    private int addressIndex = 0;
    private Context context;
    private String stuId;
    private String xes_rfh;
    private String live_id;
    private String class_id;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isStop = false;
    private LiveThreadPoolExecutor liveThreadPoolExecutor = LiveThreadPoolExecutor.getInstance();
    private Map<Short, List<TcpMessageAction>> mMessageActionMap = new HashMap<>();
    private ArrayList<TcpMessageReg.OnTcpConnect> onTcpConnects = new ArrayList<>();
    /**
     * 当时进行的游戏类型，便于断线恢复
     * 1:语音炮弹
     * 2:cleaning up
     */
    @Deprecated
    private int gt = -1;
    /** 战队pk分组ID */
    @Deprecated
    private int pid = -1;
    /** 小组互动分组ID */
    @Deprecated
    private int iid = -1;
    /** 试题ID */
    @Deprecated
    private String test_id;

    public TcpDispatch(Context context, String stuId, String xes_rfh, String live_id, String class_id, int gt, int pid, int iid, String test_id) {
        this.context = context;
        this.stuId = stuId;
        this.xes_rfh = xes_rfh;
        this.live_id = live_id;
        this.class_id = class_id;
        this.gt = gt;
        this.pid = pid;
        this.iid = iid;
        this.test_id = test_id;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd,HH:mm:ss", Locale.getDefault());
        String s = dateFormat.format(new Date());
        String[] ss = s.split(",");
        logDir = LiveCacheFile.geCacheFile(BaseApplication.getContext(), "livelog/" + ss[0] + "/" + live_id + "-NL");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
    }

    public int getGt() {
        return gt;
    }

    public void setGt(int gt) {
        this.gt = gt;
    }

    public int getIid() {
        return iid;
    }

    public void setIid(int iid) {
        this.iid = iid;
    }

    public int getPid() {
        return pid;
    }

    @Deprecated
    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getTest_id() {
        return test_id;
    }

    public void setTest_id(String test_id) {
        this.test_id = test_id;
    }

    public void setOnTcpConnects(ArrayList<TcpMessageReg.OnTcpConnect> onTcpConnects) {
        this.onTcpConnects = onTcpConnects;
    }

    public void setAddresses(ArrayList<InetSocketAddress> addresses) {
        this.addresses = addresses;
        InetSocketAddress inetSocketAddress = addresses.get(addressIndex++);
        logger.d("setAddresses:inetSocketAddress=" + inetSocketAddress);

        groupGameTcp = new GroupGameTcp(inetSocketAddress, logDir);
        groupGameTcp.setReceiveMegCallBack(receiveMegCallBack);
        groupGameTcp.start();
    }

    public void stop() {
        isStop = true;
        if (groupGameTcp != null) {
            groupGameTcp.stop("stop");
        }
        mMessageActionMap.clear();
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
//                jsonObject.put("gt", gt);
//                jsonObject.put("pid", pid);
//                jsonObject.put("iid", iid);
//                jsonObject.put("test_id", test_id);
                String bodyStr = jsonObject.toString();
                short type = TcpConstants.LOGIN_TYPE;
                int operation = TcpConstants.LOGIN_OPERATION_SEND;
                groupGameTcp.send(type, operation, bodyStr);
            } catch (Exception e) {

            }
            for (int i = 0; i < onTcpConnects.size(); i++) {
                onTcpConnects.get(i).onTcpConnect();
            }
        }

        @Override
        public void onReceiveMeg(short type, int operation, String msg) {
            logger.d("onReceiveMeg:type=" + type + ",operation=" + operation + ",msg=" + msg);
            if (type == TcpConstants.LOGIN_TYPE) {
                if (operation == TcpConstants.LOGIN_OPERATION_REC) {

                }
            }
            List<TcpMessageAction> tcpMessageActions = mMessageActionMap.get((Short) type);
            if (tcpMessageActions != null) {
                logger.d("onReceiveMeg:type=" + type + ",tcpMessageActions=" + tcpMessageActions.size());
                for (int i = 0; i < tcpMessageActions.size(); i++) {
                    TcpMessageAction tcpMessageAction = tcpMessageActions.get(i);
                    tcpMessageAction.onMessage(type, operation, msg);
                }
            }
        }

        @Override
        public void onDisconnect(GroupGameTcp oldGroupGameTcp) {
            oldGroupGameTcp.stop("onDisconnect:isStop=" + isStop);
            final int seq = oldGroupGameTcp.getSeq();
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
                            groupGameTcp = new GroupGameTcp(inetSocketAddress, logDir);
                            groupGameTcp.setSeq(seq);
                            groupGameTcp.setReceiveMegCallBack(receiveMegCallBack);
                            groupGameTcp.start();
                        }
                    });
                }
            }, 1000);
        }
    };

    public void send(short type, int operation, String bodyStr) {
        if (groupGameTcp != null) {
            groupGameTcp.send(type, operation, bodyStr);
        }
    }

    public void send(short type, int operation, String bodyStr, SendCallBack sendCallBack) {
        if (groupGameTcp != null) {
            groupGameTcp.send(type, operation, bodyStr, sendCallBack);
        } else {
            if (sendCallBack != null) {
                sendCallBack.onNoOpen();
            }
        }
    }

    public void registTcpMessageAction(TcpMessageAction tcpMessageAction) {
        short[] messageFilter = tcpMessageAction.getMessageFilter();
        if (messageFilter != null && messageFilter.length > 0) {
            for (int i = 0; i < messageFilter.length; i++) {
                Short type = messageFilter[i];
                List<TcpMessageAction> tcpMessageActions = mMessageActionMap.get(type);
                if (tcpMessageActions == null) {
                    tcpMessageActions = new ArrayList<>();
                    mMessageActionMap.put(type, tcpMessageActions);
                }
                tcpMessageActions.add(tcpMessageAction);
            }
        }
    }

    public void unregistTcpMessageAction(TcpMessageAction tcpMessageAction) {
        short[] messageFilter = tcpMessageAction.getMessageFilter();
        if (messageFilter != null && messageFilter.length > 0) {
            for (int i = 0; i < messageFilter.length; i++) {
                Short type = messageFilter[i];
                List<TcpMessageAction> tcpMessageActions = mMessageActionMap.get(type);
                if (tcpMessageActions != null) {
                    tcpMessageActions.remove(tcpMessageAction);
                    if (tcpMessageActions.isEmpty()) {
                        mMessageActionMap.remove(type);
                    }
                }
            }
        }
    }
}
