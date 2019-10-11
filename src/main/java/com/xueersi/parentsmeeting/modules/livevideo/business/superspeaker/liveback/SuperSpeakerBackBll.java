package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.liveback;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.xueersi.common.config.AppConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.BackMediaPlayerControl;
import com.xueersi.parentsmeeting.module.videoplayer.media.VP;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.HalfBodySceneTransAnim;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.ISuperSpeakerContract;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.UploadVideoService;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.entity.SuperSpeakerRedPackageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.entity.UploadVideoEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.page.SuperSpeakerPopWindowPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.StorageUtils;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.utils.SuperSpeakerUtils;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.widget.SuperSpeakerBridge;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.video.DoPSVideoHandle;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

import static com.xueersi.common.business.sharebusiness.config.LocalCourseConfig.CATEGORY_SUPER_SPEAKER;
import static com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.ISuperSpeakerContract.IS_PLAYBACK;

public class SuperSpeakerBackBll extends LiveBackBaseBll implements ISuperSpeakerContract.ICameraPresenter {

    public SuperSpeakerBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public void onQuestionEnd(VideoQuestionEntity questionEntity) {
        super.onQuestionEnd(questionEntity);

    }

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    BackMediaPlayerControl mediaPlayerControl;
    private String srcType, courseWareId;

    private SuperSpeakerBridge superSpeakerBridge;
    SuperSpeakerPopWindowPager superSpeakerPopWindowPager;

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, VideoQuestionEntity questionEntity, LiveBackBll.ShowQuestion showQuestion) {
        super.showQuestion(oldQuestionEntity, questionEntity, showQuestion);
        this.questionEntity = questionEntity;
//        SuperSpeakerCameraPager cameraPager = new SuperSpeakerCameraPager(mContext, this);
        srcType = questionEntity.getvQuestionType();
        courseWareId = questionEntity.getvQuestionID();

        int uploadStatus = questionEntity.getIsupload();
        SuperSpeakerPopWindowPager superSpeakerPopWindowPager;

        mediaPlayerControl = getInstance(BackMediaPlayerControl.class);
//        if (mediaPlayerControl != null) {//暂停视频
//            mediaPlayerControl.pause();
//        }

        if (uploadStatus == 1) {
            superSpeakerPopWindowPager = new SuperSpeakerPopWindowPager(mContext);
            superSpeakerPopWindowPager.setTextTip(mContext.getString(R.string.super_speaker_back_has_send));
            addPopWindowPager(superSpeakerPopWindowPager.getRootView());
            if (mediaPlayerControl != null) {
                mediaPlayerControl.seekTo(questionEntity.getvEndTime() * 1000);
            }
            putCurrentPos(questionEntity.getvEndTime() * 1000);
        } else {

            uploadStatus = StorageUtils.getStorageSPValue(liveGetInfo.getId(), courseWareId);

            if (uploadStatus == 0) {
                performStartRecord();
            } else if (uploadStatus == 2) {
                performLoadOver();
            } else {
                if (AppConfig.DEBUG) {
                    if (isServiceAlive() &&
                            UploadVideoService.getUploadingList().contains(StorageUtils.getVideoPath())) {
                        performShowInLoad();
                    } else {
                        performStartRecord();
                    }
                }
            }
        }
    }

    /**
     * 准备打开视频
     */
    private void performStartRecord() {
        UploadVideoEntity uploadVideoEntity = new UploadVideoEntity();
        uploadVideoEntity.setLiveId(liveGetInfo.getId());
        uploadVideoEntity.setTestId(courseWareId);
        uploadVideoEntity.setSrcType(srcType);
        uploadVideoEntity.setStuCouId(liveGetInfo.getStuCouId());
        uploadVideoEntity.setStuId(liveGetInfo.getStuId());
        uploadVideoEntity.setIsPlayBack("2");
        uploadVideoEntity.setIsUpload("2");
        superSpeakerBridge = new SuperSpeakerBridge(mContext, this, mRootView,
                liveGetInfo.getId(), courseWareId, IS_PLAYBACK, uploadVideoEntity);
        stopLiveVideo();
        superSpeakerBridge.performShowRecordCamera(questionEntity.getAnswerTime(), questionEntity.getRecordTime());
    }

    private void performLoadOver() {
        superSpeakerPopWindowPager = new SuperSpeakerPopWindowPager(mContext);
        superSpeakerPopWindowPager.setTextTip(mContext.getString(R.string.super_speaker_back_has_send));
        addPopWindowPager(superSpeakerPopWindowPager.getRootView());
        if (mediaPlayerControl != null) {
            mediaPlayerControl.seekTo(questionEntity.getvEndTime() * 1000);
        }
        putCurrentPos(questionEntity.getvEndTime() * 1000);
    }

    /**
     * 显示正在上传中
     */
    private void performShowInLoad() {
//        BackMediaPlayerControl mediaPlayerControl = getInstance(BackMediaPlayerControl.class);
        superSpeakerPopWindowPager = new SuperSpeakerPopWindowPager(mContext);
        superSpeakerPopWindowPager.setTextTip(mContext.getString(R.string.super_speaker_back_upload_in_background));
        addPopWindowPager(superSpeakerPopWindowPager.getRootView());
        if (mediaPlayerControl != null) {
            mediaPlayerControl.seekTo(questionEntity.getvEndTime() * 1000);
        }
        putCurrentPos(questionEntity.getvEndTime() * 1000);
    }

    /**
     * 当前Service是否存在
     *
     * @return
     */
    private boolean isServiceAlive() {

        return SuperSpeakerUtils.isServiceExisted(mContext, UploadVideoService.class.getName());
    }

