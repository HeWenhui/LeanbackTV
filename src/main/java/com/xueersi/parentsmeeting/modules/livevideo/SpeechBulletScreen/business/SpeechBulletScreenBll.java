package com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.business;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.page.SpeechBulletScreenPager;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.page.SpeechBulletScreenPlayBackPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.dialog.ShortToastDialog;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;

/**
 * Created by Zhang Yuansun on 2018/7/12.
 * 语音弹幕业务类
 */

public class  SpeechBulletScreenBll implements SpeechBulletScreenAction {
    public static final String TAG = "SpeechBulletScreenBll";
    private LiveHttpResponseParser mLiveHttpResponseParser = null;
    private Activity activity;
    private LiveBll2 mLiveBll;
    /** 语音弹幕的布局 */
    private RelativeLayout rlSpeechBulContent;
    /** 语音弹幕的界面 */
    private SpeechBulletScreenPager mSpeechBulPager;
    /** 回放弹幕的界面 */
    private SpeechBulletScreenPlayBackPager mSpeechBulPlaybackPager;

    private SpeechBulletScreenHttp speechBulletScreenHttp;
    public void setSpeechBulletScreenHttp(SpeechBulletScreenHttp speechBulletScreenHttp) {
        this.speechBulletScreenHttp = speechBulletScreenHttp;
        if (mSpeechBulPager != null)
            mSpeechBulPager.setSpeechBulletScreenHttp(speechBulletScreenHttp);
    }

    private WeakHandler mWeakHandler = new WeakHandler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            return false;
        }
    });

    public SpeechBulletScreenBll(Activity activity) {
        this.activity = activity;
    }

    public void setLiveBll(LiveBll2 mLiveBll) {
        this.mLiveBll = mLiveBll;
    }

    public void initView(final RelativeLayout bottomContent) {
        mWeakHandler.post(new Runnable() {
            @Override
            public void run() {
                if (rlSpeechBulContent != null) {
                    bottomContent.addView(rlSpeechBulContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                } else {
                    rlSpeechBulContent = new RelativeLayout(activity);
                    rlSpeechBulContent.setId(R.id.rl_livevideo_content_speechbul);
                    bottomContent.addView(rlSpeechBulContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
            }
        });

    }

    public void onStartSpeechBulletScreenPlayBack() {
        mWeakHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSpeechBulPlaybackPager = new SpeechBulletScreenPlayBackPager(activity,SpeechBulletScreenBll.this);
                rlSpeechBulContent.removeAllViews();
                rlSpeechBulContent.addView(mSpeechBulPlaybackPager.getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                rlSpeechBulContent.setVisibility(View.VISIBLE);
            }
        },0);
    }

    @Override
    public void onStartSpeechBulletScreen() {
        Log.i(TAG,"onStartSpeechBulletScreen()");
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Log.i(TAG,"start setSoftInputMode:SOFT_INPUT_ADJUST_NOTHING");
//                WindowManager.LayoutParams attributes = activity.getWindow().getAttributes();
//                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING|WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//                attributes = activity.getWindow().getAttributes();
//                Log.i(TAG,"end setSoftInputMode");
//            }
//        });
        mWeakHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showShortToast("老师开启了语音弹幕");
            }
        },0);

        mWeakHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSpeechBulPager = new SpeechBulletScreenPager(activity,speechBulletScreenHttp);
                rlSpeechBulContent.removeAllViews();
                rlSpeechBulContent.addView(mSpeechBulPager.getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                rlSpeechBulContent.setVisibility(View.VISIBLE);
            }
        },2000);
    }

    public void showShortToast(final String tips) {
        ShortToastDialog shortToastDialog= new ShortToastDialog(activity);
        shortToastDialog.setMsg(tips);
        shortToastDialog.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fangzhengcuyuan.ttf"));
        shortToastDialog.showDialog();
    }

    @Override
    public void onCloseSpeechBulletScreen(final boolean hasTips) {
        mWeakHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mSpeechBulPager != null) {
                    mSpeechBulPager.CloseSpeechBulletScreen(hasTips);
                }
            }
        });
    }

    public void addPlayBackDanmaku(final String name, final String msg, final String headImgUrl , final boolean isGuest) {
        if (mSpeechBulPlaybackPager!=null) {
            mSpeechBulPlaybackPager.addDanmaKuFlowers(name, msg, headImgUrl, isGuest);
        }
    }

    public void pauseDanmaku() {
        if (mSpeechBulPlaybackPager!=null) {
            mSpeechBulPlaybackPager.pauseDanmaku();
        }
    }

    public void resumeDanmaku() {
        if (mSpeechBulPlaybackPager!=null) {
            mSpeechBulPlaybackPager.resumeDanmaku();
        }
    }

    public void setDanmakuSpeed(float speed) {
        if (mSpeechBulPlaybackPager!=null) {
            mSpeechBulPlaybackPager.setDanmakuSpeed(speed);
        }
    }

    @Override
    public void onStartConnect() {

    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onRegister() {

    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onUserList(String channel, User[] users) {

    }

    @Override
    public void onMessage(final String target, final String sender, final String login, final String hostname, final String text, final String headurl) {
        mWeakHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mSpeechBulPager != null) {
                    mSpeechBulPager.onMessage(target, sender, login, hostname, text, headurl);
                }
            }
        });
    }

    @Override
    public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String message) {
        if (mSpeechBulPager != null) {
            mSpeechBulPager.onPrivateMessage(isSelf, sender, login, hostname, target, message);
        }
    }

    @Override
    public void onJoin(String target, String sender, String login, String hostname) {

    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {

    }

    @Override
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {

    }

    @Override
    public void onDisable(boolean disable, boolean fromNotice) {

    }

    @Override
    public void onOtherDisable(String id, String name, boolean disable) {

    }

    @Override
    public void onopenchat(boolean openchat, String mode, boolean fromNotice) {

    }

    @Override
    public void onOpenbarrage(boolean openbarrage, boolean fromNotice) {

    }

    @Override
    public void videoStatus(String status) {

    }
    public void onDestory() {
        if (mSpeechBulPager != null) {
            mSpeechBulPager.onDestroy();
        }
    }
}
