package com.xueersi.parentsmeeting.modules.livevideo.question.http;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.framework.utils.string.StringUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.groupgame.entity.GroupGameTestInfosEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpAction;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.ExperLiveQueHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.BigResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.ChineseAISubjectResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.NewCourseSec;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.PrimaryScienceAnswerResultEntity;

import org.json.JSONArray;
import org.json.JSONObject;

public class ExperCourseWareHttpManager {
    private final Logger logger = LoggerFactory.getLogger("CourseWareHttpManager");
    private LiveHttpAction liveHttpManager;
    private CourseWareParse courseWareParse;
    private int arts;

    public ExperCourseWareHttpManager(LiveHttpManager liveHttpManager) {
        this.liveHttpManager = liveHttpManager;
        courseWareParse = new CourseWareParse();
        if (liveHttpManager.getLiveVideoSAConfig() != null) {
            arts = liveHttpManager.getLiveVideoSAConfig().getArts();
        }
    }

    public void submitCourseWareTests(VideoQuestionLiveEntity detailInfo, String stuId, String packageId, String packageSource, String packageAttr, String releasedPageInfos, int isPlayBack, String classId,
                                      String classTestId, String srcTypes, String testIds, String educationStage, String nonce, String testInfos, int isforce, long entranceTime, final AbstractBusinessDataCallBack callBack) {
        HttpRequestParams httpRequestParams = new HttpRequestParams();
        httpRequestParams.addBodyParam("stuId", stuId);
        httpRequestParams.addBodyParam("packageId", packageId);
        httpRequestParams.addBodyParam("stuId", stuId);
        httpRequestParams.addBodyParam("packageId", packageId);
        httpRequestParams.addBodyParam("packageSource", packageSource);
        httpRequestParams.addBodyParam("packageAttr", packageAttr);
        httpRequestParams.addBodyParam("releasedPageInfos", releasedPageInfos);
        httpRequestParams.addBodyParam("isPlayBack", "" + isPlayBack);
        httpRequestParams.addBodyParam("classId", "" + classId);
        httpRequestParams.addBodyParam("classTestId", "" + classTestId);
        httpRequestParams.addBodyParam("srcTypes", "" + srcTypes);
        httpRequestParams.addBodyParam("testIds", "" + testIds);
        httpRequestParams.addBodyParam("educationStage", "" + educationStage);
        httpRequestParams.addBodyParam("nonce", "" + nonce);
        httpRequestParams.addBodyParam("testInfos", "" + testInfos);
        httpRequestParams.addBodyParam("entranceTime", "" + entranceTime);
        httpRequestParams.addBodyParam("isForce", "" + isforce);
        String url;
        if (detailInfo.isTUtor()) {
//            httpRequestParams.addBodyParam("stuCouId", "9649079");
//            httpRequestParams.addBodyParam("stuId", "58074");
//            httpRequestParams.addBodyParam("packageId", "59148");
//            httpRequestParams.addBodyParam("packageSource", "2");
//            httpRequestParams.addBodyParam("liveId", "376269");
//            httpRequestParams.addBodyParam("packageAttr", "1");
//            httpRequestParams.addBodyParam("releasedPageInfos", "[{\"72853\":[\"21\",\"20188\"]}]");
            url = LiveQueHttpConfig.LIVE_GET_COURSEWARE_SUBMIT_TESTS;
        } else {
            url = ExperLiveQueHttpConfig.LIVE_SUBMIT_COURSEWARE;
        }
        liveHttpManager.sendPostDefault(url, httpRequestParams, new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.d("submitCourseWareTests:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
                callBack.onDataSucess(responseEntity.getJsonObject());
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.d("submitCourseWareTests:onPmError:responseEntity=" + responseEntity.getErrorMsg());
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                logger.d("submitCourseWareTests:onPmFailure:responseEntity=" + msg, error);
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
            }
        });
    }

    public void getCourseWareTests(VideoQuestionLiveEntity info, String stuId, String packageId, String packageSource, String packageAttr, String releasedPageInfos, int isPlayBack, String classId, String classTestId,
                                   String srcTypes, String testIds, String educationStage, String nonce, String isShowTeamPk, final AbstractBusinessDataCallBack callBack) {
        HttpRequestParams httpRequestParams = new HttpRequestParams();
        httpRequestParams.addBodyParam("stuId", stuId);
        httpRequestParams.addBodyParam("packageId", packageId);
        httpRequestParams.addBodyParam("packageSource", packageSource);
        httpRequestParams.addBodyParam("packageAttr", packageAttr);
        httpRequestParams.addBodyParam("releasedPageInfos", releasedPageInfos);
        httpRequestParams.addBodyParam("isPlayBack", "" + isPlayBack);
        httpRequestParams.addBodyParam("classId", "" + classId);
        httpRequestParams.addBodyParam("classTestId", "" + classTestId);
        httpRequestParams.addBodyParam("srcTypes", "" + srcTypes);
        httpRequestParams.addBodyParam("testIds", "" + testIds);
        httpRequestParams.addBodyParam("educationStage", "" + educationStage);
        httpRequestParams.addBodyParam("nonce", "" + nonce);
        httpRequestParams.addBodyParam("isShowTeamPk", "" + isShowTeamPk);
        String url;
        if (info.isTUtor()) {
//            httpRequestParams.addBodyParam("stuCouId","9649079");
//            httpRequestParams.addBodyParam("stuId", "58074");
//            httpRequestParams.addBodyParam("packageId", "59148");
//            httpRequestParams.addBodyParam("packageSource", "2");
//            httpRequestParams.addBodyParam("liveId", "376269");
//            httpRequestParams.addBodyParam("packageAttr", "1");
//            httpRequestParams.addBodyParam("releasedPageInfos", "[{\"72853\":[\"21\",\"20188\"]}]");
            url = LiveQueHttpConfig.LIVE_GET_COURSEWARE_TUTOR_TESTS;
        } else if (arts == LiveVideoSAConfig.ART_SEC || arts == LiveVideoSAConfig.ART_CH) {
            url = ExperLiveQueHttpConfig.LIVE_GET_COURSEWARE_TESTS;
        } else {
            url = LiveQueHttpConfig.LIVE_GET_COURSEWARE_TESTS_CN;
        }
        liveHttpManager.sendPostDefault(url, httpRequestParams, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.d("getCourseWareTests:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
                NewCourseSec newCourseSec = courseWareParse.parseSec(responseEntity);
                if (newCourseSec != null) {
                    callBack.onDataSucess(newCourseSec);
                } else {
                    callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_NULL, "null");
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.d("getCourseWareTests:onPmError:responseEntity=" + responseEntity.getErrorMsg());
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                logger.d("getCourseWareTests:onPmFailure:responseEntity=" + msg, error);
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
            }
        });
    }

    /**
     * Created by ZhangYuansun on 2019/3/7
     * <p>
     * 请求学生作答情况列表
     */
    public void getStuTestResult(String liveId, String stuId, String srcTypes, String testIds, String classTestId, String packageId, String packageAttr, String packageSource, int isPlayBack,
                                 final AbstractBusinessDataCallBack callBack, boolean isTutor) {
        HttpRequestParams httpRequestParams = new HttpRequestParams();
        httpRequestParams.addBodyParam("liveId", liveId);
        httpRequestParams.addBodyParam("stuId", stuId);
        httpRequestParams.addBodyParam("srcTypes", srcTypes);
        httpRequestParams.addBodyParam("testIds", "" + testIds);
        httpRequestParams.addBodyParam("classTestId", "" + classTestId);
        httpRequestParams.addBodyParam("packageId", "" + packageId);
        httpRequestParams.addBodyParam("packageAttr", "" + packageAttr);
        httpRequestParams.addBodyParam("packageSource", packageSource);
        httpRequestParams.addBodyParam("isPlayBack", "0");
        HttpCallBack httpCallBack = new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.d("getStuTestResult:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
                PrimaryScienceAnswerResultEntity primaryScienceAnswerResultEntity = courseWareParse.parseStuTestResult(responseEntity);
                if (primaryScienceAnswerResultEntity != null) {
                    callBack.onDataSucess(primaryScienceAnswerResultEntity);
                } else {
                    callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_NULL, "null");
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.d("getStuTestResult:onPmError:responseEntity=" + responseEntity.getErrorMsg());
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                logger.d("getStuTestResult:onPmFailure:responseEntity=" + msg, error);
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
            }
        };
        String url = ExperLiveQueHttpConfig.LIVE_GET_STU_TESTS_RESULT;
        liveHttpManager.sendPostDefault(url, httpRequestParams, httpCallBack);
    }

    //语文主观题
    public void getStuChiAITestResult(String liveId, String stuId, String srcTypes, String testIds, String classTestId, String packageId, String packageAttr, int isPlayBack,
                                      String classId, final AbstractBusinessDataCallBack callBack) {
        HttpRequestParams httpRequestParams = new HttpRequestParams();
        httpRequestParams.addBodyParam("liveId", liveId);
        httpRequestParams.addBodyParam("stuId", stuId);
        httpRequestParams.addBodyParam("srcTypes", srcTypes);
        httpRequestParams.addBodyParam("testIds", "" + testIds);
        httpRequestParams.addBodyParam("classTestId", "" + classTestId);
        httpRequestParams.addBodyParam("packageId", "" + packageId);
        httpRequestParams.addBodyParam("packageAttr", "" + packageAttr);
        httpRequestParams.addBodyParam("isPlayBack", "" + isPlayBack);
        httpRequestParams.addBodyParam("classId", "" + classId);
        HttpCallBack httpCallBack = new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.d("getStuTestResult:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
                ChineseAISubjectResultEntity chineseAISubjectResultEntity = courseWareParse.paresChiAIStuTestResult(responseEntity);
                if (chineseAISubjectResultEntity != null) {
                    callBack.onDataSucess(chineseAISubjectResultEntity, responseEntity);
                } else {
                    callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_NULL, "null");
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.d("getStuTestResult:onPmError:responseEntity=" + responseEntity.getErrorMsg());
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                logger.d("getStuTestResult:onPmFailure:responseEntity=" + msg, error);
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
            }
        };
        String url = LiveQueHttpConfig.LIVE_GET_STU_TESTS_RESULT_CN;
        liveHttpManager.sendPostDefault(url, httpRequestParams, httpCallBack);

    }

    public void getTestInfos(String stuId, String testIds, final AbstractBusinessDataCallBack callBack) {
        HttpRequestParams httpRequestParams = new HttpRequestParams();
        httpRequestParams.addBodyParam("testIds", testIds);
        httpRequestParams.addBodyParam("stuId", stuId);
        String url = ExperLiveQueHttpConfig.LIVE_GET_COURSEWARE_TESTS_EN;
        liveHttpManager.sendPostDefault(url, httpRequestParams, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.d("getTestInfos:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
                NewCourseSec newCourseSec = courseWareParse.parseEn(responseEntity);
                if (newCourseSec != null) {
                    callBack.onDataSucess(newCourseSec);
                } else {
                    callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_NULL, "null");
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.d("getTestInfos:onPmError:responseEntity=" + responseEntity.getErrorMsg());
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                logger.d("getTestInfos:onPmFailure:responseEntity=" + msg, error);
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
            }
        });
    }

    public void submitMultiTest(String answers, int isPlayBack, int isForce, final AbstractBusinessDataCallBack callBack) {
        HttpRequestParams httpRequestParams = new HttpRequestParams();
        httpRequestParams.addBodyParam("answers", answers);
        httpRequestParams.addBodyParam("isPlayBack", "" + isPlayBack);
        httpRequestParams.addBodyParam("isForce", "" + isForce);
        liveHttpManager.sendPostDefault(ExperLiveQueHttpConfig.LIVE_SUBMIT_COURSEWARE_EN, httpRequestParams, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.d("getTestInfos:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
                callBack.onDataSucess(responseEntity.getJsonObject());
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.d("getTestInfos:onPmError:responseEntity=" + responseEntity.getErrorMsg());
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                logger.d("getTestInfos:onPmFailure:responseEntity=" + msg, error);
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
            }
        });
    }

    public void submitH5(String testAnswer, int testNum, String testId, String type, String stu_id, int isPlayBack, int isSubmit, final AbstractBusinessDataCallBack callBack) {
        HttpRequestParams httpRequestParams = new HttpRequestParams();
        httpRequestParams.addBodyParam("testAnswer", testAnswer);
        httpRequestParams.addBodyParam("testNum", "" + testNum);
        httpRequestParams.addBodyParam("testId", "" + testId);
        httpRequestParams.addBodyParam("type", "" + type);
        httpRequestParams.addBodyParam("isPlayBack", "" + isPlayBack);
        httpRequestParams.addBodyParam("isSubmit", "" + isSubmit);
        httpRequestParams.addBodyParam("stuId", "" + stu_id);
        liveHttpManager.sendPostDefault(ExperLiveQueHttpConfig.LIVE_SUBMIT_COURSEWARE_H5_EN, httpRequestParams, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.d("submitH5:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
                callBack.onDataSucess(responseEntity.getJsonObject());
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.d("submitH5:onPmError:responseEntity=" + responseEntity.getErrorMsg());
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                logger.d("getTestInfos:onPmFailure:responseEntity=" + msg, error);
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
            }
        });
    }

    public void isSubmitH5Vote(final String userAnswer, final String testId, final String classId, final String stuId, final int isPlayBack, final int isforce, final AbstractBusinessDataCallBack callBack) {
        HttpRequestParams httpRequestParams = new HttpRequestParams();
        httpRequestParams.addBodyParam("testId", "" + testId);
        httpRequestParams.addBodyParam("stuId", "" + stuId);
        liveHttpManager.sendPostDefault(LiveQueHttpConfig.LIVE_IS_SUBMIT_COURSEWARE_VOTE, httpRequestParams, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.d("isSubmitH5Vote:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
                try {
                    JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                    Boolean isSubmit = jsonObject.optBoolean("isSubmit");
                    if (isSubmit) {
                        callBack.onDataSucess(responseEntity.getJsonObject(), true);
                    } else {
                        submitH5Vote(userAnswer, testId, classId, stuId, isPlayBack, isforce, callBack);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.d("isSubmitH5Vote:onPmError:responseEntity=" + responseEntity.getErrorMsg());
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                logger.d("isSubmitH5Vote:onPmFailure:responseEntity=" + msg, error);
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
            }
        });
    }

    public void submitH5Vote(String userAnswer, String testId, String classId, String stuId, int isPlayBack, final int isforce, final AbstractBusinessDataCallBack callBack) {
        try {
            JSONArray jsonArray = new JSONArray(userAnswer);
            if (jsonArray.length() > 0) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                userAnswer = jsonObject.optString("useranswer");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpRequestParams httpRequestParams = new HttpRequestParams();
        httpRequestParams.addBodyParam("userAnswer", userAnswer);
        httpRequestParams.addBodyParam("testId", "" + testId);
        if (isforce == 1) {
            httpRequestParams.addBodyParam("forceSubmit", "" + false);
        } else {
            httpRequestParams.addBodyParam("forceSubmit", "" + true);
        }
        httpRequestParams.addBodyParam("isPlayBack", "" + isPlayBack);
        httpRequestParams.addBodyParam("classId", "" + classId);
        httpRequestParams.addBodyParam("stuId", "" + stuId);
        liveHttpManager.sendPostDefault(LiveQueHttpConfig.LIVE_SUBMIT_COURSEWARE_VOTE, httpRequestParams, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.d("submitH5Vote:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
                callBack.onDataSucess(responseEntity.getJsonObject(), false);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.d("submitH5Vote:onPmError:responseEntity=" + responseEntity.getErrorMsg());
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                logger.d("submitH5Vote:onPmFailure:responseEntity=" + msg, error);
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
            }
        });
    }

    /**
     * 小组互动 - 拉题
     *
     * @param testIds
     * @param type
     * @param callBack
     */
    public void getGroupGameTestInfos(String testIds, String stuId, final String type, final AbstractBusinessDataCallBack callBack) {
        HttpRequestParams httpRequestParams = new HttpRequestParams();
        httpRequestParams.addBodyParam("testIds", testIds);
        httpRequestParams.addBodyParam("stuId", stuId);
        String url = LiveQueHttpConfig.LIVE_GET_COURSEWARE_TESTS_EN;
        liveHttpManager.sendPostDefault(url, httpRequestParams, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.d("getGroupGameTestInfos:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
                GroupGameTestInfosEntity entity;
                if (LiveQueConfig.EN_COURSE_TYPE_CLEANING_UP.equals(type)) {
                    entity = courseWareParse.parseCleanUpTestInfo(responseEntity);
                } else {
                    entity = courseWareParse.parseGroupGameTestInfo(responseEntity, type);
                }
                if (entity != null) {
                    callBack.onDataSucess(entity);
                } else {
                    callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_NULL, "null");
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.d("getGroupGameTestInfos:onPmError:responseEntity=" + responseEntity.getErrorMsg());
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                logger.d("getGroupGameTestInfos:onPmFailure:responseEntity=" + msg, error);
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
            }
        });
    }

    /**
     * 小组互动 - 答题
     */
    public void submitGroupGame(String classId, String testId, String type, int gameMode, int voiceTime, int isPlayBack, int pkTeamId, int gameGroupId,
                                int starNum, int energy, int gold, int videoLengthTime, int micLengthTime, int acceptVideoLengthTime, int acceptMicLengthTime,
                                String answerData, final AbstractBusinessDataCallBack callBack) {
        HttpRequestParams httpRequestParams = new HttpRequestParams();
        httpRequestParams.addBodyParam("classId", "" + classId);
        httpRequestParams.addBodyParam("testId", "" + testId);
        httpRequestParams.addBodyParam("type", type);
        httpRequestParams.addBodyParam("gameMode", "" + gameMode);
        httpRequestParams.addBodyParam("voiceTime", "" + voiceTime);
        httpRequestParams.addBodyParam("isPlayBack", "" + isPlayBack);
        httpRequestParams.addBodyParam("pkTeamId", "" + pkTeamId);
        httpRequestParams.addBodyParam("gameGroupId", "" + gameGroupId);
        httpRequestParams.addBodyParam("starNum", "" + starNum);
        httpRequestParams.addBodyParam("energy", "" + energy);
        httpRequestParams.addBodyParam("gold", "" + gold);
        httpRequestParams.addBodyParam("videoLengthTime", "" + videoLengthTime);
        httpRequestParams.addBodyParam("micLengthTime", "" + micLengthTime);
        httpRequestParams.addBodyParam("acceptVideoLengthTime", "" + acceptVideoLengthTime);
        httpRequestParams.addBodyParam("acceptMicLengthTime", "" + acceptMicLengthTime);
        httpRequestParams.addBodyParam("answerData", answerData);
        liveHttpManager.sendPostDefault(LiveQueHttpConfig.LIVE_SUBMIT_COURSEWARE_GROUPGAME_EN, httpRequestParams, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.d("submitGroupGame:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
                callBack.onDataSucess(responseEntity.getJsonObject());
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.d("submitGroupGame:onPmError:responseEntity=" + responseEntity.getErrorMsg());
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                logger.d("submitGroupGame:onPmFailure:responseEntity=" + msg, error);
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
            }
        });
    }

    public void submitBigTestInteraction(String stuId, String testId, String interactionId, JSONArray userAnswer, long startTime, int isForce, int isPlayBack, String srcType, final AbstractBusinessDataCallBack callBack) {
        HttpRequestParams httpRequestParams = new HttpRequestParams();
        httpRequestParams.addBodyParam("stuId", "" + stuId);
        httpRequestParams.addBodyParam("testId", "" + testId);
        httpRequestParams.addBodyParam("srcType", "" + srcType);
        httpRequestParams.addBodyParam("interactionId", "" + interactionId);
        httpRequestParams.addBodyParam("isForce", "" + isForce);
        httpRequestParams.addBodyParam("userAnswer", "" + userAnswer);
        httpRequestParams.addBodyParam("isPlayBack", "" + isPlayBack);
        httpRequestParams.addBodyParam("startTime", "" + startTime);
        liveHttpManager.sendPostDefault(liveHttpManager.getLiveVideoSAConfigInner().URL_LIVE_SUBMIT_BIG_TEST, httpRequestParams, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                callBack.onDataSucess(responseEntity.getJsonObject());
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
            }
        });
    }

    public void getStuInteractionResult(String stuId, String testId, String srcType, String interactionId, int isPlayBack, final AbstractBusinessDataCallBack callBack) {
        HttpRequestParams httpRequestParams = new HttpRequestParams();
        httpRequestParams.addBodyParam("stuId", "" + stuId);
        httpRequestParams.addBodyParam("testId", "" + testId);
        httpRequestParams.addBodyParam("srcType", "" + srcType);
        httpRequestParams.addBodyParam("interactionId", "" + interactionId);
        httpRequestParams.addBodyParam("isPlayBack", "" + isPlayBack);
        liveHttpManager.sendPostDefault(liveHttpManager.getLiveVideoSAConfigInner().URL_LIVE_GET_BIG_TEST_RESULT, httpRequestParams, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                BigResultEntity bigResultEntity = courseWareParse.parseBigResult(responseEntity);
                if (bigResultEntity == null) {
                    callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_NULL, "数据解析异常");
                } else {
                    callBack.onDataSucess(bigResultEntity, responseEntity.getJsonObject());
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
            }
        });
    }

    public void sumitCourseWareH5(String testId, String testResult, String testDay,
                                  String classId, String type, String isSubmit,
                                  double voiceTime, boolean isRight, HttpCallBack requestCallBack) {
        if (LiveQueConfig.EN_COURSE_TYPE_VOICE_BLANK.equals(type) || LiveQueConfig.EN_COURSE_TYPE_VOICE_CHOICE.equals(type)) {
            HttpRequestParams params = new HttpRequestParams();
            String url = ExperLiveQueHttpConfig.LIVE_SUBMIT_COURSEWARE_VOICE_EN;
            params.addBodyParam("testId", testId);
            params.addBodyParam("liveId", classId);
            params.addBodyParam("type", type);
            params.addBodyParam("isRight", isRight ? "1" : "0");
            params.addBodyParam("isPlayBack", "1");
            params.addBodyParam("isSubmit", isSubmit);
            params.addBodyParam("voiceUrl", "");
            params.addBodyParam("voiceTime", "" + voiceTime);
            params.addBodyParam("url", "");
            params.addBodyParam("imageUrl", "");
            params.addBodyParam("userAnswer", LiveVideoConfig.userAnswer);
            params.addBodyParam("answer", LiveVideoConfig.answer);
            liveHttpManager.sendPostDefault(url, params, requestCallBack);
        } else {
            //类型18 19
            HttpRequestParams params = new HttpRequestParams();
            String url = ExperLiveQueHttpConfig.LIVE_SUBMIT_COURSEWARE_EN;
            params.addBodyParam("liveId", classId);
            params.addBodyParam("answers", testResult);
            params.addBodyParam("isPlayBack", "2");
            params.addBodyParam("isForce", isSubmit);
            liveHttpManager.sendPostDefault(url, params, requestCallBack);
        }
    }

    public void sendSpeechEvalResultNewArts(String liveId, String id, String stuAnswer, String isSubmit, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", id);
        params.addBodyParam("isRejected", "");
        params.addBodyParam("isSubmit", "" + isSubmit);
        params.addBodyParam("answers", "" + stuAnswer);
        params.addBodyParam("type", "1");
        params.addBodyParam("isPlayBack", "0");
        liveHttpManager.sendPostDefault(ExperLiveQueHttpConfig.URL_LIVE_SEND_SPEECHEVALUATEARTS, params, requestCallBack);
    }

    //判断语音评测2期是否作答
    public void speechEval42IsAnswered(String liveId, String id, HttpCallBack requestCallBack) {
        HttpRequestParams params = new HttpRequestParams();
        params.addBodyParam("liveId", liveId);
        params.addBodyParam("testId", id);
        params.addBodyParam("type", "1");
        liveHttpManager.sendPostDefault(ExperLiveQueHttpConfig.URL_LIVE_SEND_SPEECHEVAL42_ANSWER, params, requestCallBack);
    }
}
