package com.xueersi.parentsmeeting.modules.livevideo.videoaudiochat.business;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.ContextLiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.VideoChatLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.videoaudiochat.page.AgoraChatPager;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.VideoChatEvent;

import java.util.ArrayList;

public class ChatTipBll {
    protected Logger logger = LiveLoggerFactory.getLogger(getClass().getSimpleName());
    Activity activity;
    private RelativeLayout bottomContent;
    Handler handler = new Handler(Looper.getMainLooper());
    private LiveAndBackDebug liveAndBackDebug;
    private VideoAudioChatHttp videoChatHttp;
    private boolean raisehand = false;
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
    ViewGroup vgRaisehand;
    TextView tv_livevideo_chat_people;
    RelativeLayout rl_livevideo_content_left;
    LinearLayout ll_livevideo_chat_people;
    /**
     * 举手人数
     */
    private int raiseHandCount = 0;
    private String msgFrom;
    private String room;
    private AgoraVideoChatInter videoChatInter;
    private VideoChatEvent videoChatEvent;
    private LiveGetInfo getInfo;

    public ChatTipBll(Activity activity) {
        this.activity = activity;
        liveAndBackDebug = new ContextLiveAndBackDebug(activity);
    }

    public void setVideoChatHttp(VideoAudioChatHttp videoChatHttp) {
        this.videoChatHttp = videoChatHttp;
    }

    public void setVideoChatEvent(VideoChatEvent videoChatEvent) {
        this.videoChatEvent = videoChatEvent;
    }

    public void setGetInfo(LiveGetInfo getInfo) {
        this.getInfo = getInfo;
    }

    public void setRootView(RelativeLayout bottomContent) {
        this.bottomContent = bottomContent;
//        videoChatPager = new VideoChatPager(activity);
//        bottomContent.addView(videoChatPager.getRootView());
    }

    public void raisehand(String room, String from, String nonce) {
        this.msgFrom = from;
        this.room = room;
        logger.d("raisehand:from=" + from + ",nonce=" + nonce);
        XESToastUtils.showToast(activity, "老师开启了语音连麦，踊跃参与吧");
        handler.post(new Runnable() {
            @Override
            public void run() {
                initView();
            }
        });
    }

