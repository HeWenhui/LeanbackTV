package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker;

import android.app.Activity;
import android.support.annotation.UiThread;
import android.widget.RelativeLayout;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.parentsmeeting.modules.livevideo.business.HalfBodySceneTransAnim;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.entity.SuperSpeakerRedPackageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.entity.UploadVideoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.widget.SuperSpeakerBridge;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.videochat.business.VPlayerListenerReg;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.Call;

public class SuperSpeakerBll extends LiveBaseBll implements NoticeAction, TopicAction, ISuperSpeakerContract.ICameraPresenter {

    public SuperSpeakerBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);

    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        super.initView(bottomContent, mIsLand);

//        Observable.
//                just(AppConfig.DEBUG).
//                delay(2, TimeUnit.SECONDS).
//                observeOn(AndroidSchedulers.mainThread()).
//                subscribe(new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean aBoolean) throws Exception {
//                        if (aBoolean) {
//                            logger.i("accept");
//                            mGetInfo.setId(String.valueOf(454400));
//                            courseWareId = String.valueOf(1);
//                            srcType = String.valueOf(40);
//                            performShowRecordCamera(10, 65);
//                        }
//                    }
//                });
//        if (AppConfig.DEBUG) {
//                bottomContent.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
////                        mGetInfo.setId(String.valueOf(454400));
//                        courseWareId = String.valueOf(1);
//                        srcType = String.valueOf(40);
//                        performShowRecordCamera(10, 65);
//                    }
//                }, 3000);
//            }
//        }
    }

    @Override
    public void onNotice(String sourceNick, String target, final JSONObject data, int type) {
        switch (type) {
            case XESCODE.SUPER_SPEAKER_TAKE_CAMERA: {
//                final int open = data.optInt("open");
                srcType = data.optString("srcType");
                courseWareId = data.optString("testId");
                final int recordVideoTotalTime = data.optInt("recordTime");
                final int answerTime = data.optInt("answerTime");
                logger.i("srcType:" + srcType +
                        " courseWareId:" + courseWareId +
                        " recordVideoTotalTime:" + recordVideoTotalTime +
                        " answerTime:" + answerTime);

//                        .observeOn()
//                Observable.
//                        create(new ObservableOnSubscribe<Boolean>() {
//                            @Override
//                            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
//                                e.onNext(data.optBoolean("open"));
//                                e.onComplete();
//                            }
//                        }).
                createObserValbeDelay(data.optBoolean("open")).
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean bol) throws Exception {
                                if (bol) {
                                    performShowRecordCamera(answerTime, recordVideoTotalTime);
                                } else {
                                    if (superSpeakerBridge != null) {
                                        superSpeakerBridge.timeUp();
                                    }
                                }
                            }
                        });

                break;
            }
            default: {
                break;
            }
        }
    }

    private HalfBodySceneTransAnim mTransAnim;

    @Override
    public void showAnima() {
        if (mTransAnim == null) {
            mTransAnim = new HalfBodySceneTransAnim(activity, mGetInfo);
        }
        mTransAnim.onModeChange(mGetInfo.getMode(), true);
    }

    /** 拍照时需要停止播放视频 */
    @Override
    public void stopLiveVideo() {
        VPlayerListenerReg reg = ProxUtil.getProxUtil().get(mContext, VPlayerListenerReg.class);
        if (reg != null) {
            logger.i("停止播放");
            reg.release();
        }
    }

    /** 成功时需要重新播放直播 */
    @Override
    public void startLiveVideo() {
        VPlayerListenerReg reg = ProxUtil.getProxUtil().get(mContext, VPlayerListenerReg.class);
        if (reg != null) {
            logger.i("开始播放");
            reg.playVideo();
        }
    }

    /**
     * http://wiki.xesv5.com/pages/viewpage.action?pageId=18552940
     *
     * @param liveTopic
     * @param jsonObject
     * @param modeChange 是否发生主/辅导 态切换
     */
    @Override
    public void onTopic(LiveTopic liveTopic, final JSONObject jsonObject, boolean modeChange) {
        JSONObject room_1 = jsonObject.optJSONObject("room_1");
        if (room_1 != null) {
            final JSONObject dataJson = room_1.optJSONObject("speechShow");
            if (dataJson != null) {
//            final int open = dataJson.optInt("open");
                courseWareId = dataJson.optString("testId");
                srcType = dataJson.optString("srcType");
                final int recordVideoTotalTime = dataJson.optInt("recordTime");
                final int answerTime = dataJson.optInt("answerTime");
//                Observable.
//                        create(new ObservableOnSubscribe<Boolean>() {
//                            @Override
//                            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
//                                e.onNext(dataJson.optBoolean("open"));
//                                e.onComplete();
//                            }
//                        }).
//                        map(new Function<Boolean, Boolean>() {
//                            @Override
//                            public Boolean apply(Boolean bol) throws Exception {
//                                //1打开试题并且点击提交按钮
//                                return bol && ShareDataManager.getInstance().getInt(
//                                        ShareDataConfig.SUPER_SPEAKER_UPLOAD_SP_KEY + "_" + mGetInfo.getId() + "_" + courseWareId,
//                                        0,
//                                        ShareDataManager.SHAREDATA_NOT_CLEAR) == 0;
//
//                            }
//                        }).
//                        doOnNext(new Consumer<Boolean>() {
//                            @Override
//                            public void accept(Boolean aBoolean) throws Exception {
//                                if (aBoolean) {
//                                    showAnima();
//                                }
//                            }
//                        }).
//                        delay(2, TimeUnit.SECONDS).
                createObserValbeDelay(dataJson.optBoolean("open")).
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean bool) throws Exception {
                                if (bool) {
                                    performShowRecordCamera(answerTime, recordVideoTotalTime);
                                }
                            }
                        });
