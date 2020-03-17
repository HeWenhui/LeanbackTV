package com.xueersi.parentsmeeting.modules.livevideo.videoaudiochat.business;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.ContextLiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.MyEngineEventHandler;
import com.xueersi.parentsmeeting.modules.livevideo.business.agora.WorkerThread;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoLevel;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.VideoAudioChatLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
import com.xueersi.parentsmeeting.modules.livevideo.util.MidToast;
import com.xueersi.parentsmeeting.modules.livevideo.videoaudiochat.page.AgoraChatPager;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.VideoChatEvent;

import java.util.ArrayList;

public class ChatTipBll {
    String TAG = getClass().getSimpleName();
    protected Logger logger = LiveLoggerFactory.getLogger(TAG);
    Activity activity;
    private LiveViewAction liveViewAction;
    private Handler handler = LiveMainHandler.getMainHandler();
    private LiveAndBackDebug liveAndBackDebug;
    private String linkMicNonce = "";
    private VideoAudioChatHttp videoChatHttp;
    private int stuPutUpHandsNum = 0;
    private boolean raisehand = false;
    private boolean haveRaisehand = false;
    private boolean haveContainMe = false;
    /**
     * 连麦状态
     */
    private String onMic = "off";
    /**
     * 举麦包含我
     */
    private boolean containMe = false;
    /**
     * 连麦人数变化
     */
    private boolean classmateChange = true;
    /**
     * 连麦人数
     */
    private ArrayList<ClassmateEntity> classmateEntities = new ArrayList<>();
    private ViewGroup vgRaisehand;
    //添加声网
    private RelativeLayout rl_livevideo_agora_content;
    private boolean destory = false;
    private TextView tv_livevideo_chat_people;
    private TextView tv_livevideo_chat_people_hind;
    private TextView tv_livevideo_chat_people_grey;
    private TextView tv_livevideo_chat_people_grey_hind;
    private RelativeLayout rl_livevideo_content_left;
    private LinearLayout ll_livevideo_chat_people;
    private TextView tv_livevideo_chat_in_queue;
    private RelativeLayout rl_livevideo_chat_raisehand;
    private Button bt_livevideo_chat_raisehand;
    private TextView tv_livevideo_chat_raisehand;
    private RelativeLayout rl_livevideo_chat_raisehand_on;
    private RelativeLayout rl_livevideo_chat_raisehand_off;
    /**
     * 举手人数
     */
    private int raiseHandCount = 0;
    private String msgFrom;
    private int micType;
    private String linkmicid;
    private String room;
    private AgoraVideoChatInter videoChatInter;
    private VideoChatEvent videoChatEvent;
    private LiveGetInfo getInfo;
    private WorkerThread testWorkerThread;
    private boolean isConnect = true;
    private LogToFile logToFile;

    public ChatTipBll(Activity activity) {
        this.activity = activity;
        liveAndBackDebug = new ContextLiveAndBackDebug(activity);
        logToFile = new LogToFile(activity, TAG);
    }

    public void setMicType(int micType) {
        this.micType = micType;
    }

    public void setLinkmicid(String linkmicid) {
        this.linkmicid = linkmicid;
    }

    public void setVideoChatHttp(VideoAudioChatHttp videoChatHttp) {
        this.videoChatHttp = videoChatHttp;
    }

    public void setVideoChatEvent(VideoChatEvent videoChatEvent) {
        this.videoChatEvent = videoChatEvent;
    }

    public void setGetInfo(LiveGetInfo getInfo) {
        this.getInfo = getInfo;
        stuPutUpHandsNum = getInfo.getStuPutUpHandsNum();
    }

    public void setRootView(LiveViewAction liveViewAction) {
        this.liveViewAction = liveViewAction;
//        videoChatPager = new VideoChatPager(activity);
//        bottomContent.addView(videoChatPager.getRootView());
    }

