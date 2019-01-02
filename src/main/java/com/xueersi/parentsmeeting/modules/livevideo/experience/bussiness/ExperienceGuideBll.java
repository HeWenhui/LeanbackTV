package com.xueersi.parentsmeeting.modules.livevideo.experience.bussiness;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.experience.pager.ExperienceGuidePager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpManager;

import java.util.HashMap;

public class ExperienceGuideBll extends LiveBackBaseBll implements IPagerControl {

    ExperienceGuidePager mGuidePager;
    RelativeLayout rlViewContent;
    boolean iShowGuidePager = false;
    VideoLivePlayBackEntity mVideoEntity;
    LivePlayBackHttpManager livePlayBackHttpManager;

    public ExperienceGuideBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object> businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);
        this.mVideoEntity = mVideoEntity;
        mGuidePager = new ExperienceGuidePager(mContext, this,Long.valueOf(mVideoEntity.getVisitTimeKey()),mVideoEntity.getSubjectId());
        mGuidePager.setSubjeceId(mVideoEntity.getSubjectId());
    }

    @Override
    public void initView() {
//        if (mRootView != null && Long.valueOf(mVideoEntity.getVisitTimeKey()) > 15000) {
            if(mRootView != null && mGuidePager != null){
                livePlayBackHttpManager = new LivePlayBackHttpManager(mContext);
            showPager();
            submitNovicGuide();
//            liveBackBll.getvPlayer().pause();
        }else {
                liveBackBll.removeBusinessBll(this);
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

    private void submitNovicGuide(){
        livePlayBackHttpManager.sumbitExperienceNoviceGuide(UserBll.getInstance().getMyUserInfoEntity()
                .getStuId(), mVideoEntity.getChapterId(), mVideoEntity.getSubjectId(), new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.i("submitNovicGuide");
            }
        });
    }
}
