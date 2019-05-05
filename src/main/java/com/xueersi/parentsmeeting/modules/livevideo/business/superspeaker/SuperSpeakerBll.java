package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker;

import android.app.Activity;
import android.support.annotation.UiThread;
import android.widget.RelativeLayout;

import com.xueersi.common.config.AppConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.entity.SuperSpeakerRedPackageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.widget.SuperSpeakerBridge;
import com.xueersi.parentsmeeting.modules.livevideo.config.ShareDataConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.Call;

public class SuperSpeakerBll extends LiveBaseBll implements NoticeAction, TopicAction, ISuperSpeakerContract.ICameraPresenter {
//    ISuperSpeakerContract.ICameraView iView;

    public SuperSpeakerBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);

    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        super.initView(bottomContent, mIsLand);
        if (AppConfig.DEBUG) {
            if (bottomContent != null) {
                bottomContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        performShowRecordCamera(10, 60);
                    }
                }, 2000);
            }
        }
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
                //                Observable.create(new ObservableOnSubscribe<JSONObject>() {
//                    @Override
//                    public void subscribe(ObservableEmitter<JSONObject> e) throws Exception {
//                        e.onNext(data);
//                    }
//                })
//                        .flatMap(new Function<JSONObject, ObservableSource<Integer>>() {
//                    @Override
//                    public ObservableSource<Integer> apply(JSONObject jsonObject) throws Exception {
//                        return Observable.just(data.optInt("open"));//,data.optString("srcType"),data.optString("testId"));
////                        return null;
//                    }
//                })
//                        .map(new Function<Integer, Object>() {
//                })
                Observable.create(new ObservableOnSubscribe<Integer>() {

                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        e.onNext(data.optInt("open"));
                        e.onComplete();
                    }
                }).observeOn(AndroidSchedulers.mainThread()).
                        subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                if (integer == 1) {
                                    performShowRecordCamera(answerTime, recordVideoTotalTime);
                                } else {
                                    superSpeakerBridge.timeUp();
                                }
                            }
                        });

//                if (open == 1) {
//                    performShowRecordCamera();
//                } else {
//                    iView.timeUp();
//                }
                break;
            }
            default: {
                break;
            }
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
        final JSONObject dataJson = jsonObject.optJSONObject("speechShow");
        if (dataJson != null) {
//            final int open = dataJson.optInt("open");
            courseWareId = dataJson.optString("testId");
            srcType = dataJson.optString("srcType");
            final int recordVideoTotalTime = dataJson.optInt("recordTime");
            final int answerTime = dataJson.optInt("answerTime");
            Observable.create(new ObservableOnSubscribe<Integer>() {
                @Override
                public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                    e.onNext(dataJson.optInt("open"));
                    e.onComplete();
                }
            }).map(new Function<Integer, Boolean>() {
                @Override
                public Boolean apply(Integer integer) throws Exception {
                    //1打开试题并且点击提交按钮
//                    if (integer == 0) {
//                        return false;
//                    } else if (
//                            ShareDataManager.getInstance().getInt(
//                                    ShareDataConfig.SUPER_SPEAKER_UPLOAD_SP_KEY + "_" + mGetInfo.getId() + "_" + courseWareId,
//                                    0,
//                                    ShareDataManager.SHAREDATA_NOT_CLEAR) > 0) {
//                        return false;
//                    } else {
//                        return true;
//                    }
                    return integer == 1 && ShareDataManager.getInstance().getInt(
                            ShareDataConfig.SUPER_SPEAKER_UPLOAD_SP_KEY + "_" + mGetInfo.getId() + "_" + courseWareId,
                            0,
                            ShareDataManager.SHAREDATA_NOT_CLEAR) == 0;

                }
            }).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Boolean>() {
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
//        Observable.create(new ObservableOnSubscribe<JSONObject>() {
//            @Override
//            public void subscribe(ObservableEmitter<JSONObject> e) throws Exception {
//                e.onNext(jsonObject.optJSONObject("speechShow"));
//            }
//        }).map(new Function<JSONObject, Integer>() {
//            @Override
//            public Integer apply(JSONObject jsonObject) throws Exception {
//                Integer open = dataJson.optInt("open");
//                courseWareId = dataJson.optString("testId");
//                srcType = dataJson.optString("srcType");
//                return open;
//            }
//        }).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Integer>() {
//                    @Override
//                    public void accept(Integer integer) throws Exception {
//                        performShowRecordCamera();
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        logger.i(throwable);
//                    }
//                });
    }

    SuperSpeakerBridge superSpeakerBridge;

    /**
     * 表现录制视频
     */
    @UiThread
    private void performShowRecordCamera(int answerTime, int recordTime) {
        try {
            if (superSpeakerBridge != null && superSpeakerBridge.containsView()) {
                return;
            }
            superSpeakerBridge = new SuperSpeakerBridge(mContext, this, mRootView, mLiveId, courseWareId);
            superSpeakerBridge.performShowRecordCamera(answerTime, recordTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        if (iView == null) {
//            iView = new SuperSpeakerCameraPager(mContext, this);
//        }
//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iView.getView().getLayoutParams();
//        if (layoutParams == null) {
//            logger.i("layoutParams = null");
//            layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//        }
//        //如果有录音权限
//        if (isHasRecordPermission()) {
//            logger.i("has record permission");
//            mRootView.addView(iView.getView(), layoutParams);
//        } else {
//            logger.i("no record permission");
//        }

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

    /**
     * 提交视频
     *
     * @param isForce 1：是 2：否
     */
    @Override
    public void submitSpeechShow(String isForce) {
        getHttpManager().sendSuperSpeakersubmitSpeech(
                mGetInfo.getId(),
                mGetInfo.getStuCouId(),
                mGetInfo.getStuId(),
                "1",
                courseWareId,
                srcType,
                isForce,
                new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        SuperSpeakerRedPackageEntity entity = getHttpResponseParser().parseSuperSpeakerSubmitEntity(responseEntity);
                        if (superSpeakerBridge != null) {
                            superSpeakerBridge.updateNum(entity.getMoney());
                        }
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

//    @Override
//    public void removeView(View view) {
//        if (view != null && view.getParent() == mRootView) {
//            mRootView.removeView(view);
//        }
//    }

    @Override
    public void stopRecord() {
//        if (iView != null) {
//            iView.startPlayVideo();
//        }
    }

    @Override
    public void uploadSucess(String videoUrl, String audioUrl) {
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
                "30",
                new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        XESToastUtils.showToast(mContext, "通知接口成功");
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
    public void onStop() {
        super.onStop();
        if (superSpeakerBridge != null) {
            superSpeakerBridge.pauseVideo();
        }
    }
}