    public void raisehand(String room, String from, final String nonce, final int micType) {
        this.msgFrom = from;
        this.room = room;
        logger.d("raisehand:from=" + from + ",nonce=" + nonce);
        if (0 == micType) {
            MidToast.showToast(activity, "老师开启了语音连麦，踊跃参与吧");
        } else {
            MidToast.showToast(activity, "老师开启了视频连麦，踊跃参与吧");
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                initView("raisehand");
                linkMicNonce = nonce;
                VideoAudioChatLog.showLinkMicPanelSno3(liveAndBackDebug, linkmicid, micType == 0 ? "audio" : "video", nonce);
            }
        });
    }

    public void onClassmateChange(final ArrayList<ClassmateEntity> classmateEntities, boolean contain) {
        logger.d("onClassmateChange:size=" + classmateEntities.size() + "，contain=" + contain);
        this.classmateEntities.clear();
        this.classmateEntities.addAll(classmateEntities);
        String oldMic = onMic;
        if (classmateEntities.isEmpty()) {
            onMic = "off";
        }
        final boolean modeChange = onMic.equals(oldMic);
        handler.post(new Runnable() {
            @Override
            public void run() {
                raiseHandCount(raiseHandCount);
                if (videoChatInter != null) {
                    videoChatInter.updateUser(classmateChange, classmateEntities);
                }
                if (modeChange && "off".equals(onMic)) {
                    handler.removeCallbacks(waitRun);
                    handler.postDelayed(waitRun, 1000);
//                    initView("onClassmateChange");
//                    rl_livevideo_chat_raisehand_on.setVisibility(View.GONE);
//                    rl_livevideo_chat_raisehand_off.setVisibility(View.VISIBLE);
                }
            }
        });
    }

//    public void raiseHandStatus(String status, final int num, String from) {
//        if ("on".equals(status)) {
//            raisehand = true;
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    raiseHandCount(num);
//                }
//            });
//        }
//        logger.d("raiseHandStatus:status=" + status + ",num=" + num + ",from=" + from);
//    }

    private void initView(String method) {
        logger.d("initView:vgRaisehand=null?" + (vgRaisehand == null) + ",method=" + method);
        if (vgRaisehand != null) {
            return;
        }
        handler.postDelayed(waitRun, 1000);
        vgRaisehand = (ViewGroup) liveViewAction.inflateView(R.layout.layout_live_video_chat);
        rl_livevideo_agora_content = (RelativeLayout) liveViewAction.inflateView(R.layout.layout_livevideo_video_chat);
        rl_livevideo_content_left = vgRaisehand.findViewById(R.id.rl_livevideo_chat_content_left);
        ll_livevideo_chat_people = vgRaisehand.findViewById(R.id.ll_livevideo_chat_people);
        rl_livevideo_chat_raisehand_on = vgRaisehand.findViewById(R.id.rl_livevideo_chat_raisehand_on);
        rl_livevideo_chat_raisehand_off = vgRaisehand.findViewById(R.id.rl_livevideo_chat_raisehand_off);
        final RelativeLayout.LayoutParams lpRaisehand = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpRaisehand.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lpRaisehand.leftMargin = LiveVideoPoint.getInstance().x2;
        logToFile.d("initView:x2=" + LiveVideoPoint.getInstance().x2 + ",method=" + method + ",destory=" + destory);
        final int bottom = LiveVideoPoint.getInstance().screenHeight - LiveVideoPoint.getInstance().y4 + 200;
        vgRaisehand.setPadding(vgRaisehand.getLeft(), bottom, vgRaisehand.getRight(), bottom);
        liveViewAction.addView(new LiveVideoLevel(-4), rl_livevideo_agora_content);
        liveViewAction.addView(new LiveVideoLevel(-3), vgRaisehand, lpRaisehand);
        rl_livevideo_chat_raisehand = vgRaisehand.findViewById(R.id.rl_livevideo_chat_raisehand);
        bt_livevideo_chat_raisehand = vgRaisehand.findViewById(R.id.bt_livevideo_chat_raisehand);
        tv_livevideo_chat_raisehand = vgRaisehand.findViewById(R.id.tv_livevideo_chat_raisehand);
        bt_livevideo_chat_raisehand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!raisehand) {
                    boolean have = XesPermission.checkPermissionNoAlert(activity, PermissionConfig.PERMISSION_CODE_AUDIO, PermissionConfig.PERMISSION_CODE_CAMERA);
                    if (!have) {
                        return;
                    }
                    VideoAudioChatLog.clickedRaiseHandSno4(liveAndBackDebug, "on", linkmicid, micType == 0 ? "audio" : "video", linkMicNonce);
                    if (testWorkerThread == null) {
                        enableLastmileTest();
                    }
                } else {
                    VideoAudioChatLog.cancelRaiseHandSno6(liveAndBackDebug, linkmicid, micType == 0 ? "audio" : "video", linkMicNonce);
                }
                isConnect = true;
                raisehandClick(true);
            }
        });
        tv_livevideo_chat_people = vgRaisehand.findViewById(R.id.tv_livevideo_chat_people);
        tv_livevideo_chat_people_hind = vgRaisehand.findViewById(R.id.tv_livevideo_chat_people_hind);
        tv_livevideo_chat_people.setText("当前举手" + raiseHandCount + "人，等待连麦中...");
        tv_livevideo_chat_people_grey = vgRaisehand.findViewById(R.id.tv_livevideo_chat_people_grey);
        tv_livevideo_chat_people_grey_hind = vgRaisehand.findViewById(R.id.tv_livevideo_chat_people_grey_hind);
        tv_livevideo_chat_people_grey.setText("当前举手" + raiseHandCount + "人，等待连麦中...");
        tv_livevideo_chat_in_queue = vgRaisehand.findViewById(R.id.tv_livevideo_chat_in_queue);
        final ImageView iv_livevideo_chat_small = vgRaisehand.findViewById(R.id.iv_livevideo_chat_small);
        iv_livevideo_chat_small.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rl_livevideo_content_left.getVisibility() == View.VISIBLE) {
                    iv_livevideo_chat_small.setImageResource(R.drawable.live_task_you_icon_normal);
                    rl_livevideo_content_left.setVisibility(View.GONE);
                    if (videoChatInter instanceof AgoraChatPager) {
                        AgoraChatPager agoraChatPager = (AgoraChatPager) videoChatInter;
                        agoraChatPager.hind("onClick");
                    }
                } else {
                    iv_livevideo_chat_small.setImageResource(R.drawable.live_task_zuo_icon_normal);
                    rl_livevideo_content_left.setVisibility(View.VISIBLE);
                    if (videoChatInter instanceof AgoraChatPager) {
                        AgoraChatPager agoraChatPager = (AgoraChatPager) videoChatInter;
                        agoraChatPager.show("onClick");
                    }
                }
