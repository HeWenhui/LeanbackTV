package com.xueersi.parentsmeeting.modules.livevideo.question.http;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.HttpRequestParams;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueHttpConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.NewCourseSec;

public class CourseWareHttpManager {
    private final Logger logger = LoggerFactory.getLogger("CourseWareHttpManager");
    private LiveHttpManager liveHttpManager;
    private CourseWareParse courseWareParse;

    public CourseWareHttpManager(LiveHttpManager liveHttpManager) {
        this.liveHttpManager = liveHttpManager;
        courseWareParse = new CourseWareParse();
    }

    public void getCourseWareTests(String stuId, String packageId, String packageSource, String packageAttr, String releasedPageInfos, int isPlayBack, String classId, String classTestId,
                                   String srcTypes, String testIds, String educationStage, String nonce, final AbstractBusinessDataCallBack callBack) {
        HttpRequestParams httpRequestParams = new HttpRequestParams();
        liveHttpManager.setDefaultParameter(httpRequestParams);
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
        liveHttpManager.sendPost(LiveQueHttpConfig.LIVE_GET_COURSEWARE_TESTS, httpRequestParams, new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.d("getCourseWareTests:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
                NewCourseSec newCourseSec = courseWareParse.parse(responseEntity);
                if (newCourseSec != null) {
                    callBack.onDataSucess(newCourseSec);
                } else {
                    callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_NULL, "null");
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.d("getCourseWareTests:onPmError:responseEntity=" + responseEntity.getErrorMsg());
                super.onPmError(responseEntity);
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_ERROR, responseEntity.getErrorMsg());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                logger.d("getCourseWareTests:onPmFailure:responseEntity=" + msg, error);
                super.onPmFailure(error, msg);
                callBack.onDataFail(LiveHttpConfig.HTTP_ERROR_FAIL, msg);
            }
        });
    }
}
