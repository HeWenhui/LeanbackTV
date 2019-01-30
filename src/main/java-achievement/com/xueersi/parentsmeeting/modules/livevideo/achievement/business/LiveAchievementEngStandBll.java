package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.page.EnAchievePager;
import com.xueersi.parentsmeeting.modules.livevideo.achievement.page.EnStandAchievePager;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

import java.util.ArrayList;

/**
 * Created by linyuqiang on 2018/11/21.
 * 小英本场成就-站立直播
 */
public class LiveAchievementEngStandBll implements StarInteractAction {
    private String TAG = "LiveAchievementEngStandBll";
    protected Logger logger = LiveLoggerFactory.getLogger(this.getClass().getSimpleName());
    private LiveGetInfo mLiveGetInfo;
    private Activity activity;
    private RelativeLayout bottomContent;
    private EnStandAchievePager enAchievePager;

    public LiveAchievementEngStandBll(Activity activity, int liveType, LiveGetInfo mLiveGetInfo, boolean mIsLand) {
        this.activity = activity;
        this.mLiveGetInfo = mLiveGetInfo;
        mLiveGetInfo.getStarCount();
    }

    public void initView(RelativeLayout bottomContent, RelativeLayout mContentView) {
        this.bottomContent = bottomContent;
        RelativeLayout relativeLayout = bottomContent.findViewById(R.id.rl_livevideo_english_content);
        relativeLayout.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams layoutParams = relativeLayout.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        relativeLayout.setLayoutParams(layoutParams);
        relativeLayout.setBackgroundColor(Color.TRANSPARENT);
        enAchievePager = new EnStandAchievePager(activity, relativeLayout, mLiveGetInfo);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) enAchievePager.getRootView().getLayoutParams();
        LiveVideoPoint videoPoint = LiveVideoPoint.getInstance();
        lp.rightMargin += (videoPoint.screenWidth - videoPoint.x4);
        logger.d("initView:rightMargin=" + lp.rightMargin);
//        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        relativeLayout.addView(enAchievePager.getRootView(), lp);
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

}