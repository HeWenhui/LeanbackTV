package com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.bussiness;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.http.EvaluateResponseParser;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.pager.BaseEvaluateTeacherPaper;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.pager.EvaluateTeacherPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.pager.PrimaryChineseEvaluateTeacherPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.pager.PrimaryScienceEvaluateTeacherPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.evaluateteacher.pager.SmallEnglishEvaluateTeacherPager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.LiveBackPlayerFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by：WangDe on 2018/12/2 22:06
 */
public class EvaluateTeacherPlayBackBll extends LiveBackBaseBll implements IShowEvaluateAction, IButtonOnClick {
    LiveBackPlayerFragment liveBackPlayVideoFragment;
    RelativeLayout bottomContent;
    private BaseEvaluateTeacherPaper evaluateTeacherPager;
    private RelativeLayout rlLiveMessageContent;
    private LiveHttpManager mHttpManager;
    private int reSubmitCount = 0;
    EvaluateResponseParser mParser;

    public EvaluateTeacherPlayBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object>
            businessShareParamMap) {
        super.onCreate(mVideoEntity, liveGetInfo, businessShareParamMap);
        if (liveGetInfo != null && 1 == mVideoEntity.getEvaluateIsOpen()) {
            mParser = new EvaluateResponseParser();
            mHttpManager = liveBackBll.getmHttpManager();
            if (liveGetInfo.getIsArts() == 1) {
                logger.i("IsArts:" + liveGetInfo.getIsArts() + " IsSmallEnglish:" + liveGetInfo.getSmallEnglish());
                if (liveGetInfo.getSmallEnglish()) {
                    evaluateTeacherPager = new SmallEnglishEvaluateTeacherPager(mContext, liveGetInfo);
                } else {
                    evaluateTeacherPager = new EvaluateTeacherPager(mContext, liveGetInfo);
                }
                getArtsEvaluateOption(liveGetInfo.getSmallEnglish());
            } else if (liveGetInfo.getIsArts() == 0) {
                logger.i("IsArts:" + liveGetInfo.getIsArts() + " IsPrimaryScience:" + liveGetInfo.getIsPrimarySchool());
                if (1 == liveGetInfo.getIsPrimarySchool()) {
                    evaluateTeacherPager = new PrimaryScienceEvaluateTeacherPager(mContext, liveGetInfo);
                } else {
                    evaluateTeacherPager = new EvaluateTeacherPager(mContext, liveGetInfo);
                }
                getSciecneEvaluateOption();
            } else if (liveGetInfo.getIsArts() == 2) {
                logger.i("IsArts:" + liveGetInfo.getIsArts());
                if (liveGetInfo.isPrimaryChinese()) {
                    evaluateTeacherPager = new PrimaryChineseEvaluateTeacherPager(mContext, liveGetInfo);
                } else {
                    evaluateTeacherPager = new EvaluateTeacherPager(mContext, liveGetInfo);
                }
                getChsEvaluateOption();
            } else {
                return;
            }
            evaluateTeacherPager.setIShowEvaluateAction(this);
            evaluateTeacherPager.setButtonOnClick(this);
        }
    }

    public void setLiveFragmentBase(LiveBackPlayerFragment liveBackPlayVideoFragment) {
        this.liveBackPlayVideoFragment = liveBackPlayVideoFragment;
    }


    @Override
    public boolean showPager() {
        if (liveGetInfo.isShowHightFeedback()) {
            return false;
        }
        if (0 != mVideoEntity.getEvaluateTimePer() && ((liveBackBll.getvPlayer().getCurrentPosition() + 0.0) /
                liveBackBll.getvPlayer().getDuration()) > mVideoEntity.getEvaluateTimePer()) {
            logger.i("showEvaluateTeacher");
            liveBackBll.getvPlayer().stop();
            liveBackBll.getvPlayer().release();
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
        reSubmitCount = 0;
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


    private void quitLive() {
        logger.i("quit livevideo");
        UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string
                .evaluate_teacher_1708002));
        if (liveBackPlayVideoFragment.isLandSpace()) {
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
                logger.i("uploadEvaluation fail");
                evaluateTeacherPager.showUploadFailPager();
                evaluateTeacherPager.setReUpload();
                super.onPmFailure(error, msg);
            }

        };
        if (liveGetInfo.getIsArts() == 1) {
            mHttpManager.saveArtsEvaluationTeacher(liveGetInfo.getId(), mVideoEntity.getCourseId(), liveGetInfo.getMainTeacherInfo().getTeacherId(), teacherEvaluLevel, teacherEvaluOption, liveGetInfo.getTeacherId(),
                    tutorEvaluLevel, tutorEvaluOption, mVideoEntity.getClassId(), callBack);
        } else if (liveGetInfo.getIsArts() == 0) {
            mHttpManager.saveScienceEvaluationTeacher(liveGetInfo.getId(), mVideoEntity.getCourseId(), liveGetInfo.getMainTeacherInfo().getTeacherId()
                    , teacherEvaluLevel, teacherEvaluOption, liveGetInfo.getTeacherId(),
                    tutorEvaluLevel, tutorEvaluOption, mVideoEntity.getClassId(), callBack);
        } else if (liveGetInfo.getIsArts() == 2) {
            mHttpManager.saveChsEvaluationTeacher(liveGetInfo.getId(), mVideoEntity.getCourseId(), liveGetInfo.getMainTeacherInfo().getTeacherId(), teacherEvaluLevel, teacherEvaluOption, liveGetInfo.getTeacherId(),
                    tutorEvaluLevel, tutorEvaluOption, mVideoEntity.getClassId(), callBack);
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
