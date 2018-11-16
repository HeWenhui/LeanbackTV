package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.lib.framework.utils.EventBusUtil;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LineEvaluator;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.parentsmeeting.modules.livevideo.util.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by linyuqiang on 2018/11/6.
 * 小英本场成就
 */
public class LiveAchievementEngBll implements StarInteractAction {
    private String TAG = "LiveAchievementEngBll";
    protected Logger logger = LiveLoggerFactory.getLogger(this.getClass().getSimpleName());
    Activity activity;
    RelativeLayout bottomContent;

    public LiveAchievementEngBll(Activity activity, int liveType, LiveGetInfo mLiveGetInfo, boolean mIsLand) {
        this.activity = activity;
    }

    public void initView(RelativeLayout bottomContent, RelativeLayout mContentView) {
        this.bottomContent = bottomContent;
        RelativeLayout relativeLayout = bottomContent.findViewById(R.id.rl_livevideo_star_content);
        relativeLayout.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_livevodeo_en_achive, relativeLayout);
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

    }

    @Override
    public void onStarAdd(int star, float x, float y) {

    }
}