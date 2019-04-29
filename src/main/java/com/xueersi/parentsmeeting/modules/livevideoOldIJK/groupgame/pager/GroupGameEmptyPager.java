package com.xueersi.parentsmeeting.modules.livevideoOldIJK.groupgame.pager;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.entity.BaseVideoQuestionEntity;
import com.xueersi.common.entity.EnglishH5Entity;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.business.GetStuActiveTeam;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.InteractiveTeam;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.entity.GroupGameTestInfosEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.EnglishH5CoursewareBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.business.EnglishH5CoursewareSecHttp;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.BaseCoursewareNativePager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.page.BaseEnglishH5CoursewarePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

import java.util.List;

public class GroupGameEmptyPager extends BaseCoursewareNativePager implements BaseEnglishH5CoursewarePager {
    private BaseEnglishH5CoursewarePager baseEnglishH5CoursewarePager;
    EnglishH5CoursewareBll englishH5CoursewareBll;
    EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp;
    private RelativeLayout group;
    private ImageView ivCourseRefresh;
    private LiveGetInfo liveGetInfo;
    private VideoQuestionLiveEntity detailInfo;
    private EnglishH5Entity englishH5Entity;
    private EnglishH5CoursewareBll.OnH5ResultClose onClose;
    private String url;
    private boolean getTeam;

    public GroupGameEmptyPager(Context context, LiveGetInfo liveGetInfo, VideoQuestionLiveEntity detailInfo, EnglishH5Entity englishH5Entity, EnglishH5CoursewareBll.OnH5ResultClose onClose, boolean getTeam) {
        super(context);
        this.liveGetInfo = liveGetInfo;
        this.detailInfo = detailInfo;
        this.englishH5Entity = englishH5Entity;
        this.onClose = onClose;
        this.url = englishH5Entity.getUrl();
        this.getTeam = getTeam;
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_h5_courseware_groupgame_empty, null);
        group = (RelativeLayout) view;
        ivCourseRefresh = view.findViewById(R.id.iv_livevideo_course_refresh);
        return group;
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void initListener() {
        super.initListener();
        ivCourseRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivCourseRefresh.setVisibility(View.GONE);
                getCourseWareTests("Refresh");
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
    }

    @Override
    public void setEnglishH5CoursewareSecHttp(final EnglishH5CoursewareSecHttp englishH5CoursewareSecHttp) {
        this.englishH5CoursewareSecHttp = englishH5CoursewareSecHttp;
        if (getTeam) {
            getTeaam();
        } else {
            getCourseWareTests("setEnglishH5");
        }
    }

    private InteractiveTeam interactiveTeam;

