package com.xueersi.parentsmeeting.modules.livevideo.english;

import android.app.Activity;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.AppBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveEventBus;
import com.xueersi.parentsmeeting.modules.livevideo.english.http.EnglishHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.business.ClassEndReg;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.business.EnTeamPkIRCBll;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.http.EnTeamPkHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp.TcpDispatch;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp.TcpMessageAction;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp.TcpMessageReg;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.tcp.TcpRunnable;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.lib.SendCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class EnglishCommonBll extends LiveBaseBll {
    private TcpDispatch tcpDispatch;
    private EnglishHttpManager englishHttpManager;
    private boolean destory = false;
    private TcpMessageReg tcpMessageReg;
    private ArrayList<TcpMessageReg.OnTcpConnect> onTcpConnects = new ArrayList<>();
    private ArrayList<TcpRunnable> tcpRun = new ArrayList<>();
    private int classInt = 0;

    public EnglishCommonBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    public EnglishHttpManager getEnglishHttpManager() {
        if (englishHttpManager == null) {
            englishHttpManager = new EnglishHttpManager(getHttpManager());
        }
        return englishHttpManager;
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        if (getInfo.getStudentLiveInfo() != null) {
            String classId = getInfo.getStudentLiveInfo().getClassId();
            try {
                mLogtf.d("onLiveInited:classInt=" + classId);
                classInt = Integer.parseInt(classId);
            } catch (Exception e) {
                CrashReport.postCatchedException(e);
            }
        }
        if (tcpMessageReg == null) {
            tcpMessageReg = new TcpMessageReg() {

                @Override
                public void onConnect(OnTcpConnect onTcpConnect) {
                    if (tcpDispatch != null) {
                        onTcpConnect.onTcpConnect();
                    }
                    onTcpConnects.add(onTcpConnect);
                }

                @Override
                public void send(final short type, final int operation, final String bodyStr) {
                    if (tcpDispatch != null) {
                        tcpDispatch.send(type, operation, bodyStr);
                    } else {
                        tcpRun.add(new TcpRunnable("send1") {
                            @Override
                            public void run() {
                                if (tcpDispatch != null) {
                                    tcpDispatch.send(type, operation, bodyStr);
                                }
                            }
                        });
                    }
                }

                @Override
                public void send(final short type, final int operation, final String bodyStr, final SendCallBack sendCallBack) {
                    if (tcpDispatch != null) {
                        tcpDispatch.send(type, operation, bodyStr, sendCallBack);
                    } else {
                        tcpRun.add(new TcpRunnable("send2") {
                            @Override
                            public void run() {
                                if (tcpDispatch != null) {
                                    tcpDispatch.send(type, operation, bodyStr, sendCallBack);
                                }
                            }
                        });
                    }
                }

                @Override
                public void registTcpMessageAction(final TcpMessageAction tcpMessageAction) {
                    if (tcpDispatch != null) {
                        tcpDispatch.registTcpMessageAction(tcpMessageAction);
                    } else {
                        tcpRun.add(new TcpRunnable("registTcpMessageAction") {
                            @Override
                            public void run() {
                                if (tcpDispatch != null) {
                                    tcpDispatch.registTcpMessageAction(tcpMessageAction);
                                }
                            }
                        });
                    }
                }

                @Override
                public void unregistTcpMessageAction(TcpMessageAction tcpMessageAction) {
                    if (tcpDispatch != null) {
                        tcpDispatch.unregistTcpMessageAction(tcpMessageAction);
                    }
                }

            };
            ProxUtil.getProxUtil().put(mContext, TcpMessageReg.class, tcpMessageReg);
        }
        getEnglishHttpManager().dispatch(mGetInfo.getStuId(), new AbstractBusinessDataCallBack() {
            AbstractBusinessDataCallBack callBack;
            int time = 1;

            @Override
            public void onDataSucess(Object... objData) {
                ArrayList<InetSocketAddress> addresses = (ArrayList<InetSocketAddress>) objData[0];
                mLogtf.d("dispatch:size=" + addresses.size());
                if (addresses.size() > 0) {
                    connect("onArtsExtLiveInited", addresses);
                }
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                super.onDataFail(errStatus, failMsg);
                mLogtf.d("dispatch:time=" + time + ",failMsg=" + failMsg);
                callBack = this;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!destory) {
                            getEnglishHttpManager().dispatch(mGetInfo.getStuId(), callBack);
                        }
                    }
                }, ++time * 1000);
            }
        });
    }

    private synchronized void connect(String method, ArrayList<InetSocketAddress> addresses) {
        if (tcpDispatch == null) {
            if (destory) {
                mLogtf.d("connect:destory:method=" + method);
                return;
            }
            int pid = -1;
            int iid = -1;
            tcpDispatch = new TcpDispatch(mContext, mGetInfo.getStuId(), AppBll.getInstance().getUserRfh(), mGetInfo.getId(), classInt + "", -1, pid, iid, "");
            tcpDispatch.setOnTcpConnects(onTcpConnects);
            tcpDispatch.setAddresses(addresses);
            while (!tcpRun.isEmpty()) {
                TcpRunnable runnable = tcpRun.remove(0);
                mLogtf.d("connect:run=" + runnable.getName());
                runnable.run();
            }
        } else {
            mLogtf.d("connect:method=" + method);
        }
    }

    @Override
    public void onDestory() {
        super.onDestory();
        destory = true;
        onTcpConnects.clear();
        if (tcpDispatch != null) {
            tcpDispatch.stop();
            tcpDispatch = null;
        }
    }
}
