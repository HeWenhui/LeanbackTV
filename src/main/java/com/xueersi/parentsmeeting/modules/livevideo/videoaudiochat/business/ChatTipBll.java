package com.xueersi.parentsmeeting.modules.livevideo.videoaudiochat.business;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.toast.XesToast;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.ContextLiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.VideoChatLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.business.VideoChatHttp;

import java.util.ArrayList;

public class ChatTipBll {
    protected Logger logger = LiveLoggerFactory.getLogger(getClass().getSimpleName());
    Activity activity;
    private RelativeLayout bottomContent;
    Handler handler = new Handler(Looper.getMainLooper());
    private LiveAndBackDebug liveAndBackDebug;
    private VideoChatHttp videoChatHttp;
    private boolean raisehand = false;
    ViewGroup vgRaisehand;
    TextView tv_livevideo_chat_people;
    /** 举手人数 */
    private int raiseHandCount = 0;
    private String msgFrom;

    public ChatTipBll(Activity activity) {
        this.activity = activity;
        liveAndBackDebug = new ContextLiveAndBackDebug(activity);
    }

    public void setVideoChatHttp(VideoChatHttp videoChatHttp) {
        this.videoChatHttp = videoChatHttp;
    }

    public void initView(RelativeLayout bottomContent) {
        this.bottomContent = bottomContent;
//        videoChatPager = new VideoChatPager(activity);
//        bottomContent.addView(videoChatPager.getRootView());
    }

    public void onJoin(final String onmic, final String openhands, final String room, final boolean classmateChange,
                       final
                       ArrayList<ClassmateEntity> classmateEntities, final String from) {
        raisehand(openhands, from, "");
    }

    public void raisehand(String status, String from, String nonce) {
        this.msgFrom = from;
        logger.d("raisehand:status=" + status + ",from=" + from + ",nonce=" + nonce);
        if ("on".equals(status)) {
            XESToastUtils.showToast(activity, "老师开启了语音连麦，踊跃参与吧");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    initView();
                }
            });
        }
    }

    public void raiseHandStatus(String status, final int num, String from) {
        if ("on".equals(status)) {
            raisehand = true;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    raiseHandCount(num);
                }
            });
        }
        logger.d("raiseHandStatus:status=" + status + ",num=" + num + ",from=" + from);
    }

    private void initView() {
        if (vgRaisehand != null) {
            return;
        }
        vgRaisehand = (ViewGroup) LayoutInflater.from(activity).inflate(R.layout.layout_live_video_chat, bottomContent, false);
        final RelativeLayout.LayoutParams lpRaisehand = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpRaisehand.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        final int bottom = LiveVideoPoint.getInstance().screenHeight - LiveVideoPoint.getInstance().y4;
        vgRaisehand.setPadding(vgRaisehand.getLeft(), vgRaisehand.getTop(), vgRaisehand.getRight(), bottom);
        bottomContent.addView(vgRaisehand, lpRaisehand);
        Button bt_livevideo_chat_raisehand = vgRaisehand.findViewById(R.id.bt_livevideo_chat_raisehand);
        bt_livevideo_chat_raisehand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (raisehand) {
                    XESToastUtils.showToast(activity, "已经举手");
                    return;
                }
                raisehand = true;
                raisehand(msgFrom);
            }
        });
        tv_livevideo_chat_people = vgRaisehand.findViewById(R.id.tv_livevideo_chat_people);
        tv_livevideo_chat_people.setText("当前直播人数" + raiseHandCount);
        Button bt_livevideo_chat_small = vgRaisehand.findViewById(R.id.bt_livevideo_chat_small);
        bt_livevideo_chat_small.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomContent.removeView(vgRaisehand);

                final ViewGroup vgChatSmall = (ViewGroup) LayoutInflater.from(activity).inflate(R.layout.layout_live_video_chat_small, bottomContent, false);
                RelativeLayout.LayoutParams lpChatSmall = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lpChatSmall.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                vgChatSmall.setPadding(vgChatSmall.getLeft(), vgChatSmall.getTop(), vgChatSmall.getRight(), bottom);
                bottomContent.addView(vgChatSmall, lpChatSmall);
                Button bt_livevideo_chat_big = vgChatSmall.findViewById(R.id.bt_livevideo_chat_big);
                bt_livevideo_chat_big.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomContent.removeView(vgChatSmall);
                        bottomContent.addView(vgRaisehand, lpRaisehand);
                    }
                });
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
                raisehand = true;
                videoChatHttp.requestMicro(nonce, from);
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
                BaseApplication baseApplication = (BaseApplication) BaseApplication.getContext();

            }
        };
        runnable.run();
    }
}
