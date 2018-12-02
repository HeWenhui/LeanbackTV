package com.xueersi.parentsmeeting.modules.livevideo.EvaluateTeacher.bussiness;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.EvaluateTeacher.entity.EvaluateOptionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.EvaluateTeacher.pager.BaseEvaluateTeacherPaper;
import com.xueersi.parentsmeeting.modules.livevideo.EvaluateTeacher.pager.EvaluateTeacherPager;
import com.xueersi.parentsmeeting.modules.livevideo.EvaluateTeacher.pager.PrimaryScienceEvaluateTeacherPager;
import com.xueersi.parentsmeeting.modules.livevideo.EvaluateTeacher.pager.SmallEnglishEvaluateTeacherPager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveFragmentBase;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by：WangDe on 2018/11/27 16:12
 */
public class EvaluateTeacherBll extends LiveBaseBll implements IShowEvaluateAction, IButtonOnClick {
    RelativeLayout bottomContent;
    private BaseEvaluateTeacherPaper evaluateTeacherPager;
    private RelativeLayout rlLiveMessageContent;
    Handler mainHandler = new Handler(Looper.getMainLooper());
    private LiveHttpManager mHttpManager;
    private int reSubmitCount = 0;
    LiveFragmentBase liveFragmentBase;

    public EvaluateTeacherBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);

    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