    private void getTeaam() {
        GetStuActiveTeam getStuActiveTeam = ProxUtil.getProxUtil().get(mContext, GetStuActiveTeam.class);
        //强制更新小组消息，返回值是旧的
        interactiveTeam = getStuActiveTeam.getStuActiveTeam(true, new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                InteractiveTeam interactiveTeam = (InteractiveTeam) objData[0];
                if (interactiveTeam != null && interactiveTeam.getEntities().size() > 1) {
                    getCourseWareTests("getTeaam");
                } else {
                    gotoSignal(null, "onDataSucess");
                }
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                //如果失败，旧的不为空。
                if (interactiveTeam != null && interactiveTeam.getEntities().size() > 1) {
                    getCourseWareTests("getTeaam");
                } else {
                    gotoSignal(null, "onDataFail");
                }
            }
        });
    }

    private void getCourseWareTests(String method) {
        mLogtf.d("onDataSucess:method=" + method);
        englishH5CoursewareSecHttp.getCourseWareTests(detailInfo, new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                GroupGameTestInfosEntity mGroupGameTestInfosEntity = (GroupGameTestInfosEntity) objData[0];
                List<GroupGameTestInfosEntity.TestInfoEntity> tests = mGroupGameTestInfosEntity.getTestInfoList();
                if (mGroupGameTestInfosEntity.isAnswered() || tests.isEmpty()) {
                    onClose.onH5ResultClose(GroupGameEmptyPager.this, baseVideoQuestionEntity);
                    return;
                }
//                mGroupGameTestInfosEntity.setAnswered(true);
                GroupGameTestInfosEntity.TestInfoEntity test = tests.get(0);
                int gameModel = test.getGameModel();
                if (gameModel == LiveQueConfig.GAME_MODEL_2) {
                    GroupGameMultNativePager groupGameMultNativePager = new GroupGameMultNativePager(mContext, liveGetInfo, detailInfo, englishH5Entity, new EnglishH5CoursewareBll.OnH5ResultClose() {
                        @Override
                        public void onH5ResultClose(BaseEnglishH5CoursewarePager baseEnglishH5CoursewarePager, BaseVideoQuestionEntity baseVideoQuestionEntity) {
                            //延迟remove，否则会卡住界面
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    group.removeAllViews();
                                    onClose.onH5ResultClose(GroupGameEmptyPager.this, detailInfo);
                                }
                            });
                        }
                    });
                    groupGameMultNativePager.setLivePagerBack(livePagerBack);
                    groupGameMultNativePager.setGroupGameTestInfosEntity(mGroupGameTestInfosEntity);
                    group.removeAllViews();
                    group.addView(groupGameMultNativePager.getRootView());
                    baseEnglishH5CoursewarePager = groupGameMultNativePager;
                    baseEnglishH5CoursewarePager.setEnglishH5CoursewareBll(englishH5CoursewareBll);
                    baseEnglishH5CoursewarePager.setEnglishH5CoursewareSecHttp(englishH5CoursewareSecHttp);
                } else {
                    gotoSignal(mGroupGameTestInfosEntity, "gameModel");
                }
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                mLogtf.d("onDataFail:errStatus=" + errStatus + ",failMsg=" + failMsg);
                if (errStatus == LiveHttpConfig.HTTP_ERROR_ERROR) {
                    XESToastUtils.showToast(mContext, failMsg + ",请刷新");
                } else {
                    XESToastUtils.showToast(mContext, "请求互动题失败，请刷新");
                }
                ivCourseRefresh.setVisibility(View.VISIBLE);
//                GroupGameNativePager groupGameNativePager = new GroupGameNativePager(mContext, false, liveGetInfo, detailInfo, englishH5Entity, new EnglishH5CoursewareBll.OnH5ResultClose() {
//                    @Override
//                    public void onH5ResultClose(BaseEnglishH5CoursewarePager baseEnglishH5CoursewarePager, BaseVideoQuestionEntity baseVideoQuestionEntity) {
//                        group.removeAllViews();
//                        onClose.onH5ResultClose(GroupGameEmptyPager.this, baseVideoQuestionEntity);
//                    }
//                });
//                groupGameNativePager.setLivePagerBack(livePagerBack);
//                group.addView(groupGameNativePager.getRootView());
//                baseEnglishH5CoursewarePager = groupGameNativePager;
//                baseEnglishH5CoursewarePager.setEnglishH5CoursewareBll(englishH5CoursewareBll);
//                baseEnglishH5CoursewarePager.setEnglishH5CoursewareSecHttp(englishH5CoursewareSecHttp);
            }
        });
    }

    private void gotoSignal(GroupGameTestInfosEntity mGroupGameTestInfosEntity, String method) {
        mLogtf.d("gotoSignal:method=" + method);
        GroupGameNativePager groupGameNativePager = new GroupGameNativePager(mContext, false, liveGetInfo, detailInfo, englishH5Entity, new EnglishH5CoursewareBll.OnH5ResultClose() {
            @Override
            public void onH5ResultClose(BaseEnglishH5CoursewarePager baseEnglishH5CoursewarePager, BaseVideoQuestionEntity baseVideoQuestionEntity) {
                group.removeAllViews();
                onClose.onH5ResultClose(GroupGameEmptyPager.this, baseVideoQuestionEntity);
            }
        });
        groupGameNativePager.setLivePagerBack(livePagerBack);
        groupGameNativePager.setGroupGameTestInfosEntity(mGroupGameTestInfosEntity);
        group.removeAllViews();
        group.addView(groupGameNativePager.getRootView());
        baseEnglishH5CoursewarePager = groupGameNativePager;
        baseEnglishH5CoursewarePager.setEnglishH5CoursewareBll(englishH5CoursewareBll);
        baseEnglishH5CoursewarePager.setEnglishH5CoursewareSecHttp(englishH5CoursewareSecHttp);
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

    @Override
    public boolean isResultRecived() {
        return false;
    }
}
