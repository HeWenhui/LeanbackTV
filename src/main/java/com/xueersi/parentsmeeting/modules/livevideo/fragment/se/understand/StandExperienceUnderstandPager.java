package com.xueersi.parentsmeeting.modules.livevideo.fragment.se.understand;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.QuestionShowAction;

public class StandExperienceUnderstandPager<T extends IStandExperienceUnderstandContract.IUnderStandPresenter> extends BasePager
        implements QuestionShowAction {

//    private static StandExperienceUnderstandPager mPager;
    /**
     * 听懂了
     */
    public final static int STAND_EXPERIENCE_UNDERSTAND = 0;
    /**
     * 似懂非懂
     */
    public static final int STAND_EXPERIENCE_LITTLE_UNDERSTAND = 1;
    /**
     * 没懂
     */
    public static final int STAND_EXPERIENCE_NO_UNDERSTAND = 2;
    /**
     * 听懂了
     */
    private ImageView ivUnderStandBtn;
    /**
     * 没有听懂
     */
    private ImageView ivNoUnderStandBtn;
    /**
     * 似懂非懂
     */
    private ImageView ivLittleUnderStandBtn;
    //懂了吗标题
    private TextView tvTitle;
    /**
     * 懂了么点击事件监听器
     */
    private T iUnderStandPresenter;

    private VideoLivePlayBackEntity mVideoEntity;

//    public static StandExperienceUnderstandPager getInstance(Context context, VideoLivePlayBackEntity mVideoEntity) {
//        if (mPager == null) {
//            mPager = new StandExperienceUnderstandPager(context, mVideoEntity);
//        }
//        return mPager;
//    }

    public StandExperienceUnderstandPager(Context context, VideoLivePlayBackEntity mVideoEntity, T
            iUnderStandPresenter) {
        super(context);
        this.mVideoEntity = mVideoEntity;
        this.iUnderStandPresenter = iUnderStandPresenter;
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_standexperience_understand, null);
        ivUnderStandBtn = view.findViewById(R.id.iv_livevideo_stand_experience_understand_understand);
        ivLittleUnderStandBtn = view.findViewById(R.id.iv_livevideo_stand_experience_understand_little_understand);
        ivNoUnderStandBtn = view.findViewById(R.id.iv_livevideo_stand_experience_understand_no_understand);
        tvTitle = view.findViewById(R.id.tv_livevideo_stand_experience_recommond_course_title);
        return view;
    }

//    HashMap<String, String> map;

    @Override
    public void initData() {
//        if (!TextUtils.isEmpty(mVideoEntity.getUnderStandDifficultyTitle())) {
//            tvTitle.setText(mVideoEntity.getUnderStandDifficultyTitle());
//        }
//        map = mVideoEntity.getUnderStandDifficulty();
//        Iterator<String> iterator = map.keySet().iterator();
//
//
//        if (iterator.hasNext()) {
//            String key = iterator.next();
//
//        }
    }

    @Override
    public void initListener() {
        super.initListener();
        ivUnderStandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string
                        .stand_experience_1703001));
                clickEvent(STAND_EXPERIENCE_UNDERSTAND);
            }
        });
        ivLittleUnderStandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string
                        .stand_experience_1703002));
                clickEvent(STAND_EXPERIENCE_LITTLE_UNDERSTAND);
            }
        });
        ivNoUnderStandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string
                        .stand_experience_1703003));
                clickEvent(STAND_EXPERIENCE_NO_UNDERSTAND);
            }
        });
    }

    private void clickEvent(int sign) {
        if (iUnderStandPresenter != null) {
            iUnderStandPresenter.onClick(sign);
        }
    }


//    public void setUnderStandListener(IStandExperienceUnderstandContract.IUnderStandListener iUnderStandListener) {
//        this.iUnderStandPresenter = iUnderStandListener;
//    }

    @Override
    public void onQuestionShow(VideoQuestionLiveEntity videoQuestionLiveEntity, boolean isShow) {
        if (iUnderStandPresenter != null) {
            iUnderStandPresenter.removeView();
        }
    }
}
