package com.xueersi.parentsmeeting.modules.livevideoOldIJK.teacherpraisesec.business;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveEventBus;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.event.TeachPraiseRusltulCloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.event.TeacherPraiseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.speechfeedback.page.SpeechEnergyPager;
import com.xueersi.parentsmeeting.modules.livevideo.teacherpraisesec.page.SpeechPraisePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.NoticeAction;

import org.greenrobot.eventbus.EventBus;
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
                    LiveEventBus.getDefault(activity).post(new TeacherPraiseEvent(true));
                    SpeechPraisePager speechPraisePager = new SpeechPraisePager(mContext, 1 == getInfo.getIsPrimarySchool());
                    mRootView.addView(speechPraisePager.getRootView());
                    speechPraisePager.setOnPagerClose(new LiveBasePager.OnPagerClose() {
                        @Override
                        public void onClose(LiveBasePager basePager) {
                            mRootView.removeView(basePager.getRootView());
                            boolean add = addEnergy();
                            if (!add) {
                                LiveEventBus.getDefault(activity).post(new TeacherPraiseEvent(false));
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean addEnergy() {
        logger.d("addEnergy:pk=" + getInfo.getIsAllowTeamPk());
        if (!addEnergy && "1".equals(getInfo.getIsAllowTeamPk())) {
//                                        addEnergy = true;
            SpeechEnergyPager speechEnergyPager = new SpeechEnergyPager(mContext);
            mRootView.addView(speechEnergyPager.getRootView());
            speechEnergyPager.setOnPagerClose(new LiveBasePager.OnPagerClose() {
                @Override
                public void onClose(LiveBasePager basePager) {
                    mRootView.removeView(basePager.getRootView());
                    LiveEventBus.getDefault(activity).post(new TeacherPraiseEvent(false));
                    EventBus.getDefault().post(new TeachPraiseRusltulCloseEvent(""));
                }
            });
            return true;
        }
        return false;
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
