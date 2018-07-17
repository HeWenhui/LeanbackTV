package com.xueersi.parentsmeeting.modules.livevideo.understand.business;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lyqai on 2018/7/17.
 */

public class UnderstandBll implements UnderstandAction, Handler.Callback {
    private String TAG = "UnderstandBll";
    private String understandEventId = LiveVideoConfig.LIVE_DOYOUSEE;
    private Activity activity;
    private LiveGetInfo mGetInfo;
    private UnderstandHttp understandHttp;
    private LiveAndBackDebug liveAndBackDebug;
    private RelativeLayout rlQuestionContent;
    private LogToFile mLogtf;
    /** 当前是否正在显示懂了吗 */
    private boolean mIsShowUnderstand = false;

    private WeakHandler mVPlayVideoControlHandler = new WeakHandler(this);

    public UnderstandBll(Activity activity, LiveAndBackDebug liveAndBackDebug) {
        this.activity = activity;
        this.liveAndBackDebug = liveAndBackDebug;
        mLogtf = new LogToFile(activity, TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        mLogtf.clear();
    }

    public void setGetInfo(LiveGetInfo mGetInfo) {
        this.mGetInfo = mGetInfo;
    }

    public void setUnderstandHttp(UnderstandHttp understandHttp) {
        this.understandHttp = understandHttp;
    }

    public void initView(RelativeLayout bottomContent, boolean isLand) {

    }

    @Override
    public void understand(final String nonce) {
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "understandReceive");
        liveAndBackDebug.umsAgentDebugSys(understandEventId, mData);
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                final View underatandView = activity.getLayoutInflater().inflate(R.layout.layout_livevideo_understand,
                        rlQuestionContent,
                        false);
                ((TextView) underatandView.findViewById(R.id.tv_livevideo_under_user)).setText(mGetInfo.getStuName
                        () + " 你好");
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) underatandView.getLayoutParams();
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                rlQuestionContent.addView(underatandView, params);
                View.OnClickListener listener = new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        boolean isUnderstand = v.getId() == R.id.tv_livevideo_understand_understand;
                        mLogtf.d("understand:isUnderstand=" + isUnderstand);
                        String nonce = "" + StableLogHashMap.creatNonce();
                        understandHttp.understand(isUnderstand, nonce);
                        mIsShowUnderstand = false;
                        Map<String, String> mData = new HashMap<>();
                        mData.put("logtype", "sendUnderstand");
                        mData.put("answerType", isUnderstand ? "1" : "0");
                        mData.put("expect", "1");
                        mData.put("nonce", "" + nonce);
                        mData.put("sno", "3");
                        mData.put("stable", "1");
                        liveAndBackDebug.umsAgentDebugInter(understandEventId, mData);
                    }
                };
                activity.findViewById(R.id.tv_livevideo_understand_donotunderstand).setOnClickListener(listener);
                activity.findViewById(R.id.tv_livevideo_understand_understand).setOnClickListener(listener);
                postDelayedIfNotFinish(new Runnable() {
                    @Override
                    public void run() {
                        if (mIsShowUnderstand) {
                            Map<String, String> mData = new HashMap<>();
                            mData.put("logtype", "understandTimeout");
                            liveAndBackDebug.umsAgentDebugSys(understandEventId, mData);
                            mIsShowUnderstand = false;
                        }
                    }
                }, 10000);
                activity.getWindow().getDecorView().requestLayout();
                activity.getWindow().getDecorView().invalidate();
                Map<String, String> mData = new HashMap<>();
                mData.put("logtype", "showUnderstand");
                mData.put("nonce", "" + nonce);
                mData.put("ex", "Y");
                mData.put("sno", "2");
                mData.put("stable", "1");
                liveAndBackDebug.umsAgentDebugPv(understandEventId, mData);
            }
        };
        mVPlayVideoControlHandler.post(runnable);
        mIsShowUnderstand = true;
    }

    /**
     * 试题布局隐藏
     */
    private void understandViewGone() {
        mIsShowUnderstand = false;
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    public void postDelayedIfNotFinish(Runnable r, long delayMillis) {
        if (activity.isFinishing()) {
            return;
        }
        mVPlayVideoControlHandler.postDelayed(r, delayMillis);
    }
}
