package com.xueersi.parentsmeeting.modules.livevideo.understand.business;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.understand.page.SmallChineseUnderstandPager;
import com.xueersi.parentsmeeting.modules.livevideo.understand.page.SmallEnglishUnderstandPager;

import java.util.HashMap;
import java.util.Map;

import cn.dreamtobe.kpswitch.util.KeyboardUtil;

/**
 * Created by linyuqiang on 2018/7/17.
 */

public class UnderstandBll implements UnderstandAction, Handler.Callback {
    private Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
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
    private boolean isSmallEnglish = false;
    private WeakHandler mVPlayVideoControlHandler = new WeakHandler(this);
    //显示懂了吗的布局
    View understandView = null;
    View oldUnderstandView = null;
    public final int CANCEL_REDPAG = 1;

    SmallEnglishUnderstandPager smallEnglishUnderstandPager;
    /**
     * 语文懂了么
     */
    SmallChineseUnderstandPager smallChineseUnderstandPager;

    public UnderstandBll(Activity activity, LiveAndBackDebug liveAndBackDebug) {
        this.activity = activity;
        this.liveAndBackDebug = liveAndBackDebug;
        mLogtf = new LogToFile(activity, TAG);
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
        if (understandView != null) {
            ViewGroup.LayoutParams params = (RelativeLayout.LayoutParams) understandView.getLayoutParams();
            ViewGroup group = (ViewGroup) understandView.getParent();
            if (group != null) {
                group.removeView(understandView);
            }
            if (params == null) {
                rlQuestionContent.addView(understandView);
            } else {
                rlQuestionContent.addView(understandView, params);
            }
        }
    }

    @Override
    public void understand(final String nonce) {
//        oldUnderstandView =
//        if (understandView != null && understandView.getParent() != null && understandView.getParent() ==
//                rlQuestionContent) {
        removeView(rlQuestionContent, understandView);
//            rlQuestionContent.removeView(understandView);
//        }
        Map<String, String> mData = new HashMap<>();
        mData.put("logtype", "understandReceive");
        liveAndBackDebug.umsAgentDebugSys(understandEventId, mData);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams params = null;
                //如果不是小英
                if (!isSmallEnglish) {
                    logger.i("进入到非小英");
                    if (LiveVideoConfig.isSmallChinese) {
                        logger.i("显示语文三分屏");
                        smallChineseUnderstandPager = new SmallChineseUnderstandPager(activity);
                        smallChineseUnderstandPager.setListener(new SmallChineseUnderstandPager.UnderStandListener() {
                            /**关闭当前监听器*/
                            @Override
                            public void close() {
                                removeView(rlQuestionContent, understandView);
                            }

                            /**是否懂了*/
                            @Override
                            public void underStand(boolean underStand) {
                                smallChineseUnderstandOnclick(underStand);
                            }
                        });
                        understandView = smallChineseUnderstandPager.getRootView();

                    } else if (LiveVideoConfig.isPrimary) {
                        understandView = activity.getLayoutInflater().inflate(
                                R.layout.dialog_livevideo_primary_understand, rlQuestionContent, false);
                        understandView.findViewById(R.id.iv_understand_close).setOnClickListener(new View
                                .OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                removeView(rlQuestionContent, understandView);
                            }
                        });
                        understandView.findViewById(R.id.tv_livevideo_understand_donotunderstand).setOnClickListener
                                (listener);
                        understandView.findViewById(R.id.tv_livevideo_understand_understand).setOnClickListener(listener);
                    } else {
                        understandView = activity.getLayoutInflater().inflate(R.layout.layout_livevideo_understand,
                                rlQuestionContent,
                                false);
                        ((TextView) understandView.findViewById(R.id.tv_livevideo_under_user)).setText(mGetInfo
                                .getStuName() + " 你好");
                        understandView.findViewById(R.id.tv_livevideo_understand_donotunderstand).setOnClickListener
                                (listener);
                        understandView.findViewById(R.id.tv_livevideo_understand_understand).setOnClickListener(listener);
                    }

//                    understandView.findViewById(R.id.tv_livevideo_understand_donotunderstand).setOnClickListener
//                            (listener);
//                    understandView.findViewById(R.id.tv_livevideo_understand_understand).setOnClickListener(listener);
//                    ((TextView) understandView.findViewById(R.id.tv_livevideo_under_user)).setText(mGetInfo.getStuName
//                            () + " 你好");
                    params = (RelativeLayout.LayoutParams) understandView.getLayoutParams();
                    if (params == null) {
                        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT);
                    }
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);

                } else {
                    if (smallEnglishUnderstandPager == null) {
                        smallEnglishUnderstandPager = new SmallEnglishUnderstandPager(activity);
                        understandView = smallEnglishUnderstandPager.getRootView();

                        smallEnglishUnderstandPager.setListener(new SmallEnglishUnderstandPager.UnderStandListener() {

                            @Override
                            public void closeListener() {
                                removeView(rlQuestionContent, understandView);
                            }

                            @Override
                            public void underStandListener(boolean underStand) {
                                smallEnglishUnderstandOnclick(underStand);
                            }

                            @Override
                            public void noUnderStandListener(boolean noUnderStand) {
                                smallEnglishUnderstandOnclick(noUnderStand);
                            }
                        });
                    } else {
                        understandView = smallEnglishUnderstandPager.getRootView();
                    }
                    params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                            .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    //在中间位置显示
                }
                mVPlayVideoControlHandler.removeCallbacks(closeRedPackage);
                ViewGroup group = (ViewGroup) understandView.getParent();
                if (group != null) {
                    group.removeView(understandView);
                }
                rlQuestionContent.addView(understandView, params);
                if (LiveVideoConfig.isPrimary) {
//                    rlQuestionContent.postDelayed(closeRedPackage, 100000);//十秒之后关闭
                } else {
                    mVPlayVideoControlHandler.postDelayed(closeRedPackage, 10000);//十秒之后关闭
                }
                activity.getWindow().getDecorView().requestLayout();
                activity.getWindow().getDecorView().invalidate();
                Map<String, String> mData = new HashMap<>();
                mData.put("logtype", "showUnderstand");
                mData.put("nonce", "" + nonce);
                mData.put("ex", "Y");
                mData.put("sno", "2");
                mData.put("stable", "1");
                liveAndBackDebug.umsAgentDebugPv(understandEventId, mData);
                KeyboardUtil.hideKeyboard(understandView);
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

    /**
     * 小学英语三分屏上传日志
     *
     * @param isUnderstand
     */
    private void smallEnglishUnderstandOnclick(boolean isUnderstand) {
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

    /**
     * 小学语文三分屏懂了吗进行http请求，上传日志，同小英
     *
     * @param isUnderstand
     */
    private void smallChineseUnderstandOnclick(boolean isUnderstand) {
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

    private void removeView(ViewGroup viewParent, View view) {
        if (understandView == view) {
            understandView = null;
        }
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