//    private UploadVideoService uploadService;

//    private UploadServiceConnction serviceConnection;

//    private class UploadServiceConnction implements ServiceConnection {
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            if (service instanceof UploadVideoService.UploadBinder) {
//                uploadService = ((UploadVideoService.UploadBinder) service).getService();
////                Set<String> setString = ((UploadVideoService.UploadBinder) service).getUploadingVideo();
//            }
//        }

//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//
//        }
//    }

    private void addPopWindowPager(final View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        mRootView.addView(view, layoutParams);
        compositeDisposable.add(Observable.
                just(true).
                delay(3, TimeUnit.SECONDS).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean o) throws Exception {
                        if (view.getParent() == mRootView) {
                            mRootView.removeView(view);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        logger.e(throwable);
                    }
                }));
    }

    @Override
    public int[] getCategorys() {
        return new int[]{CATEGORY_SUPER_SPEAKER};
    }

    @Override
    public void submitSpeechShow(String isForce, String videoDuration) {
        getCourseHttpManager().sendSuperSpeakersubmitSpeech(
                liveGetInfo.getId(),
                liveGetInfo.getStuCouId(),
                liveGetInfo.getStuId(),
                IS_PLAYBACK,
                courseWareId,
                srcType,
                isForce,
                videoDuration,
                new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        SuperSpeakerRedPackageEntity entity = getCourseHttpResponseParser().parseSuperSpeakerSubmitEntity(responseEntity);
                        if (superSpeakerBridge != null) {
                            superSpeakerBridge.updateNum(entity.getMoney());
                        }
                    }
                });
    }

    @Override
    public void sendSuperSpeakerCameraStatus() {
        getCourseHttpManager().sendSuperSpeakerCameraStatus(liveGetInfo.getId(),
                liveGetInfo.getStuId(),
                courseWareId,
                new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        logger.i("摄像头状态成功");
                    }
                });
    }

    @Override
    public void stopRecord() {

    }

    @Deprecated
    @Override
    public void uploadSucess(String videoUrl, String audioUrl, String averVocieDecibel) {
        getCourseHttpManager().uploadSpeechShow(
                liveGetInfo.getId(),
                liveGetInfo.getStuCouId(),
                liveGetInfo.getStuId(),
                IS_PLAYBACK,
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
                    }

                    @Override
                    public void onPmError(ResponseEntity responseEntity) {
                        super.onPmError(responseEntity);
                        logger.i("upload pmError");
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        super.onPmFailure(error, msg);
                        logger.i("upload pmFailure");
                    }
                }
        );
    }

    private HalfBodySceneTransAnim mTransAnim;

    @Override
    public void showAnima() {
        if (mTransAnim == null) {
            mTransAnim = new HalfBodySceneTransAnim(activity, liveGetInfo);
        }
        mTransAnim.onModeChange(LiveTopic.MODE_TRANING, true);
    }

    @Override
    public void stopLiveVideo() {
        BackMediaPlayerControl mediaPlayerControl = getInstance(BackMediaPlayerControl.class);
        if (mediaPlayerControl != null) {
            mediaPlayerControl.release();
        }
//        mediaPlayerControl.stop();
        View view = activity.findViewById(R.id.vv_course_video_video);
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    private VideoQuestionEntity questionEntity;

    @Override
    public void startLiveVideo() {
        BackMediaPlayerControl mediaPlayerControl = getInstance(BackMediaPlayerControl.class);
        if (mediaPlayerControl != null) {
            mediaPlayerControl.startPlayVideo();

            putCurrentPos(questionEntity.getvEndTime() * 1000);

//            mediaPlayerControl.seekTo(questionEntity.getvEndTime() * 1000);
        }
        View view = activity.findViewById(R.id.vv_course_video_video);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void putCurrentPos(long pos) {
        String videoPath;
        String url = mVideoEntity.getVideoPath();
        if (url.contains("http") || url.contains("https")) {
            videoPath = DoPSVideoHandle.getPSVideoPath(url);
        } else {
            videoPath = url;
        }

        mShareDataManager.put(videoPath + mShareKey + VP.SESSION_LAST_POSITION_SUFIX, pos, ShareDataManager.SHAREDATA_USER);//重置播放进度
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (superSpeakerBridge != null) {
            superSpeakerBridge.pauseVideo();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (superSpeakerBridge == null) return;
        superSpeakerBridge.resumeVideo();
    }

    /** 进度缓存的追加KEY值 */
    private final String mShareKey = "LiveBack";
}