    public void onClassmateChange(final ArrayList<ClassmateEntity> classmateEntities) {
        this.classmateEntities = classmateEntities;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (videoChatInter != null) {
                    videoChatInter.updateUser(classmateChange, classmateEntities);
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

    private void initView() {
        if (vgRaisehand != null) {
            return;
        }
        vgRaisehand = (ViewGroup) LayoutInflater.from(activity).inflate(R.layout.layout_live_video_chat, bottomContent, false);
        rl_livevideo_content_left = vgRaisehand.findViewById(R.id.rl_livevideo_chat_content_left);
        ll_livevideo_chat_people = vgRaisehand.findViewById(R.id.ll_livevideo_chat_people);
        final RelativeLayout.LayoutParams lpRaisehand = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpRaisehand.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        final int bottom = LiveVideoPoint.getInstance().screenHeight - LiveVideoPoint.getInstance().y4 + 200;
        vgRaisehand.setPadding(vgRaisehand.getLeft(), bottom, vgRaisehand.getRight(), bottom);
        bottomContent.addView(vgRaisehand, lpRaisehand);
        Button bt_livevideo_chat_raisehand = vgRaisehand.findViewById(R.id.bt_livevideo_chat_raisehand);
        bt_livevideo_chat_raisehand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (raisehand) {
                    v.setBackgroundResource(R.drawable.live_task_jushou_icon_normal);
                    videoChatHttp.giveupMicro(msgFrom);
                } else {
                    v.setBackgroundResource(R.drawable.live_task_fangqi_icon_normal);
                    raisehand(msgFrom);
                    if (videoChatInter != null) {
                        stopRecord();
                    }
                }
                raisehand = !raisehand;
                logger.d("onClick:raisehand=" + raisehand);
            }
        });
        tv_livevideo_chat_people = vgRaisehand.findViewById(R.id.tv_livevideo_chat_people);
        tv_livevideo_chat_people.setText("当前直播人数" + raiseHandCount);
        final ImageView iv_livevideo_chat_small = vgRaisehand.findViewById(R.id.iv_livevideo_chat_small);
        iv_livevideo_chat_small.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rl_livevideo_content_left.getVisibility() == View.VISIBLE) {
                    iv_livevideo_chat_small.setImageResource(R.drawable.live_task_zuo_icon_normal);
                    rl_livevideo_content_left.setVisibility(View.GONE);
                    if (videoChatInter instanceof AgoraChatPager) {
                        AgoraChatPager agoraChatPager = (AgoraChatPager) videoChatInter;
                        agoraChatPager.hind();
                    }
                } else {
                    iv_livevideo_chat_small.setImageResource(R.drawable.live_task_you_icon_normal);
                    rl_livevideo_content_left.setVisibility(View.VISIBLE);
                    if (videoChatInter instanceof AgoraChatPager) {
                        AgoraChatPager agoraChatPager = (AgoraChatPager) videoChatInter;
                        agoraChatPager.show();
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
    }

    public void raiseHandCount(final int num) {
        raiseHandCount = num;
        handler.post(new Runnable() {
            @Override
            public void run() {
                initView();
                if (tv_livevideo_chat_people != null) {
                    tv_livevideo_chat_people.setText("当前直播人数" + num);
                }
            }
        });
    }

    private void raisehand(final String from) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String nonce = StableLogHashMap.creatNonce();
                VideoChatLog.sno4(liveAndBackDebug, nonce);
                videoChatHttp.requestMicro(nonce, room, from);
                videoChatHttp.chatHandAdd(new HttpCallBack(false) {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        logger.d("chatHandAdd:onPmSuccess:responseEntity=" + responseEntity.getJsonObject
                                ());
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                        logger.d("chatHandAdd:onPmError:responseEntity=" + responseEntity.getErrorMsg());
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        super.onPmFailure(error, msg);
                        logger.e("chatHandAdd:onPmFailure:responseEntity=" + msg);
                    }
                });
            }
        };
        runnable.run();
    }

    public void startMicro(final String nonce, final String room, String from, final boolean contain, final int micType) {
        logger.d("startMicro:nonce=" + nonce + ",from=" + from);
        if (contain) {
            raisehand = true;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                startRecord(room, nonce, contain, micType);
            }
        });
    }

    public void startRecord(final String room, final String nonce, boolean contain, int micType) {
        if (videoChatInter != null) {
            if (contain) {
                videoChatInter.startRecord("startRecord", room, nonce, micType == 1);
            } else {
                videoChatInter.removeMe();
            }
            videoChatInter.updateUser(classmateChange, classmateEntities);
            return;
        }
        initView();
        videoChatInter = new AgoraChatPager(activity, liveAndBackDebug, getInfo, videoChatEvent, videoChatHttp);
        if (contain) {
            videoChatInter.startRecord("startRecord", room, nonce, micType == 1);
        }
        videoChatInter.updateUser(classmateChange, classmateEntities);
        ll_livevideo_chat_people.addView(videoChatInter.getRootView(), RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    }

    public void stopRecord() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (vgRaisehand != null) {
                    bottomContent.removeView(vgRaisehand);
                    vgRaisehand = null;
                }
                if (videoChatInter != null) {
                    if (videoChatInter instanceof AgoraChatPager) {
                        AgoraChatPager agoraChatPager = (AgoraChatPager) videoChatInter;
                        agoraChatPager.onDestroy();
                        if (ll_livevideo_chat_people != null) {
                            ll_livevideo_chat_people.removeView(agoraChatPager.getRootView());
                        }
                    }
                    videoChatInter.stopRecord();
                    videoChatInter = null;
                }
            }
        });
    }
}
