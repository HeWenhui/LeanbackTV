//package com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.http;
//
//import com.xueersi.common.base.AbstractBusinessDataCallBack;
//import com.xueersi.common.http.HttpCallBack;
//import com.xueersi.common.http.HttpRequestParams;
//import com.xueersi.common.http.ResponseEntity;
//import com.xueersi.lib.log.LoggerFactory;
//import com.xueersi.lib.log.logger.Logger;
//import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
//import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoSAConfig;
//import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
//import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.config.LiveQueHttpConfig;
//import com.xueersi.parentsmeeting.modules.livevideo.question.entity.ChineseAISubjectResultEntity;
//import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.entity.NewCourseSec;
//import com.xueersi.parentsmeeting.modules.livevideoOldIJK.question.entity.PrimaryScienceAnswerResultEntity;
//
//public class CourseWareHttpManager {
//    private final Logger logger = LoggerFactory.getLogger("CourseWareHttpManager");
//    private LiveHttpManager liveHttpManager;
//    private CourseWareParse courseWareParse;
//    private int arts;
//
//    public CourseWareHttpManager(LiveHttpManager liveHttpManager) {
//        this.liveHttpManager = liveHttpManager;
//        courseWareParse = new CourseWareParse();
//        if (liveHttpManager.getLiveVideoSAConfig() != null) {
//            arts = liveHttpManager.getLiveVideoSAConfig().getArts();
//        }
//    }
//
//    public void submitCourseWareTests(String stuId, String packageId, String packageSource, String packageAttr, String releasedPageInfos, int isPlayBack, String classId,
//                                      String classTestId, String srcTypes, String testIds, String educationStage, String nonce, String testInfos, int isforce, long entranceTime, final AbstractBusinessDataCallBack callBack) {
//        HttpRequestParams httpRequestParams = new HttpRequestParams();
//        liveHttpManager.setDefaultParameter(httpRequestParams);
//        httpRequestParams.addBodyParam("stuId", stuId);
//        httpRequestParams.addBodyParam("packageId", packageId);
//        httpRequestParams.addBodyParam("stuId", stuId);
//        httpRequestParams.addBodyParam("packageId", packageId);
//        httpRequestParams.addBodyParam("packageSource", packageSource);
//        httpRequestParams.addBodyParam("packageAttr", packageAttr);
//        httpRequestParams.addBodyParam("releasedPageInfos", releasedPageInfos);
//        httpRequestParams.addBodyParam("isPlayBack", "" + isPlayBack);
//        httpRequestParams.addBodyParam("classId", "" + classId);
//        httpRequestParams.addBodyParam("classTestId", "" + classTestId);
//        httpRequestParams.addBodyParam("srcTypes", "" + srcTypes);
//        httpRequestParams.addBodyParam("testIds", "" + testIds);
//        httpRequestParams.addBodyParam("educationStage", "" + educationStage);
//        httpRequestParams.addBodyParam("nonce", "" + nonce);
//        httpRequestParams.addBodyParam("testInfos", "" + testInfos);
//        httpRequestParams.addBodyParam("entranceTime", "" + entranceTime);
//        httpRequestParams.addBodyParam("isForce", "" + isforce);
//        String url;
//        if (arts == LiveVideoSAConfig.ART_SEC) {
//            url = LiveQueHttpConfig.LIVE_SUBMIT_COURSEWARE;
//        } else {
//            url = LiveQueHttpConfig.LIVE_SUBMIT_COURSEWARE_CN;
//        }
//        liveHttpManager.sendPost(url, httpRequestParams, new HttpCallBack() {
//            @Override
//            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
//                logger.d("submitCourseWareTests:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
//                callBack.onDataSucess(responseEntity.getJsonObject());
//            }
//
//            @Override
//            public void onPmError(ResponseEntity responseEntity) {
//                logger.d("submitCourseWareTests:onPmError:responseEntity=" + responseEntity.getErrorMsg());
//                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
//            }
//
//            @Override
//            public void onPmFailure(Throwable error, String msg) {
//                logger.d("submitCourseWareTests:onPmFailure:responseEntity=" + msg, error);
//                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
//            }
//        });
//    }
//
//    public void getCourseWareTests(String stuId, String packageId, String packageSource, String packageAttr, String releasedPageInfos, int isPlayBack, String classId, String classTestId,
//                                   String srcTypes, String testIds, String educationStage, String nonce, String isShowTeamPk, final AbstractBusinessDataCallBack callBack) {
//        HttpRequestParams httpRequestParams = new HttpRequestParams();
//        liveHttpManager.setDefaultParameter(httpRequestParams);
//        httpRequestParams.addBodyParam("stuId", stuId);
//        httpRequestParams.addBodyParam("packageId", packageId);
//        httpRequestParams.addBodyParam("packageSource", packageSource);
//        httpRequestParams.addBodyParam("packageAttr", packageAttr);
//        httpRequestParams.addBodyParam("releasedPageInfos", releasedPageInfos);
//        httpRequestParams.addBodyParam("isPlayBack", "" + isPlayBack);
//        httpRequestParams.addBodyParam("classId", "" + classId);
//        httpRequestParams.addBodyParam("classTestId", "" + classTestId);
//        httpRequestParams.addBodyParam("srcTypes", "" + srcTypes);
//        httpRequestParams.addBodyParam("testIds", "" + testIds);
//        httpRequestParams.addBodyParam("educationStage", "" + educationStage);
//        httpRequestParams.addBodyParam("nonce", "" + nonce);
//        httpRequestParams.addBodyParam("isShowTeamPk", "" + isShowTeamPk);
//        String url;
//        if (arts == LiveVideoSAConfig.ART_SEC) {
//            url = LiveQueHttpConfig.LIVE_GET_COURSEWARE_TESTS;
//        } else {
//            url = LiveQueHttpConfig.LIVE_GET_COURSEWARE_TESTS_CN;
//        }
//        liveHttpManager.sendPost(url, httpRequestParams, new HttpCallBack(false) {
//            @Override
//            public void onPmSuccess(ResponseEntity responseEntity) {
//                logger.d("getCourseWareTests:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
//                NewCourseSec newCourseSec = courseWareParse.parseSec(responseEntity);
//                if (newCourseSec != null) {
//                    callBack.onDataSucess(newCourseSec);
//                } else {
//                    callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_NULL, "null");
//                }
//            }
//
//            @Override
//            public void onPmError(ResponseEntity responseEntity) {
//                logger.d("getCourseWareTests:onPmError:responseEntity=" + responseEntity.getErrorMsg());
//                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
//            }
//
//            @Override
//            public void onPmFailure(Throwable error, String msg) {
//                logger.d("getCourseWareTests:onPmFailure:responseEntity=" + msg, error);
//                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
//            }
//        });
//    }
//
//    /**
//     * Created by ZhangYuansun on 2019/3/7
//     * <p>
//     * 请求学生作答情况列表
//     */
//    public void getStuTestResult(String liveId, String stuId, String srcTypes, String testIds, String classTestId, String packageId, String packageAttr, int isPlayBack,
//                                 final AbstractBusinessDataCallBack callBack) {
//        HttpRequestParams httpRequestParams = new HttpRequestParams();
//        liveHttpManager.setDefaultParameter(httpRequestParams);
//        httpRequestParams.addBodyParam("liveId", liveId);
//        httpRequestParams.addBodyParam("stuId", stuId);
//        httpRequestParams.addBodyParam("srcTypes", srcTypes);
//        httpRequestParams.addBodyParam("testIds", "" + testIds);
//        httpRequestParams.addBodyParam("classTestId", "" + classTestId);
//        httpRequestParams.addBodyParam("packageId", "" + packageId);
//        httpRequestParams.addBodyParam("packageAttr", "" + packageAttr);
//        httpRequestParams.addBodyParam("isPlayBack", "" + isPlayBack);
//        HttpCallBack httpCallBack = new HttpCallBack(false) {
//            @Override
//            public void onPmSuccess(ResponseEntity responseEntity) {
//                logger.d("getStuTestResult:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
//                PrimaryScienceAnswerResultEntity primaryScienceAnswerResultEntity = courseWareParse.parseStuTestResult(responseEntity);
//                if (primaryScienceAnswerResultEntity != null) {
//                    callBack.onDataSucess(primaryScienceAnswerResultEntity);
//                } else {
//                    callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_NULL, "null");
//                }
//            }
//
//            @Override
//            public void onPmError(ResponseEntity responseEntity) {
//                logger.d("getStuTestResult:onPmError:responseEntity=" + responseEntity.getErrorMsg());
//                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
//            }
//
//            @Override
//            public void onPmFailure(Throwable error, String msg) {
//                logger.d("getStuTestResult:onPmFailure:responseEntity=" + msg, error);
//                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
//            }
//        };
//        String url;
//        if (arts == LiveVideoSAConfig.ART_SEC) {
//            url = LiveQueHttpConfig.LIVE_GET_STU_TESTS_RESULT;
//            liveHttpManager.sendPost(url, httpRequestParams, httpCallBack);
//        } else {
//            url = LiveQueHttpConfig.LIVE_GET_STU_TESTS_RESULT_CN;
//            liveHttpManager.sendGet(url, httpRequestParams, httpCallBack);
//        }
//    }
//
//    //语文主观题
//    public void getStuChiAITestResult(String liveId, String stuId, String srcTypes, String testIds, String classTestId, String packageId, String packageAttr, int isPlayBack,
//                                      String classId,final AbstractBusinessDataCallBack callBack) {
//        HttpRequestParams httpRequestParams = new HttpRequestParams();
//        liveHttpManager.setDefaultParameter(httpRequestParams);
//        httpRequestParams.addBodyParam("liveId", liveId);
//        httpRequestParams.addBodyParam("stuId", stuId);
//        httpRequestParams.addBodyParam("srcTypes", srcTypes);
//        httpRequestParams.addBodyParam("testIds", "" + testIds);
//        httpRequestParams.addBodyParam("classTestId", "" + classTestId);
//        httpRequestParams.addBodyParam("packageId", "" + packageId);
//        httpRequestParams.addBodyParam("packageAttr", "" + packageAttr);
//        httpRequestParams.addBodyParam("isPlayBack", "" + isPlayBack);
//        httpRequestParams.addBodyParam("classId", "" + classId);
//        HttpCallBack httpCallBack = new HttpCallBack(false) {
//            @Override
//            public void onPmSuccess(ResponseEntity responseEntity) {
//                logger.d("getStuTestResult:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
//                ChineseAISubjectResultEntity chineseAISubjectResultEntity = courseWareParse.paresChiAIStuTestResult(responseEntity);
//                if (chineseAISubjectResultEntity != null) {
//                    callBack.onDataSucess(chineseAISubjectResultEntity,responseEntity);
//                } else {
//                    callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_NULL, "null");
//                }
//            }
//
//            @Override
//            public void onPmError(ResponseEntity responseEntity) {
//                logger.d("getStuTestResult:onPmError:responseEntity=" + responseEntity.getErrorMsg());
//                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
//            }
//
//            @Override
//            public void onPmFailure(Throwable error, String msg) {
//                logger.d("getStuTestResult:onPmFailure:responseEntity=" + msg, error);
//                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
//            }
//        };
//        String url = LiveQueHttpConfig.LIVE_GET_STU_TESTS_RESULT_CN;
//        liveHttpManager.sendGet(url, httpRequestParams, httpCallBack);
//
//    }
//
//    public void getTestInfos(String testIds, final AbstractBusinessDataCallBack callBack) {
//        HttpRequestParams httpRequestParams = new HttpRequestParams();
//        liveHttpManager.setDefaultParameter(httpRequestParams);
//        httpRequestParams.addBodyParam("testIds", testIds);
//        String url = LiveQueHttpConfig.LIVE_GET_COURSEWARE_TESTS_EN;
//        liveHttpManager.sendPost(url, httpRequestParams, new HttpCallBack(false) {
//            @Override
//            public void onPmSuccess(ResponseEntity responseEntity) {
//                logger.d("getTestInfos:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
//                NewCourseSec newCourseSec = courseWareParse.parseEn(responseEntity);
//                if (newCourseSec != null) {
//                    callBack.onDataSucess(newCourseSec);
//                } else {
//                    callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_NULL, "null");
//                }
//            }
//
//            @Override
//            public void onPmError(ResponseEntity responseEntity) {
//                logger.d("getTestInfos:onPmError:responseEntity=" + responseEntity.getErrorMsg());
//                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
//            }
//
//            @Override
//            public void onPmFailure(Throwable error, String msg) {
//                logger.d("getTestInfos:onPmFailure:responseEntity=" + msg, error);
//                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
//            }
//        });
//    }
//
//    public void submitMultiTest(String answers, int isPlayBack, int isForce, final AbstractBusinessDataCallBack callBack) {
//        HttpRequestParams httpRequestParams = new HttpRequestParams();
//        liveHttpManager.setDefaultParameter(httpRequestParams);
//        httpRequestParams.addBodyParam("answers", answers);
//        httpRequestParams.addBodyParam("isPlayBack", "" + isPlayBack);
//        httpRequestParams.addBodyParam("isForce", "" + isForce);
//        liveHttpManager.sendPost(LiveQueHttpConfig.LIVE_SUBMIT_COURSEWARE_EN, httpRequestParams, new HttpCallBack(false) {
//            @Override
//            public void onPmSuccess(ResponseEntity responseEntity) {
//                logger.d("getTestInfos:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
//                callBack.onDataSucess(responseEntity.getJsonObject());
//            }
//
//            @Override
//            public void onPmError(ResponseEntity responseEntity) {
//                logger.d("getTestInfos:onPmError:responseEntity=" + responseEntity.getErrorMsg());
//                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
//            }
//
//            @Override
//            public void onPmFailure(Throwable error, String msg) {
//                logger.d("getTestInfos:onPmFailure:responseEntity=" + msg, error);
//                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
//            }
//        });
//    }
//
//    public void submitH5(String testAnswer, int testNum, String testId, String type, String stu_id, int isPlayBack, int isSubmit, final AbstractBusinessDataCallBack callBack) {
//        HttpRequestParams httpRequestParams = new HttpRequestParams();
//        liveHttpManager.setDefaultParameter(httpRequestParams);
//        httpRequestParams.addBodyParam("testAnswer", testAnswer);
//        httpRequestParams.addBodyParam("testNum", "" + testNum);
//        httpRequestParams.addBodyParam("testId", "" + testId);
//        httpRequestParams.addBodyParam("type", "" + type);
//        httpRequestParams.addBodyParam("isPlayBack", "" + isPlayBack);
//        httpRequestParams.addBodyParam("isSubmit", "" + isSubmit);
//        httpRequestParams.addBodyParam("stuId", "" + stu_id);
//        liveHttpManager.sendPost(LiveQueHttpConfig.LIVE_SUBMIT_COURSEWARE_VOICE_EN, httpRequestParams, new HttpCallBack(false) {
//            @Override
//            public void onPmSuccess(ResponseEntity responseEntity) {
//                logger.d("submitH5:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
//                callBack.onDataSucess(responseEntity.getJsonObject());
//            }
//
//            @Override
//            public void onPmError(ResponseEntity responseEntity) {
//                logger.d("submitH5:onPmError:responseEntity=" + responseEntity.getErrorMsg());
//                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
//            }
//
//            @Override
//            public void onPmFailure(Throwable error, String msg) {
//                logger.d("getTestInfos:onPmFailure:responseEntity=" + msg, error);
//                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
//            }
//        });
//    }
//}
