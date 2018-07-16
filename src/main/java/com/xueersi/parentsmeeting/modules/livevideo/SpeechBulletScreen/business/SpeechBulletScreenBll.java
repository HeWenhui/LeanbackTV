package com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.business;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.page.SpeechBulletScreenPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;

/**
 * Created by Zhang Yuansun on 2018/7/12.
 * 语音弹幕业务类
 */

public class SpeechBulletScreenBll implements SpeechBulletScreenAction, Handler.Callback {
    public static final String TAG = "SpeechBulletScreenBll";
    private WeakHandler mWeakHandler = new WeakHandler(this);
    private LiveHttpResponseParser mLiveHttpResponseParser = null;
    private Activity activity;
    private LiveBll2 mLiveBll;
    /** 语音弹幕的布局 */
    private RelativeLayout rlSpeechBulContent;
    /** 语音弹幕的界面 */
    private SpeechBulletScreenPager mSpeechBulPager;

    @Override
    public boolean handleMessage(Message message) {
        return false;
    }

    public SpeechBulletScreenBll(Activity activity) {
        this.activity = activity;
    }

    public void setLiveBll(LiveBll2 mLiveBll) {
        this.mLiveBll = mLiveBll;
    }

    public void initView(RelativeLayout bottomContent) {
        if (rlSpeechBulContent != null) {
            bottomContent.addView(rlSpeechBulContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            rlSpeechBulContent = new RelativeLayout(activity);
            rlSpeechBulContent.setId(R.id.rl_livevideo_content_speechbul);
            bottomContent.addView(rlSpeechBulContent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    @Override
    public void onStartSpeechBulletScreen() {
        mWeakHandler.post(new Runnable() {
            @Override
            public void run() {
                mSpeechBulPager = new SpeechBulletScreenPager(activity,SpeechBulletScreenBll.this);
                rlSpeechBulContent.removeAllViews();
                rlSpeechBulContent.addView(mSpeechBulPager.getRootView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                rlSpeechBulContent.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onCloseSpeechBulletScreen() {
        mWeakHandler.post(new Runnable() {
            @Override
            public void run() {
                mSpeechBulPager = null;
                rlSpeechBulContent.removeAllViews();
                rlSpeechBulContent.setVisibility(View.INVISIBLE);
            }
        });
    }
}
