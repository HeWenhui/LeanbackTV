package com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive;

import android.text.TextUtils;

import com.xueersi.common.config.AppConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;

import org.json.JSONObject;

public class EvenDriveAnimDataSource implements TasksDataSource {
    private LiveHttpManager liveHttpManager;
    private LiveGetInfo getInfo;
//    private TasksDataSource tasksDataSource;

    private Logger logger = LoggerFactory.getLogger("EvenDriveAnimDataSource");

    public EvenDriveAnimDataSource(LiveHttpManager liveHttpManager, LiveGetInfo liveGetInfo) {
        this.liveHttpManager = liveHttpManager;
        this.getInfo = liveGetInfo;
    }

    @Override
    public void getDataSource(EvenDriveAnimRepository.EvenDriveQuestionType question_type, String testId, final LoadAnimCallBack callBack) {

        if (question_type == EvenDriveAnimRepository.EvenDriveQuestionType.QUES_TYPE_ENGLISH_NEW_PLATFORM) {
            liveHttpManager.getEnglishEvenDriveNum(null, getInfo.getId(),
                    getInfo.getStuCouId(),
                    getInfo.getStudentLiveInfo().getClassId(),
                    getInfo.getStudentLiveInfo().getTeamId(),
                    new HttpCallBack() {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            parseEvenDriveNum(responseEntity, callBack);
                        }

                        @Override
                        public void onPmError(ResponseEntity responseEntity) {
                            super.onPmError(responseEntity);
                        }

                        @Override
                        public void onPmFailure(Throwable error, String msg) {
                            super.onPmFailure(error, msg);
                        }
                    });
        } else if (question_type == EvenDriveAnimRepository.EvenDriveQuestionType.QUES_TYPE_CHS_SELF_UPLOAD) {
            liveHttpManager.getSelfUploadEvenDriveNum(getInfo.getIsArts(), getInfo.getStuCouId(),
                    getInfo.getId(),
                    getInfo.getStuId(),
                    testId,
                    new HttpCallBack() {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            parseEvenDriveNum(responseEntity, callBack);
                        }

                        @Override
                        public void onPmError(ResponseEntity responseEntity) {
                            super.onPmError(responseEntity);
                            if (callBack != null) {

                            }
                        }

                        @Override
                        public void onPmFailure(Throwable error, String msg) {
                            super.onPmFailure(error, msg);
                        }
                    });
        } else if (question_type == EvenDriveAnimRepository.EvenDriveQuestionType.QUES_TYPE_CHS_NEW_PLAYFROM) {
            liveHttpManager.getNewPlatformEvenDriveNum(
                    getInfo.getIsArts(),
                    getInfo.getStudentLiveInfo().getClassId(),
                    getInfo.getId(),
                    getInfo.getStudentLiveInfo().getTeamId(),
                    getInfo.getStuId(),
                    new HttpCallBack() {
                        @Override
                        public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                            parseEvenDriveNum(responseEntity, callBack);
                        }

                        @Override
                        public void onPmError(ResponseEntity responseEntity) {
                            super.onPmError(responseEntity);
                        }

                        @Override
                        public void onPmFailure(Throwable error, String msg) {
                            super.onPmFailure(error, msg);
                        }
                    });
        } else if (question_type == EvenDriveAnimRepository.EvenDriveQuestionType.INIT_EVEN_NUM) {
            if (EvenDriveUtils.isOpenStimulation(getInfo)) {
                if (getInfo.getIsArts() == 1) {
                    liveHttpManager.getEnglishEvenDriveNum(null, getInfo.getId(),
                            getInfo.getStuCouId(),
                            getInfo.getStudentLiveInfo().getClassId(),
                            getInfo.getStudentLiveInfo().getTeamId(),
                            new HttpCallBack() {
                                @Override
                                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                                    parseEvenDriveNum(responseEntity, callBack);
                                }

                                @Override
                                public void onPmError(ResponseEntity responseEntity) {
                                    super.onPmError(responseEntity);
                                }

                                @Override
                                public void onPmFailure(Throwable error, String msg) {
                                    super.onPmFailure(error, msg);
                                }
                            });
                } else {
                    liveHttpManager.getNewPlatformEvenDriveNum(
                            getInfo.getIsArts(),
                            getInfo.getStudentLiveInfo().getClassId(),
                            getInfo.getId(),
                            getInfo.getStudentLiveInfo().getTeamId(),
                            getInfo.getStuId(),
                            new HttpCallBack() {
                                @Override
                                public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                                    parseEvenDriveNum(responseEntity, callBack);
                                }

                                @Override
                                public void onPmError(ResponseEntity responseEntity) {
                                    super.onPmError(responseEntity);
                                }

                                @Override
                                public void onPmFailure(Throwable error, String msg) {
                                    super.onPmFailure(error, msg);
                                }
                            });
                }
            }
        }
    }

    private static int mNum = 2;
    public static boolean myTest = false;

    public static boolean getTest() {
        return myTest && AppConfig.DEBUG;
    }

    private void parseEvenDriveNum(ResponseEntity responseEntity, LoadAnimCallBack callBack) {
        JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
        String num = jsonObject.optString("num");
        if (TextUtils.isEmpty(num)) {
            num = "0";
        }
        LiveGetInfo.EvenDriveInfo evenDriveInfo = getInfo.getEvenDriveInfo();
        try {
            if (getTest()) {
                evenDriveInfo.setEvenNum(mNum);
            } else {
                evenDriveInfo.setEvenNum(Integer.valueOf(num));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.e(e.getMessage());
        }

        if (callBack != null) {
            if (getTest()) {
                callBack.onDatasLoaded("" + mNum);
            } else {
                callBack.onDatasLoaded(num);
            }
        }
        if (getTest()) {
            if (mNum != 10) {
                mNum++;
            } else {
                mNum = 24;
            }
        }
    }

}
