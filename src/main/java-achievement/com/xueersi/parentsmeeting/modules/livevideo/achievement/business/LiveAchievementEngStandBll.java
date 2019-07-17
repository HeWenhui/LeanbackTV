package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.page.EnStandAchievePager;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.AimRealTimeValEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.BetterMeEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ArtsExtLiveInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

import java.util.ArrayList;

/**
 * Created by linyuqiang on 2018/11/21.
 * 小英本场成就-站立直播
 */
public class LiveAchievementEngStandBll implements StarInteractAction, EnPkInteractAction, BetterMeInteractAction {
    private String TAG = "LiveAchievementEngStandBll";
    protected Logger logger = LiveLoggerFactory.getLogger(this.getClass().getSimpleName());
    private LiveGetInfo mLiveGetInfo;
    private Activity activity;
    private LiveViewAction liveViewAction;
    private EnStandAchievePager enAchievePager;

    public LiveAchievementEngStandBll(Activity activity, int liveType, LiveGetInfo mLiveGetInfo, boolean mIsLand) {
        this.activity = activity;
        this.mLiveGetInfo = mLiveGetInfo;
    }

    public void initView(LiveViewAction liveViewAction) {
        this.liveViewAction = liveViewAction;
        RelativeLayout relativeLayout = liveViewAction.findViewById(R.id.rl_livevideo_english_content);
        relativeLayout.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams layoutParams = relativeLayout.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        LayoutParamsUtil.setViewLayoutParams(relativeLayout, layoutParams);
        relativeLayout.setBackgroundColor(Color.TRANSPARENT);
        enAchievePager = new EnStandAchievePager(activity, relativeLayout, mLiveGetInfo);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) enAchievePager.getRootView().getLayoutParams();
        LiveVideoPoint videoPoint = LiveVideoPoint.getInstance();
        lp.leftMargin = videoPoint.screenWidth - videoPoint.x4 + SizeUtils.Dp2Px(activity, 10);
        // lp.topMargin = SizeUtils.Dp2Px(activity,6);
        logger.d("initView:rightMargin=" + lp.rightMargin);
//        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        relativeLayout.addView(enAchievePager.getRootView(), lp);
    }

    public void setAchievementLayout(ArtsExtLiveInfo extLiveInfo) {
        if (enAchievePager != null) {
            enAchievePager.setRlAchieveContent(extLiveInfo);
        }
    }

    @Override
    public void onStarStart(ArrayList<String> data, String starid, String answer, String nonce) {

    }

    @Override
    public void onStarStop(String id, ArrayList<String> answer, String nonce) {

    }

    @Override
    public void onSendMsg(String msg) {

    }

    @Override
    public void onGetStar(StarAndGoldEntity starAndGoldEntity) {
        if (enAchievePager != null) {
            enAchievePager.onGetStar(starAndGoldEntity);
        }
    }

    @Override
    public void onStarAdd(int star, float x, float y) {
        if (enAchievePager != null) {
            enAchievePager.onStarAdd(star, x, y);
        }
    }

    @Override
    public void onEnglishPk() {
        if (enAchievePager != null) {
            enAchievePager.onEnglishPk();
        }
    }

    @Override
    public void updateEnpk(EnTeamPkRankEntity enTeamPkRankEntity) {
        if (enAchievePager != null) {
            enAchievePager.updateEnpk(enTeamPkRankEntity);
        }
    }

    @Override
    public void onBetterMeUpdate(AimRealTimeValEntity aimRealTimeValEntity) {
        if (enAchievePager != null) {
            enAchievePager.onBetterMeUpdate(aimRealTimeValEntity);
        }
    }

    @Override
    public void onReceiveBetterMe(BetterMeEntity betterMeEntity, boolean isNotice) {
        if (enAchievePager != null) {
            enAchievePager.onReceiveBetterMe(betterMeEntity, isNotice);
        }
    }

    @Override
    public void onBetterMeLate() {
        if (enAchievePager != null) {
            enAchievePager.onBetterMeLate();
        }
    }
}