//                bottomContent.removeView(vgRaisehand);
//
//                final ViewGroup vgChatSmall = (ViewGroup) LayoutInflater.from(activity).inflate(R.layout.layout_live_video_chat_small, bottomContent, false);
//                RelativeLayout.LayoutParams lpChatSmall = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                lpChatSmall.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                vgChatSmall.setPadding(vgChatSmall.getLeft(), vgChatSmall.getTop(), vgChatSmall.getRight(), bottom);
//                bottomContent.addView(vgChatSmall, lpChatSmall);
//                Button bt_livevideo_chat_big = vgChatSmall.findViewById(R.id.bt_livevideo_chat_big);
//                bt_livevideo_chat_big.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        bottomContent.removeView(vgChatSmall);
//                        bottomContent.addView(vgRaisehand, lpRaisehand);
//                    }
//                });
            }
        });
        if (micType == 0) {
            View view = vgRaisehand.findViewById(R.id.v_livevideo_chat_myline);
            view.setVisibility(View.GONE);
            RelativeLayout relativeLayout = vgRaisehand.findViewById(R.id.rl_livevideo_chat_raisehand2);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
            lp.leftMargin = (int) (15 * ScreenUtils.getScreenDensity());
            LayoutParamsUtil.setViewLayoutParams(relativeLayout, lp);
        }
    }

    /**
     * 上次网络质量
     */
    private int lastquality = -1;
    MyEngineEventHandler.OnLastmileQuality onLastmileQuality = new MyEngineEventHandler.OnLastmileQuality() {

        @Override
        public void onLastmileQuality(int quality) {
            if (lastquality != quality) {
//                logger.d("onLastmileQuality:quality=" + quality);
                videoChatHttp.sendNetWorkQuality(quality);
            }
            lastquality = quality;
        }

        @Override
        public void onQuit() {
            logger.d("onQuit");
            lastquality = -1;
            testWorkerThread = null;
        }
    };

    private void enableLastmileTest() {
        AgoraChatPager agoraChatPager = null;
        if (videoChatInter instanceof AgoraChatPager) {
            agoraChatPager = (AgoraChatPager) videoChatInter;
            logger.d("enableLastmileTest:WorkerThread=null?" + (agoraChatPager.getWorkerThread() == null));
            if (agoraChatPager.getWorkerThread() != null) {
                return;
            }
        }
        if (testWorkerThread == null) {
            testWorkerThread = new WorkerThread(activity.getApplicationContext(), 0, false);
            if (agoraChatPager != null) {
                agoraChatPager.setTestWorkerThread(testWorkerThread);
            }
        }
        testWorkerThread.enableLastmileTest(onLastmileQuality);
    }

    private void changeRaisehand(boolean raisehand) {
        if (raisehand) {
            bt_livevideo_chat_raisehand.setBackgroundResource(R.drawable.live_task_fangqi_icon_normal);
            tv_livevideo_chat_raisehand.setText("放弃");
            tv_livevideo_chat_in_queue.setText("举手成功，已进入队列");
        } else {
            bt_livevideo_chat_raisehand.setBackgroundResource(R.drawable.live_task_jushou_icon_normal);
            tv_livevideo_chat_raisehand.setText("举手");
            if (isConnect) {
                tv_livevideo_chat_in_queue.setText("你已下麦，可以再次举手");
            } else {
                tv_livevideo_chat_in_queue.setText("你已掉线，可以再次举手");
            }
        }
    }

    private String pointstr = ".  ";
    private int index = 0;

    private Runnable waitRun = new Runnable() {
        @Override
        public void run() {
            if ("off".equals(onMic)) {
                if (tv_livevideo_chat_people != null) {
                    tv_livevideo_chat_people.setText("当前举手" + raiseHandCount + "人，等待连麦中" + pointstr);
                }
                if (tv_livevideo_chat_people_grey != null) {
                    tv_livevideo_chat_people_grey.setText("当前举手" + raiseHandCount + "人，等待连麦中" + pointstr);
                }
                index++;
                if (index % 3 == 1) {
                    pointstr = ".. ";
                } else if (index % 3 == 2) {
                    pointstr = "...";
                } else {
                    pointstr = ".  ";
                }
//                logger.d("waitRun:index=" + index + ",pointstr=" + pointstr);
                handler.postDelayed(this, 1000);
            } else {
                logger.d("waitRun:onMic=off");
            }
        }
    };

    public void raiseHandCount(final int num) {
        raiseHandCount = num;
        handler.post(new Runnable() {
            @Override
            public void run() {
                initView("raiseHandCount:onMic=" + onMic + ",num=" + num);
                if ("on".equals(onMic)) {
                    if (tv_livevideo_chat_people != null) {
                        String string = "当前举手" + raiseHandCount + "人，已连麦" + classmateEntities.size() + "人";
                        tv_livevideo_chat_people.setText(string);
                        tv_livevideo_chat_people_hind.setText(string);
                    }
                    if (tv_livevideo_chat_people_grey != null) {
                        String string = "当前举手" + raiseHandCount + "人，已连麦" + classmateEntities.size() + "人";
                        tv_livevideo_chat_people_grey.setText(string);
                        tv_livevideo_chat_people_grey_hind.setText(string);
                    }
                } else {
                    if (tv_livevideo_chat_people != null) {
                        String string = "当前举手" + raiseHandCount + "人，等待连麦中...";
                        tv_livevideo_chat_people.setText(string);
                        tv_livevideo_chat_people_hind.setText(string);
                    }
                    if (tv_livevideo_chat_people_grey != null) {
                        String string = "当前举手" + raiseHandCount + "人，等待连麦中...";
                        tv_livevideo_chat_people_grey.setText(string);
                        tv_livevideo_chat_people_grey_hind.setText(string);
                    }
                }
            }
        });
    }

    private void raisehandClick(boolean addRaise) {
        haveRaisehand = true;
        boolean oldRaisehand = raisehand;
        if (!raisehand) {
            raisehand(addRaise, msgFrom);
            if ("off".equals(onMic)) {
                rl_livevideo_chat_raisehand_on.setVisibility(View.VISIBLE);
                rl_livevideo_chat_raisehand_off.setVisibility(View.GONE);
            }
        } else {
            videoChatHttp.giveupMicro(msgFrom);
//            if ("off".equals(onMic)) {
//                rl_livevideo_chat_raisehand_on.setVisibility(View.GONE);
//                rl_livevideo_chat_raisehand_off.setVisibility(View.VISIBLE);
//            }
        }
        changeRaisehand(!raisehand);
        raisehand = !oldRaisehand;
        logToFile.d("raisehandClick:raisehand=" + raisehand + ",addRaise=" + addRaise);
    }

    private void raisehand(final boolean addRaise, final String from) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String nonce = StableLogHashMap.creatNonce();
                if (addRaise) {
                    stuPutUpHandsNum++;
                }
                getInfo.setStuPutUpHandsNum(stuPutUpHandsNum);
                videoChatHttp.requestMicro(nonce, room, from);
                if (!addRaise) {
                    return;
                }
                final long before = SystemClock.elapsedRealtime();
                videoChatHttp.chatHandAdd(new HttpCallBack(false) {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) {
                        Object jsonObject = responseEntity.getJsonObject();
                        logger.d("chatHandAdd:onPmSuccess:responseEntity=" + jsonObject);
                        try {
                            stuPutUpHandsNum = Integer.parseInt(jsonObject + "");
                            getInfo.setStuPutUpHandsNum(stuPutUpHandsNum);
                        } catch (Exception e) {
                            LiveCrashReport.postCatchedException(new Exception("" + jsonObject, e));
                        }
                        VideoAudioChatLog.raiseHandToPhpSno5(liveAndBackDebug, linkmicid, micType == 0 ? "audio" : "video", linkMicNonce, true, "0", SystemClock.elapsedRealtime() - before);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                        logger.d("chatHandAdd:onPmError:responseEntity=" + responseEntity.getErrorMsg());
                        VideoAudioChatLog.raiseHandToPhpSno5(liveAndBackDebug, linkmicid, micType == 0 ? "audio" : "video", linkMicNonce, false, "1", SystemClock.elapsedRealtime() - before);
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        super.onPmFailure(error, msg);
                        logger.e("chatHandAdd:onPmFailure:responseEntity=" + msg);
                        VideoAudioChatLog.raiseHandToPhpSno5(liveAndBackDebug, linkmicid, micType == 0 ? "audio" : "video", linkMicNonce, false, "2", SystemClock.elapsedRealtime() - before);
                    }
                });
            }
        };
        runnable.run();
    }

    public void startMicro(final String onMic, final String room, String from, final boolean contain, final int micType, final String nonce) {
        logger.d("startMicro:onMic=" + onMic + ",contain=" + contain + ",from=" + from);
        this.onMic = onMic;
        this.msgFrom = from;
        this.micType = micType;
        if (contain) {
            raisehand = true;
            if (!containMe) {
                VideoAudioChatLog.getSelectedMsgSno9(liveAndBackDebug, linkmicid, micType == 0 ? "audio" : "video", nonce);
            }
        } else {
            if (containMe) {
                VideoAudioChatLog.getLeaveMsgSno9(liveAndBackDebug, linkmicid, micType == 0 ? "audio" : "video", nonce);
            }
        }
        final boolean oldcontainMe = containMe;
        containMe = contain;
        handler.post(new Runnable() {
            @Override
            public void run() {
                initView("startMicro");
                handler.removeCallbacks(waitRun);
                if ("on".equals(onMic)) {
                    rl_livevideo_chat_raisehand_on.setVisibility(View.VISIBLE);
                    rl_livevideo_chat_raisehand_off.setVisibility(View.GONE);
                } else {
                    handler.postDelayed(waitRun, 1000);
//                    rl_livevideo_chat_raisehand_on.setVisibility(View.GONE);
//                    rl_livevideo_chat_raisehand_off.setVisibility(View.VISIBLE);
                }
                if (oldcontainMe != containMe) {
                    changeRaisehand(contain);
                }
                raisehand = contain;
                if (contain) {
                    haveRaisehand = true;
                    haveContainMe = true;
                    rl_livevideo_chat_raisehand.setVisibility(View.GONE);
                    tv_livevideo_chat_in_queue.setText("恭喜你已连麦");
                } else {
                    rl_livevideo_chat_raisehand.setVisibility(View.VISIBLE);
                    if (oldcontainMe != containMe) {
                        tv_livevideo_chat_in_queue.setText("你已下麦，可以再次举手");
                    }
                }
                startRecord(room, contain, micType, nonce);
            }
        });
    }

    public void startRecord(final String room, boolean contain, int micType, String nonce) {
        logToFile.d("startRecord:room=" + room + ",contain=" + contain + ",micType=" + micType);
        if (videoChatInter != null) {
            if (contain) {
                videoChatInter.startRecord("startRecord", room, nonce, micType == 1);
            } else {
                videoChatInter.removeMe(nonce);
            }
            videoChatInter.updateUser(classmateChange, classmateEntities);
            return;
        }
        initView("startRecord");
        AgoraChatPager agoraChatPager = new AgoraChatPager(activity, liveAndBackDebug, getInfo, videoChatEvent, videoChatHttp, msgFrom, micType, linkmicid, ll_livevideo_chat_people, liveViewAction);
        agoraChatPager.setTestWorkerThread(testWorkerThread);
        videoChatInter = agoraChatPager;
        if (contain) {
            videoChatInter.startRecord("startRecord", room, nonce, micType == 1);
        }
        videoChatInter.updateUser(classmateChange, classmateEntities);
        ll_livevideo_chat_people.addView(videoChatInter.getRootView(), RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (micType == 1) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ll_livevideo_chat_people.getLayoutParams();
            lp.leftMargin = SizeUtils.Dp2Px(activity, 15);
            LayoutParamsUtil.setViewLayoutParams(ll_livevideo_chat_people, lp);
        }
    }

    public void onConnect() {
        logToFile.d("onConnect");
        isConnect = true;
    }

    public void onDisconnect() {
        logToFile.d("onDisconnect:visibility=" + rl_livevideo_chat_raisehand.getVisibility());
        isConnect = false;
        if (raisehand && rl_livevideo_chat_raisehand.getVisibility() == View.VISIBLE) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    raisehandClick(false);
                }
            });
        }
    }

    public void onNetWorkChange(int netWorkType) {
        if (videoChatInter != null) {
            videoChatInter.onNetWorkChange(netWorkType);
        }
    }

    public void stopRecord(String method, boolean isDestory, final String nonce) {
        raiseHandCount = 0;
        if (!isDestory) {
            if (haveRaisehand) {
                if (haveContainMe) {
                    MidToast.showToast(activity, "老师已结束本次举麦");
                } else {
                    MidToast.showToast(activity, "很遗憾本次没有轮到你，下次再见哦");
                }
                haveRaisehand = false;
            } else {
                MidToast.showToast(activity, "老师已结束本次举麦");
            }
        }
        raisehand = false;
        onMic = "off";
        logToFile.d("stopRecord:method=" + method);
        handler.removeCallbacks(waitRun);
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (vgRaisehand != null) {
                    liveViewAction.removeView(vgRaisehand);
                    vgRaisehand = null;
                    destory = true;
                }
                if (rl_livevideo_agora_content != null) {
                    liveViewAction.removeView(rl_livevideo_agora_content);
                    rl_livevideo_agora_content = null;
                }
                if (videoChatInter != null) {
                    if (videoChatInter instanceof AgoraChatPager) {
                        AgoraChatPager agoraChatPager = (AgoraChatPager) videoChatInter;
                        agoraChatPager.onDestroy();
                        if (ll_livevideo_chat_people != null) {
                            ll_livevideo_chat_people.removeView(agoraChatPager.getRootView());
                        }
                    }
                    videoChatInter.stopRecord(nonce);
                    videoChatInter = null;
                }
            }
        });
    }

    public void destory() {
        handler.removeCallbacks(waitRun);
        if (testWorkerThread != null) {
            testWorkerThread.disableLastmileTest();
        }
    }
}
