package com.xueersi.parentsmeeting.modules.livevideo.understand.business;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

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
    //是否是小英
    private boolean isSmallEnglish = true;
    private WeakHandler mVPlayVideoControlHandler = new WeakHandler(this);
    //显示懂了吗的布局
    View understandView = null;
    View oldUnderstandView = null;
    public final int CANCEL_REDPAG = 1;

    public UnderstandBll(Activity activity, LiveAndBackDebug liveAndBackDebug) {
        this.activity = activity;
        this.liveAndBackDebug = liveAndBackDebug;
        mLogtf = new LogToFile(activity, TAG, new File(Environment.getExternalStorageDirectory(),
                "parentsmeeting/log/" + TAG
                        + ".txt"));
        mLogtf.clear();
    }

    public void setGetInfo(LiveGetInfo mGetInfo) {
        this.mGetInfo = mGetInfo;
        if (mGetInfo != null) {
            isSmallEnglish = mGetInfo.getSmallEnglish();
        }
    }

    public void setUnderstandHttp(UnderstandHttp understandHttp) {
        this.understandHttp = understandHttp;
    }

    public void initView(RelativeLayout bottomContent, boolean isLand) {
        rlQuestionContent = bottomContent;
    }

    @Override
    public void understand(final String nonce) {
//        oldUnderstandView =
        if (understandView != null && understandView.getParent() != null && understandView.getParent() ==
                rlQuestionContent) {
            removeView(rlQuestionContent, understandView);
//            rlQuestionContent.removeView(understandView);
        }
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "understandReceive");
        liveAndBackDebug.umsAgentDebugSys(understandEventId, mData);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                RelativeLayout.LayoutParams params = null;
                //如果不是小英
                if (!isSmallEnglish) {
                    understandView = activity.getLayoutInflater().inflate(R.layout.layout_livevideo_understand,
                            rlQuestionContent, false);
                    understandView.findViewById(R.id.tv_livevideo_understand_donotunderstand).setOnClickListener
                            (listener);
                    understandView.findViewById(R.id.tv_livevideo_understand_understand).setOnClickListener(listener);
                    ((TextView) understandView.findViewById(R.id.tv_livevideo_under_user)).setText(mGetInfo.getStuName
                            () + " 你好");
                    params = (RelativeLayout.LayoutParams) understandView.getLayoutParams();
                    if (params == null) {
                        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT);
                    }
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);

                } else {
                    understandView = View.inflate(activity, R.layout.layout_livevideo_small_english_understand, null);
                    understandView.findViewById(R.id.iv_livevideo_small_english_understand).setOnClickListener
                            (smallEnglishListener);
                    understandView.findViewById(R.id.iv_livevideo_small_english_no_understand).setOnClickListener
                            (smallEnglishListener);
                    understandView.findViewById(R.id.iv_livevideo_small_english_close).setOnClickListener
                            (smallEnglishCloseListener);

                    params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                            .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    //在中间位置显示
                    LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();

                    Drawable drawable = activity.getResources().getDrawable(R.drawable
                            .bg_livevideo_small_english_understand_board);
                    int draweight = drawable.getIntrinsicWidth();

                    params.leftMargin = (liveVideoPoint.x3 - liveVideoPoint.x2 - draweight) / 2;

                    params.addRule(RelativeLayout.CENTER_VERTICAL);
                }
                rlQuestionContent.getHandler().removeCallbacks(closeRedPackage);
                rlQuestionContent.addView(understandView, params);
                rlQuestionContent.postDelayed(closeRedPackage, 10000);//十秒之后关闭
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

    Runnable closeRedPackage = new Runnable() {
        @Override
        public void run() {
            if (understandView != null && understandView.getParent() != null && understandView.getParent
                    () == rlQuestionContent) {
                removeView(rlQuestionContent, understandView);
                Map<String, String> mData = new HashMap<>();
                mData.put("logtype", "understandTimeout");
                liveAndBackDebug.umsAgentDebugSys(understandEventId, mData);
                mIsShowUnderstand = false;
            }
        }
    };//十秒之后自动关闭

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

            removeView(rlQuestionContent, understandView);
        }
    };
    View.OnClickListener smallEnglishListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean isUnderstand = v.getId() == R.id.iv_livevideo_small_english_understand;
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

            removeView(rlQuestionContent, understandView);
        }
    };
    private View.OnClickListener smallEnglishCloseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            removeView(rlQuestionContent, understandView);
        }
    };

    private void removeView(ViewGroup viewParent, View view) {
        if (view != null && viewParent != null && view.getParent() == viewParent) {
            viewParent.removeView(view);

        }
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
