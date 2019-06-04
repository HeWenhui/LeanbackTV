package com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.bussiness;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoFragment;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.HalfBodyLiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.http.EvaluateResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.pager.BaseEvaluateTeacherPaper;
import com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.pager.EvaluateTeacherPager;
import com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.pager.PrimaryChineseEvaluateTeacherPager;
import com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.pager.PrimaryScienceEvaluateTeacherPager;
import com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.pager.SmallEnglishEvaluateTeacherPager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by：WangDe on 2018/11/27 16:12
 */
public class EvaluateTeacherBll extends LiveBaseBll implements com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.bussiness.IShowEvaluateAction, com.xueersi.parentsmeeting.modules.livevideo.evaluateteacher.bussiness.IButtonOnClick {
    RelativeLayout bottomContent;
    private BaseEvaluateTeacherPaper evaluateTeacherPager;
    private RelativeLayout rlLiveMessageContent;
    private LiveHttpManager mHttpManager;
    private int reSubmitCount = 0;
    LiveVideoFragment liveFragment;
    EvaluateResponseParser mParser;

    public EvaluateTeacherBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);

    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {

        if (getInfo != null   && !(getInfo.getIsArts() == LiveVideoSAConfig.ART_SEC
                && LiveVideoConfig.EDUCATION_STAGE_3.equals(mGetInfo.getEducationStage())
                || LiveVideoConfig.EDUCATION_STAGE_4.equals(mGetInfo.getEducationStage()))) {
            if (getInfo.getEvaluateTeacherEntity() != null && getInfo.getEvaluateTeacherEntity().isEvaluateIsOpen()) {
                mHttpManager = mLiveBll.getHttpManager();
                mParser = new EvaluateResponseParser();
                if (getInfo.getIsArts() == 1) {
                    logger.i("IsArts:" + getInfo.getIsArts() + " IsSmallEnglish:" + getInfo.getSmallEnglish());
                    if (getInfo.getSmallEnglish()) {
                        evaluateTeacherPager = new SmallEnglishEvaluateTeacherPager(mContext, getInfo);
                    } else {
                        evaluateTeacherPager = new EvaluateTeacherPager(mContext, getInfo);
                    }
                    getArtsEvaluateOption(getInfo.getSmallEnglish());
                } else if (getInfo.getIsArts() == 0) {
                    logger.i("IsArts:" + getInfo.getIsArts() + " IsPrimaryScience:" + getInfo.getIsPrimarySchool());

                   // 语文半身 直播，采用语文的皮肤，理科的接口
                   if(getInfo.getPattern() == HalfBodyLiveConfig.LIVE_TYPE_HALFBODY
                           && getInfo.getUseSkin() == HalfBodyLiveConfig.SKIN_TYPE_CH){
                       evaluateTeacherPager = new PrimaryChineseEvaluateTeacherPager(mContext, getInfo);
                   } else if (1 == getInfo.getIsPrimarySchool()) {
                        evaluateTeacherPager = new PrimaryScienceEvaluateTeacherPager(mContext, getInfo);
                    } else {
                        evaluateTeacherPager = new EvaluateTeacherPager(mContext, getInfo);
                    }
                    getSciecneEvaluateOption();
                } else if (getInfo.getIsArts() == 2) {
                    logger.i("IsArts:" + getInfo.getIsArts());
                    if (LiveVideoConfig.isSmallChinese){
                        evaluateTeacherPager = new PrimaryChineseEvaluateTeacherPager(mContext, getInfo);
                    } else {
                        evaluateTeacherPager = new EvaluateTeacherPager(mContext, getInfo);
                    }
                    getChsEvaluateOption();
                } else {
                    return;
                }
            evaluateTeacherPager.setIShowEvaluateAction(this);
            evaluateTeacherPager.setButtonOnClick(this);
            } else {
                mLiveBll.removeBusinessBll(this);
            }
        }
        super.onLiveInited(getInfo);
    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        this.bottomContent = bottomContent;
    }

    @Override
    public boolean showPager() {
        if ((mGetInfo.getEvaluateTeacherEntity() != null && System.currentTimeMillis() / 1000 > mGetInfo.getEvaluateTeacherEntity().getEvaluateTime())) {
            logger.i("showEvaluateTeacher");
            logger.i("currenttime:" + System.currentTimeMillis() + "  getEvaluatetime:" + mGetInfo.getEvaluateTeacherEntity().getEvaluateTime());
            liveFragment.stopPlayer();
            mLiveBll.onIRCmessageDestory();
            final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            if (rlLiveMessageContent == null) {
                rlLiveMessageContent = new RelativeLayout(activity);
                rlLiveMessageContent.setId(R.id.rl_livevideo_evalutate_teacher);
                mRootView.addView(rlLiveMessageContent, params);
            } else {
                rlLiveMessageContent.removeAllViews();
            }
            View view = evaluateTeacherPager.getRootView();
            rlLiveMessageContent.addView(view, params);
            UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string
                    .evaluate_teacher_1708001));
            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean removePager() {
        if (rlLiveMessageContent != null) {
            rlLiveMessageContent.removeAllViews();
        }
        return false;
    }

    @Override
    public void submit(Map<String, String> mainEva, Map<String, String> tutorEva) {
        String teacherEvaluLevel = mainEva.get("eva");
        String teacherEvaluOption = "";
        String tutorEvaluLevel = tutorEva.get("eva");
        String tutorEvaluOption = "";
        teacherEvaluOption = getEvaluteOption(mainEva);
        tutorEvaluOption = getEvaluteOption(tutorEva);
        uploadEvaluation(teacherEvaluLevel, teacherEvaluOption, tutorEvaluLevel, tutorEvaluOption);
    }

    @Override
    public void close() {
        quitLive();
    }

    public void setLiveFragment(LiveVideoFragment liveFragment) {
        this.liveFragment = liveFragment;
    }

    private void quitLive() {
        logger.i("quit livevideo");
        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string
                .evaluate_teacher_1708002));
        if (mLiveBll.getmIsLand().get()) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            activity.finish();
        } else {
            activity.finish();
        }
    }

    private String getEvaluteOption(Map<String, String> data) {
        String option = "";
        for (Map.Entry entry : data.entrySet()) {
            if ("1".equals(entry.getValue())) {
                option += entry.getKey() + ",";
            }
        }
        option = option.substring(0, option.length() - 1);
        return option;
    }

    /**
     * 上传评价结果
     *
     * @param teacherEvaluLevel
     * @param teacherEvaluOption
     * @param tutorEvaluLevel
     * @param tutorEvaluOption
     */
    private void uploadEvaluation(final String teacherEvaluLevel, final String
            teacherEvaluOption, final String tutorEvaluLevel, final String tutorEvaluOption) {
        HttpCallBack callBack = new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.i("uploadEvaluation success");
                evaluateTeacherPager.showSuccessPager(new EvaluateTeacherPager.CountDownCallback() {
                    @Override
                    public void finishVideo() {
                        quitLive();
                    }
                });
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                logger.i("uploadEvaluation fail");
                evaluateTeacherPager.showUploadFailPager();
                evaluateTeacherPager.setReUpload();
            }

        };
        if (mGetInfo.getIsArts() == 1) {
            mHttpManager.saveArtsEvaluationTeacher(mLiveId, mGetInfo.getStudentLiveInfo().getCourseId(), mGetInfo.getMainTeacherId(),
                    teacherEvaluLevel, teacherEvaluOption, mGetInfo.getTeacherId(), tutorEvaluLevel,
                    tutorEvaluOption, mGetInfo.getStudentLiveInfo().getClassId(), callBack);
        } else if (mGetInfo.getIsArts() == 0) {
            mHttpManager.saveScienceEvaluationTeacher(mLiveId, mGetInfo.getStudentLiveInfo().getCourseId(), mGetInfo.getMainTeacherId(),
                    teacherEvaluLevel, teacherEvaluOption, mGetInfo.getTeacherId(), tutorEvaluLevel,
                    tutorEvaluOption, mGetInfo.getStudentLiveInfo().getClassId(), callBack);
        } else if (mGetInfo.getIsArts() == 2) {
            mHttpManager.saveChsEvaluationTeacher(mLiveId, mGetInfo.getStudentLiveInfo().getCourseId(), mGetInfo.getMainTeacherId(),
                    teacherEvaluLevel, teacherEvaluOption, mGetInfo.getTeacherId(), tutorEvaluLevel,
                    tutorEvaluOption, mGetInfo.getStudentLiveInfo().getClassId(), callBack);
        }


    }

    private void getChsEvaluateOption() {
        mHttpManager.getChsEvaluationOption(new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.i("arts:success");
                evaluateTeacherPager.setOptionEntity(mParser.parseEvaluateInfo(responseEntity));
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
            }
        });
    }

    private void getArtsEvaluateOption(boolean isSmallEnglish) {
        mHttpManager.getArtsEvaluationOption(isSmallEnglish ? "1" : "0", new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.i("arts:success");
                evaluateTeacherPager.setOptionEntity(mParser.parseEvaluateInfo(responseEntity));
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
            }
        });
    }

    private void getSciecneEvaluateOption() {
        mHttpManager.getSciecneEvaluationOption(new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                evaluateTeacherPager.setOptionEntity(mParser.parseEvaluateInfo(responseEntity));
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
            }
        });
    }
}