//        mHttpManager = mLiveBll.getHttpManager();
//        evaluateTeacherPager = new PrimaryScienceEvaluateTeacherPager(mContext, getInfo);
//        evaluateTeacherPager.setIShowEvaluateAction(this);
//        evaluateTeacherPager.setButtonOnClick(this);
//        ResponseEntity responseEntity = new ResponseEntity();
//        String data = "{\"evaluateScore\":{\"1\":\"不满意\",\"2\":\"基本满意\",\"3\":\"满意\"}," +
//                "\"teacherEvaluOption\":{\"1\":[\"没听明白\",\"板书太差\",\"没精神\",\"枯燥无趣\"],\"2\":[\"听懂了\",\"板书工整\",
// \"状态不错\"," +
//                "\"有意思\"],\"3\":[\"清晰易懂\",\"板书美观\",\"激情满满\",\"津津有味\"]},\"tutorEvaluOption\":{\"1\":[\"回复太慢\"," +
//                "\"没听明白\",\"很少沟通\",\"爱答不理\"],\"2\":[\"回复及时\",\"听懂了\",\"及时督促\",\"平易近人\"],\"3\":[\"消息秒回\",\"清晰易懂\"," +
//                "\"主动关注\",\"积极热情\"]}}";
//        try {
//            JSONObject jsonObject = new JSONObject(data);
//            responseEntity.setJsonObject(jsonObject);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        evaluateTeacherPager.setOptionEntity(parseArtsExtLiveInfo(responseEntity));
        if (getInfo != null) {
            if ((getInfo.getArtsExtLiveInfo() != null && getInfo.getArtsExtLiveInfo().isPop()) || (getInfo
                    .getEvaluateTeacherEntity() != null && getInfo.getEvaluateTeacherEntity().isEvaluateIsOpen())) {
                mHttpManager = mLiveBll.getHttpManager();
                evaluateTeacherPager = new EvaluateTeacherPager(mContext, getInfo);
                evaluateTeacherPager.setIShowEvaluateAction(this);
                evaluateTeacherPager.setButtonOnClick(this);
                if (getInfo.getIsArts() == 1) {
                    if (getInfo.getSmallEnglish()) {
                        evaluateTeacherPager = new SmallEnglishEvaluateTeacherPager(mContext, getInfo);
                    } else {
                        evaluateTeacherPager = new EvaluateTeacherPager(mContext, getInfo);
                    }
                    getArtsEvaluateOption(getInfo.getSmallEnglish());
                } else {
                    if (1 == getInfo.getIsPrimarySchool()) {
                        evaluateTeacherPager = new PrimaryScienceEvaluateTeacherPager(mContext, getInfo);
                    }else{
                        evaluateTeacherPager = new EvaluateTeacherPager(mContext, getInfo);
                    }
                    getSciecneEvaluateOption();
                }
                evaluateTeacherPager.setIShowEvaluateAction(this);
                evaluateTeacherPager.setButtonOnClick(this);
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
        liveFragmentBase.stopPlayer();
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
    public void submit(Map<String, String> mainEva, Map<String, String> tutorEva) {
        reSubmitCount = 0;
        String teacherEvaluLevel = mainEva.get("eva");
        String teacherEvaluOption = "";
        String tutorEvaluLevel = tutorEva.get("eva");
        String tutorEvaluOption = "";
        mainEva.remove("eva");
        tutorEva.remove("eva");
        teacherEvaluOption = getEvaluteOption(mainEva);
        tutorEvaluOption = getEvaluteOption(tutorEva);
        uploadEvaluation(teacherEvaluLevel, teacherEvaluOption, tutorEvaluLevel, tutorEvaluOption);
    }

    @Override
    public void close() {
        quitLive();
    }

    public void setLiveFragmentBase(LiveFragmentBase liveFragmentBase) {
        this.liveFragmentBase = liveFragmentBase;
    }

    private void quitLive() {
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
//                if (reSubmitCount < 5) {
//                    uploadEvaluation(teacherEvaluLevel, teacherEvaluOption, tutorEvaluLevel, tutorEvaluOption);
//                    reSubmitCount++;
//                } else {
                evaluateTeacherPager.showUploadFailPager();
//                }

            }

        };
        if (mGetInfo.getIsArts() == 1) {
            mHttpManager.saveArtsEvaluationTeacher(mLiveId, mGetInfo.getStuCouId(),
                    teacherEvaluLevel, teacherEvaluOption, tutorEvaluLevel, tutorEvaluOption, callBack);
        } else {
            mHttpManager.saveScienceEvaluationTeacher(mLiveId, mGetInfo.getStuCouId(), mGetInfo.getMainTeacherId(),
                    teacherEvaluLevel, teacherEvaluOption, mGetInfo.getTeacherId(), tutorEvaluLevel,
                    tutorEvaluOption, mGetInfo.getStudentLiveInfo().getClassId(), callBack);
        }


    }

    private void getArtsEvaluateOption(boolean isSmallEnglish) {
        mHttpManager.getArtsEvaluationOption(isSmallEnglish ? "1" : "0", new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.i("arts:success");
                evaluateTeacherPager.setOptionEntity(parseArtsExtLiveInfo(responseEntity));
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
                evaluateTeacherPager.setOptionEntity(parseScienceExtLiveInfo(responseEntity));
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
            }
        });
    }

    private EvaluateOptionEntity parseArtsExtLiveInfo(ResponseEntity responseEntity) {
        EvaluateOptionEntity evaluateOptionEntity = new EvaluateOptionEntity();

        JSONObject data = (JSONObject) responseEntity.getJsonObject();

        JSONObject evaScoreJson = data.optJSONObject("evaluateScore");
        Map<String, String> evaScoreMap = new HashMap<>();
        evaScoreMap.put("1", evaScoreJson.optString("1"));
        evaScoreMap.put("2", evaScoreJson.optString("2"));
        evaScoreMap.put("3", evaScoreJson.optString("3"));
        evaluateOptionEntity.setEvaluateScore(evaScoreMap);

        JSONObject evaMainJson = data.optJSONObject("teacherEvaluOption");
        Map<String, List<String>> evaMainMap = new HashMap<>();
        evaMainMap.put("1", parseOption(evaMainJson, "1"));
        evaMainMap.put("2", parseOption(evaMainJson, "2"));
        evaMainMap.put("3", parseOption(evaMainJson, "3"));
        evaluateOptionEntity.setTeacherEvaluOption(evaMainMap);

        JSONObject evaTutorJson = data.optJSONObject("tutorEvaluOption");
        Map<String, List<String>> evaTutorMap = new HashMap<>();
        evaTutorMap.put("1", parseOption(evaTutorJson, "1"));
        evaTutorMap.put("2", parseOption(evaTutorJson, "2"));
        evaTutorMap.put("3", parseOption(evaTutorJson, "3"));
        evaluateOptionEntity.setTutorEvaluOption(evaTutorMap);

        return evaluateOptionEntity;
    }

    private EvaluateOptionEntity parseScienceExtLiveInfo(ResponseEntity responseEntity) {
        EvaluateOptionEntity evaluateOptionEntity = new EvaluateOptionEntity();

        JSONObject data = (JSONObject) responseEntity.getJsonObject();

        JSONObject evaScoreJson = data.optJSONObject("evaluateScore");
        Map<String, String> evaScoreMap = new HashMap<>();
        evaScoreMap.put("1", evaScoreJson.optString("1"));
        evaScoreMap.put("2", evaScoreJson.optString("2"));
        evaScoreMap.put("3", evaScoreJson.optString("3"));
        evaluateOptionEntity.setEvaluateScore(evaScoreMap);

        JSONObject evaMainJson = data.optJSONObject("evaluateTeacherContent");
        Map<String, List<String>> evaMainMap = new HashMap<>();
        evaMainMap.put("1", parseOption(evaMainJson, "1"));
        evaMainMap.put("2", parseOption(evaMainJson, "2"));
        evaMainMap.put("3", parseOption(evaMainJson, "3"));
        evaluateOptionEntity.setTeacherEvaluOption(evaMainMap);

        JSONObject evaTutorJson = data.optJSONObject("evaluateCounselorContent");
        Map<String, List<String>> evaTutorMap = new HashMap<>();
        evaTutorMap.put("1", parseOption(evaTutorJson, "1"));
        evaTutorMap.put("2", parseOption(evaTutorJson, "2"));
        evaTutorMap.put("3", parseOption(evaTutorJson, "3"));
        evaluateOptionEntity.setTutorEvaluOption(evaTutorMap);

        return evaluateOptionEntity;
    }

    private List<String> parseOption(JSONObject evaJson, String index) {
        List<String> evaList = new ArrayList<>();
        JSONArray evaMainArray1 = evaJson.optJSONArray(index);
        for (int i = 0; i < evaMainArray1.length(); i++) {
            try {
                evaList.add(evaMainArray1.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return evaList;
    }
}
