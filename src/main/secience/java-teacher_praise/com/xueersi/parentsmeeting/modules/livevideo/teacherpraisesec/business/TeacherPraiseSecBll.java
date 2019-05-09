package com.xueersi.parentsmeeting.modules.livevideo.teacherpraisesec.business;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.HalfBodyLiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.studyreport.business.StudyReportAction;
import com.xueersi.parentsmeeting.modules.livevideo.teacherpraisesec.page.SpeechEnergyPager;
import com.xueersi.parentsmeeting.modules.livevideo.teacherpraisesec.page.SpeechPraisePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author chenkun
 * 老师点赞
 */
public class TeacherPraiseSecBll extends LiveBaseBll implements NoticeAction {
    private boolean isAnimStart = false;
    private boolean addEnergy = false;
    private LiveGetInfo getInfo;

    public TeacherPraiseSecBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        this.getInfo = getInfo;
    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        super.initView(bottomContent, mIsLand);
        if (com.xueersi.common.config.AppConfig.DEBUG) {
            Button button = new Button(mContext);
            button.setText("测试");
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            mRootView.addView(button, lp);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showTeacherPraise();
                }
            });
        }
    }

    /**
     * 显示 老师点赞
     */
    public void showTeacherPraise() {
        logger.d("showTeacherPraise");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!isAnimStart) {
                    SpeechPraisePager speechPraisePager = new SpeechPraisePager(mContext, 1 == getInfo.getIsPrimarySchool());
                    mRootView.addView(speechPraisePager.getRootView());
                    speechPraisePager.setOnPagerClose(new LiveBasePager.OnPagerClose() {
                        @Override
                        public void onClose(LiveBasePager basePager) {
                            mRootView.removeView(basePager.getRootView());
                            addEnergy();
                        }
                    });
                }
            }
        });
    }

    private void addEnergy() {
        logger.d("addEnergy:pk=" + getInfo.getIsAllowTeamPk());
        if (!addEnergy && "1".equals(getInfo.getIsAllowTeamPk())) {
//                                        addEnergy = true;
            SpeechEnergyPager speechEnergyPager = new SpeechEnergyPager(mContext);
            mRootView.addView(speechEnergyPager.getRootView());
            speechEnergyPager.setOnPagerClose(new LiveBasePager.OnPagerClose() {
                @Override
                public void onClose(LiveBasePager basePager) {
                    mRootView.removeView(basePager.getRootView());
                }
            });
        }
    }

    private int[] noticeCodes = {
            XESCODE.TEACHER_PRAISE
    };

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.TEACHER_PRAISE:
                showTeacherPraise();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestory() {
        super.onDestory();
    }

    @Override
    public int[] getNoticeFilter() {
        return noticeCodes;
    }
}