//            if (open) {
//                performShowRecordCamera(answerTime, recordVideoTotalTime);
//            }
            }
        }
    }

    private Observable createObserVable(boolean is) {
        return Observable.
                just(is).
                map(new Function<Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean bol) throws Exception {
                        //1打开试题并且点击提交按钮
                        return bol
                                && ShareDataManager.getInstance().getInt(
                                ShareDataConfig.SUPER_SPEAKER_UPLOAD_SP_KEY + "_" + mGetInfo.getId() + "_" + courseWareId,
                                0,
                                ShareDataManager.SHAREDATA_NOT_CLEAR) == 0
                                &&
                                mGetInfo.getLiveTopic().getMode().equals(LiveTopic.MODE_CLASS);//主讲态


                    }
                }).
                observeOn(AndroidSchedulers.mainThread()).
                doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            showAnima();
                        }
                    }
                });
    }

    private Observable createObserValbeDelay(boolean is) {
        if (is) {
            return createObserVable(is).delay(2, TimeUnit.SECONDS);
        } else {
            return createObserVable(is);
        }

    }


    SuperSpeakerBridge superSpeakerBridge;

    /**
     * 表现录制视频
     */
    @UiThread
    private void performShowRecordCamera(int answerTime, int recordTime) {
//        AndroidAudioConverter.load(mContext, new ILoadCallback() {
//            @Override
//            public void onSuccess() {
//                // Great!
//            }
//
//            @Override
//            public void onFailure(Exception error) {
//                // FFmpeg is not supported by device
//                error.printStackTrace();
//            }
//        });
        try {
            if (superSpeakerBridge != null && superSpeakerBridge.containsView()) {
                return;
            }
            stopLiveVideo();
            UploadVideoEntity uploadVideoEntity = new UploadVideoEntity();
            uploadVideoEntity.setLiveId(mGetInfo.getId());
            uploadVideoEntity.setTestId(courseWareId);
            uploadVideoEntity.setSrcType(srcType);
            uploadVideoEntity.setStuCouId(mGetInfo.getStuCouId());
            uploadVideoEntity.setStuId(mGetInfo.getStuId());
            uploadVideoEntity.setIsPlayBack("1");
            uploadVideoEntity.setIsUpload("1");
            superSpeakerBridge = new SuperSpeakerBridge(mContext, this, mRootView, mGetInfo.getId(), courseWareId, 1, uploadVideoEntity);
            superSpeakerBridge.performShowRecordCamera(answerTime, recordTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int[] getNoticeFilter() {
        //学生点赞
        return new int[]{
                XESCODE.SUPER_SPEAKER_TAKE_CAMERA
        };
    }

    /** 互动题所属id */
    private String courseWareId;
    /** 互动题所属题目类型 */
    private String srcType;

    @Override
    public void sendSuperSpeakerCameraStatus() {
        getHttpManager().sendSuperSpeakerCameraStatus(
                mGetInfo.getId(),
                mGetInfo.getStuId(),
                courseWareId,
                new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        logger.i("摄像头状态上传成功");
                    }
                }

        );
    }

    /**
     * 提交视频
     * wiki地址 https://wiki.xesv5.com/pages/viewpage.action?pageId=18552940
     *
     * @param isForce 1：是 2：否
     */
    @Override
    public void submitSpeechShow(String isForce, String videoDuration) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("from", mGetInfo.getStuId());
            jsonObject.put("name", mGetInfo.getStuName());
            jsonObject.put("type", String.valueOf(XESCODE.SUPER_SPEAKER_SEND_MESSAGE));
            jsonObject.put("id", mGetInfo.getStuId());
            jsonObject.put("time", videoDuration);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendNoticeToMain(jsonObject);

        getHttpManager().sendSuperSpeakersubmitSpeech(
                mGetInfo.getId(),
                mGetInfo.getStuCouId(),
                mGetInfo.getStuId(),
                "1",
                courseWareId,
                srcType,
                isForce,
                videoDuration,
                new HttpCallBack() {
                    @Override
                    public void onPmSuccess(final ResponseEntity responseEntity) throws Exception {
                        logger.i("提交接口成功");
                        Observable.create(new ObservableOnSubscribe<SuperSpeakerRedPackageEntity>() {
                            @Override
                            public void subscribe(ObservableEmitter<SuperSpeakerRedPackageEntity> e) throws Exception {
                                e.onNext(getHttpResponseParser().parseSuperSpeakerSubmitEntity(responseEntity));
                                e.onComplete();
                            }
                        }).delay(2, TimeUnit.SECONDS).
                                observeOn(AndroidSchedulers.mainThread()).
                                subscribe(new Consumer<SuperSpeakerRedPackageEntity>() {
                                    @Override
                                    public void accept(SuperSpeakerRedPackageEntity superSpeakerRedPackageEntity) throws Exception {
                                        if (superSpeakerBridge != null) {
                                            superSpeakerBridge.updateNum(superSpeakerRedPackageEntity.getMoney());
                                        }
                                    }
                                });
//                        SuperSpeakerRedPackageEntity entity = ;

                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);
                        logger.i(e);
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
//                        logger.i(responseEntity);
                    }
                });
    }

    @Override
    public void stopRecord() {
    }

    /***
     *  视频上传 成功的回调，已经放在{@link UploadVideoService}中进行上传
     * @param videoUrl
     * @param audioUrl
     * @param averVocieDecibel
     */
    @Deprecated
    @Override
    public void uploadSucess(String videoUrl, String audioUrl, String averVocieDecibel) {
        getHttpManager().uploadSpeechShow(
                mGetInfo.getId(),
                mGetInfo.getStuCouId(),
                mGetInfo.getStuId(),
                "1",
                courseWareId,
                srcType,
                videoUrl,
                audioUrl,
                "1",
                averVocieDecibel,
                new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        logger.i("upload success");
//                        XESToastUtils.showToast(mContext, "通知接口成功");
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                        logger.i("upload pmError " + responseEntity.getErrorMsg());
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        super.onPmFailure(error, msg);
                        logger.i("upload pmfail " + msg);
                    }
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        if (superSpeakerBridge != null) {
            superSpeakerBridge.resumeVideo();
        }
    }

    @Override
    public void onModeChange(String oldMode, final String mode, boolean isPresent) {
        super.onModeChange(oldMode, mode, isPresent);

        Observable.
                just(superSpeakerBridge != null && !LiveTopic.MODE_CLASS.equals(mode)).
                doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            superSpeakerBridge.stopRecordVideo();
                        }
                    }
                }).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            superSpeakerBridge.removeView();
                            startLiveVideo();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        logger.e(throwable);
                    }
                });
//    }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (superSpeakerBridge != null) {
            superSpeakerBridge.pauseVideo();
        }
    }
}
