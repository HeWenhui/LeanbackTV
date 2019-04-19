package com.xueersi.parentsmeeting.modules.livevideo.groupgame.pager;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.business.GetStuActiveTeam;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.InteractiveTeam;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideo.question.business.EnglishH5CoursewareSecHttp;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseCoursewareNativePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.page.BaseEnglishH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

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
        group = new RelativeLayout(mContext);
        return group;
    }

    @Override
    public void initData() {
        super.initData();
        GetStuActiveTeam getStuActiveTeam = ProxUtil.getProxUtil().get(mContext, GetStuActiveTeam.class);
        getStuActiveTeam.getStuActiveTeam(new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                InteractiveTeam mInteractiveTeam = (InteractiveTeam) objData[0];
                logger.d("onDataSucess:mInteractiveTeam=" + mInteractiveTeam);
                GroupGameMultNativePager groupGameMultNativePager = new GroupGameMultNativePager(mContext, liveGetInfo, detailInfo, englishH5Entity, new EnglishH5CoursewareBll.OnH5ResultClose() {
                    @Override
                    public void onH5ResultClose(BaseEnglishH5CoursewarePager baseEnglishH5CoursewarePager, BaseVideoQuestionEntity baseVideoQuestionEntity) {
                        group.removeAllViews();
                        onClose.onH5ResultClose(GroupGameEmptyPager.this, baseVideoQuestionEntity);
                    }
                });
                groupGameMultNativePager.setLivePagerBack(livePagerBack);
                group.addView(groupGameMultNativePager.getRootView());
                baseEnglishH5CoursewarePager = groupGameMultNativePager;
                baseEnglishH5CoursewarePager.setEnglishH5CoursewareBll(englishH5CoursewareBll);
                baseEnglishH5CoursewarePager.setEnglishH5CoursewareSecHttp(englishH5CoursewareSecHttp);
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                logger.d("onDataFail:errStatus=" + errStatus + ",failMsg=" + failMsg);
                super.onDataFail(errStatus, failMsg);
                GroupGameNativePager groupGameMultNativePager = new GroupGameNativePager(mContext, false, liveGetInfo, detailInfo, englishH5Entity, new EnglishH5CoursewareBll.OnH5ResultClose() {
                    @Override
                    public void onH5ResultClose(BaseEnglishH5CoursewarePager baseEnglishH5CoursewarePager, BaseVideoQuestionEntity baseVideoQuestionEntity) {
                        group.removeAllViews();
                        onClose.onH5ResultClose(GroupGameEmptyPager.this, baseVideoQuestionEntity);
                    }
                });
                groupGameMultNativePager.setLivePagerBack(livePagerBack);
                group.addView(groupGameMultNativePager.getRootView());
                baseEnglishH5CoursewarePager = groupGameMultNativePager;
                baseEnglishH5CoursewarePager.setEnglishH5CoursewareBll(englishH5CoursewareBll);
                baseEnglishH5CoursewarePager.setEnglishH5CoursewareSecHttp(englishH5CoursewareSecHttp);
            }
        });
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
        if (baseEnglishH5CoursewarePager != null) {
            baseEnglishH5CoursewarePager.setEnglishH5CoursewareBll(englishH5CoursewareBll);
        }
    }

    @Override
    public void setEnglishH5CoursewareSecHttp(EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp) {
        this.englishH5CoursewareSecHttp = englishH5CoursewareSecHttp;
        if (baseEnglishH5CoursewarePager != null) {
            baseEnglishH5CoursewarePager.setEnglishH5CoursewareSecHttp(englishH5CoursewareSecHttp);
        }
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
