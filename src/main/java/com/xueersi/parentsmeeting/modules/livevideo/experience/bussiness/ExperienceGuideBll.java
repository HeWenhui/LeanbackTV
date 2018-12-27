package com.xueersi.parentsmeeting.modules.livevideo.experience.bussiness;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.experience.pager.ExperienceGuidePager;

import java.util.HashMap;

public class ExperienceGuideBll extends LiveBackBaseBll implements IPagerControl {

    ExperienceGuidePager mGuidePager;
    RelativeLayout rlViewContent;
    boolean iShowGuidePager = false;
    public ExperienceGuideBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object> businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);


    }

    @Override
    public void initView() {
        if (mRootView != null){
            mGuidePager = new ExperienceGuidePager(mContext,this);
            showPager();

//            liveBackBll.getvPlayer().pause();
        }
    }

    @Override
    public boolean showPager() {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (rlViewContent == null) {
            rlViewContent = new RelativeLayout(activity);
            rlViewContent.setId(R.id.rl_livevideo_experience_feedback_quit);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.addView(rlViewContent, params);
        } else {
            rlViewContent.removeAllViews();
        }
        View view = mGuidePager.getRootView();
        logger.i("showpager");
        rlViewContent.addView(view, params);
        return false;
    }

    @Override
    public boolean removePager() {
        if (rlViewContent != null) {
            rlViewContent.removeAllViews();
        }
        return false;
    }
}
