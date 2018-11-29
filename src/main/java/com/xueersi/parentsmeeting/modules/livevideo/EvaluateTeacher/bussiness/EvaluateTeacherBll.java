package com.xueersi.parentsmeeting.modules.livevideo.EvaluateTeacher.bussiness;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.EvaluateTeacher.pager.EvaluateTeacherPager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by：WangDe on 2018/11/27 16:12
 */
public class EvaluateTeacherBll extends LiveBaseBll implements IShowEvaluateAction, IButtonOnClick {
    RelativeLayout bottomContent;
    private EvaluateTeacherPager evaluateTeacherPager;
    private RelativeLayout rlLiveMessageContent;
    Handler mainHandler = new Handler(Looper.getMainLooper());

    public EvaluateTeacherBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);

    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        if (getInfo != null) {
            evaluateTeacherPager = new EvaluateTeacherPager(mContext, getInfo);
            evaluateTeacherPager.setIShowEvaluateAction(this);
            evaluateTeacherPager.setData(getInfo);
            evaluateTeacherPager.setButtonOnClick(this);
        }
        super.onLiveInited(getInfo);

    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        this.bottomContent = bottomContent;
//        super.initView(bottomContent, mIsLand);
    }

    @Override
    public boolean showPager() {
        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (rlLiveMessageContent == null) {
            rlLiveMessageContent = new RelativeLayout(activity);
            rlLiveMessageContent.setId(R.id.rl_livevideo_evalutate_teacher);
            mRootView.addView(rlLiveMessageContent, params);
        } else {
            rlLiveMessageContent.removeAllViews();
        }
        final View view = evaluateTeacherPager.getRootView();
        rlLiveMessageContent.addView(view, params);
        return true;
}

    @Override
    public boolean removePager() {
        if (rlLiveMessageContent != null) {
            rlLiveMessageContent.removeAllViews();
        }
        return false;
    }

    @Override
    public void submit() {
        quitLive();

    }

    @Override
    public void backClass() {
        quitLive();
    }

    private void quitLive(){
        if (mLiveBll.getmIsLand().get()) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            activity.finish();
        } else {
            activity.finish();

        }
    }
}
