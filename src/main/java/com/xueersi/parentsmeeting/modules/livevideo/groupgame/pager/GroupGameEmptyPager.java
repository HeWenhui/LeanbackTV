package com.xueersi.parentsmeeting.modules.livevideo.groupgame.pager;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.entity.GroupGameTestInfosEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareSecHttp;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseCoursewareNativePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseEnglishH5CoursewarePager;

import java.util.List;

public class GroupGameEmptyPager extends BaseCoursewareNativePager implements BaseEnglishH5CoursewarePager {
    private BaseEnglishH5CoursewarePager baseEnglishH5CoursewarePager;
    EnglishH5CoursewareBll englishH5CoursewareBll;
    EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp;
    private RelativeLayout group;
    private LiveGetInfo liveGetInfo;
    private VideoQuestionLiveEntity detailInfo;
    private EnglishH5Entity englishH5Entity;
    private EnglishH5CoursewareBll.OnH5ResultClose onClose;
    private String url;

    public GroupGameEmptyPager(Context context, LiveGetInfo liveGetInfo, VideoQuestionLiveEntity detailInfo, EnglishH5Entity englishH5Entity, EnglishH5CoursewareBll.OnH5ResultClose onClose) {
        super(context);
        this.liveGetInfo = liveGetInfo;
        this.detailInfo = detailInfo;
        this.englishH5Entity = englishH5Entity;
        this.onClose = onClose;
        this.url = englishH5Entity.getUrl();
        initData();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_h5_courseware_groupgame_multiple, null);
        group = (RelativeLayout) view;
        return group;
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public boolean isFinish() {
        if (baseEnglishH5CoursewarePager != null) {
            return baseEnglishH5CoursewarePager.isFinish();
        }
        return false;
    }

    @Override
    public void close() {
        if (baseEnglishH5CoursewarePager != null) {
            baseEnglishH5CoursewarePager.close();
        }
    }

    @Override
    public String getUrl() {
        if (baseEnglishH5CoursewarePager != null) {
            return baseEnglishH5CoursewarePager.getUrl();
        }
        return url;
    }

    @Override
    public void onBack() {
        if (baseEnglishH5CoursewarePager != null) {
            baseEnglishH5CoursewarePager.onBack();
        }
    }

    @Override
    public void destroy() {
        if (baseEnglishH5CoursewarePager != null) {
            baseEnglishH5CoursewarePager.destroy();
        }
    }

    @Override
    public void submitData() {
        if (baseEnglishH5CoursewarePager != null) {
            baseEnglishH5CoursewarePager.submitData();
        } else {
            onClose.onH5ResultClose(this, detailInfo);
        }
    }

    @Override
    public void setEnglishH5CoursewareBll(EnglishH5CoursewareBll englishH5CoursewareBll) {
        this.englishH5CoursewareBll = englishH5CoursewareBll;
    }

    @Override
    public void setEnglishH5CoursewareSecHttp(final EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp) {
        this.englishH5CoursewareSecHttp = englishH5CoursewareSecHttp;
        englishH5CoursewareSecHttp.getCourseWareTests(detailInfo, new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                GroupGameTestInfosEntity mGroupGameTestInfosEntity = (GroupGameTestInfosEntity) objData[0];
                List<GroupGameTestInfosEntity.TestInfoEntity> tests = mGroupGameTestInfosEntity.getTestInfoList();
                if (mGroupGameTestInfosEntity.isAnswered() && tests.isEmpty()) {
                    onClose.onH5ResultClose(GroupGameEmptyPager.this, baseVideoQuestionEntity);
                    return;
                }
                GroupGameTestInfosEntity.TestInfoEntity test = tests.get(0);
                int gameModel = test.getGameModel();
                if (gameModel == LiveQueConfig.GAME_MODEL_2) {
                    GroupGameMultNativePager groupGameMultNativePager = new GroupGameMultNativePager(mContext, liveGetInfo, detailInfo, englishH5Entity, new EnglishH5CoursewareBll.OnH5ResultClose() {
                        @Override
                        public void onH5ResultClose(BaseEnglishH5CoursewarePager baseEnglishH5CoursewarePager, BaseVideoQuestionEntity baseVideoQuestionEntity) {
                            group.removeAllViews();
                            onClose.onH5ResultClose(GroupGameEmptyPager.this, baseVideoQuestionEntity);
                        }
                    });
                    groupGameMultNativePager.setLivePagerBack(livePagerBack);
                    groupGameMultNativePager.setGroupGameTestInfosEntity(mGroupGameTestInfosEntity);
                    group.addView(groupGameMultNativePager.getRootView());
                    baseEnglishH5CoursewarePager = groupGameMultNativePager;
                    baseEnglishH5CoursewarePager.setEnglishH5CoursewareBll(englishH5CoursewareBll);
                    baseEnglishH5CoursewarePager.setEnglishH5CoursewareSecHttp(englishH5CoursewareSecHttp);
                } else {
                    GroupGameNativePager groupGameNativePager = new GroupGameNativePager(mContext, false, liveGetInfo, detailInfo, englishH5Entity, new EnglishH5CoursewareBll.OnH5ResultClose() {
                        @Override
                        public void onH5ResultClose(BaseEnglishH5CoursewarePager baseEnglishH5CoursewarePager, BaseVideoQuestionEntity baseVideoQuestionEntity) {
                            group.removeAllViews();
                            onClose.onH5ResultClose(GroupGameEmptyPager.this, baseVideoQuestionEntity);
                        }
                    });
                    groupGameNativePager.setLivePagerBack(livePagerBack);
                    groupGameNativePager.setGroupGameTestInfosEntity(mGroupGameTestInfosEntity);
                    group.addView(groupGameNativePager.getRootView());
                    baseEnglishH5CoursewarePager = groupGameNativePager;
                    baseEnglishH5CoursewarePager.setEnglishH5CoursewareBll(englishH5CoursewareBll);
                    baseEnglishH5CoursewarePager.setEnglishH5CoursewareSecHttp(englishH5CoursewareSecHttp);
                }
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                super.onDataFail(errStatus, failMsg);
                GroupGameNativePager groupGameNativePager = new GroupGameNativePager(mContext, false, liveGetInfo, detailInfo, englishH5Entity, new EnglishH5CoursewareBll.OnH5ResultClose() {
                    @Override
                    public void onH5ResultClose(BaseEnglishH5CoursewarePager baseEnglishH5CoursewarePager, BaseVideoQuestionEntity baseVideoQuestionEntity) {
                        group.removeAllViews();
                        onClose.onH5ResultClose(GroupGameEmptyPager.this, baseVideoQuestionEntity);
                    }
                });
                groupGameNativePager.setLivePagerBack(livePagerBack);
                group.addView(groupGameNativePager.getRootView());
                baseEnglishH5CoursewarePager = groupGameNativePager;
                baseEnglishH5CoursewarePager.setEnglishH5CoursewareBll(englishH5CoursewareBll);
                baseEnglishH5CoursewarePager.setEnglishH5CoursewareSecHttp(englishH5CoursewareSecHttp);
            }
        });
    }

    @Override
    public BasePager getBasePager() {
        return this;
    }

    @Override
    public void setWebBackgroundColor(int color) {
        if (baseEnglishH5CoursewarePager != null) {
            baseEnglishH5CoursewarePager.setWebBackgroundColor(color);
        }
    }

    @Override
    public EnglishH5Entity getEnglishH5Entity() {
        if (baseEnglishH5CoursewarePager != null) {
            return baseEnglishH5CoursewarePager.getEnglishH5Entity();
        }
        return englishH5Entity;
    }
}
