package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.component.cloud.XesCloudUploadBusiness;
import com.xueersi.component.cloud.config.CloudDir;
import com.xueersi.component.cloud.config.XesCloudConfig;
import com.xueersi.component.cloud.entity.CloudUploadEntity;
import com.xueersi.component.cloud.entity.XesCloudResult;
import com.xueersi.component.cloud.listener.XesStsUploadListener;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.entity.SuperSpeakerRedPackageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;

import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class SuperSpeakerBll extends LiveBaseBll implements NoticeAction, TopicAction, ISuperSpeakerContract.ICameraPresenter {
    ISuperSpeakerContract.ICameraView iView;

    public SuperSpeakerBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);

    }

    public void preCamera() {

    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        super.initView(bottomContent, mIsLand);
        if (bottomContent != null) {
            bottomContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    performShowRecordCamera();
                }
            }, 2000);
        }
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.SUPER_SPEAKER_TAKE_CAMERA: {
                int open = data.optInt("open");
                srcType = data.optString("srcType");
                courseWareId = data.optString("testId");

                Observable.range(1, 5).map(String::valueOf).subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

                Observable.create(
                        (ObservableEmitter<Integer> e) -> {

                        }
//                        new ObservableOnSubscribe<Integer>() {
//                    @Override
//                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
//
//                    }
//                }
                ).observeOn(AndroidSchedulers.mainThread()).
                        subscribe(new Observer<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Integer integer) {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                if (open == 1) {
                    performShowRecordCamera();
                } else {
                    iView.timeUp();
                }
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
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        JSONObject dataJson = jsonObject.optJSONObject("speechShow");
        if (dataJson != null) {
            boolean open = dataJson.optBoolean("open");
            courseWareId = dataJson.optString("testId");
            srcType = dataJson.optString("srcType");
            if (open) {
                performShowRecordCamera();
            }
        }
    }

    /**
     * 表现录制视频
     */
    private void performShowRecordCamera() {
        if (iView == null) {
            iView = new SuperSpeakerCameraPager(mContext, this);
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iView.getView().getLayoutParams();
        if (layoutParams == null) {
            logger.i("layoutParams = null");
            layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        }
        //如果有录音权限
        if (isHasRecordPermission()) {
            logger.i("has record permission");
            mRootView.addView(iView.getView(), layoutParams);
        } else {
            logger.i("no record permission");
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

    /**
     * 提交视频
     *
     * @param isForce 1：是 2：否
     */
    @Override
    public void submitSpeechShow(String isForce) {
        uploadVideo();
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
                        if (iView != null) {
                            iView.updateNum(entity.getMoney());
                        }
                    }
                });
    }

    XesCloudUploadBusiness business;
    File file;

    private void uploadVideo() {
        business = new XesCloudUploadBusiness(mContext);
        final String path = Environment.getExternalStorageDirectory() + "/parentsmeeting/love.mp4";

        CloudUploadEntity entity = new CloudUploadEntity();
        String id = UUID.randomUUID().toString();
        entity.setFileId(id);
        entity.setCloudPath(CloudDir.CLOUD_TEST);
        entity.setFilePath(path);
        entity.setType(XesCloudConfig.UPLOAD_OTHER);
        file = new File(path);
        if (!file.exists()) {
            XESToastUtils.showToast(mContext, "录制失败");
            return;
        }
        business.asyncUpload(entity, new XesStsUploadListener() {
            @Override
            public void onProgress(XesCloudResult result, int percent) {
                if (percent % 10 == 0) {
                    XESToastUtils.showToast(mContext, "上传进度：" + percent + "    " + "视频总大小:" + getDataSize(file.length()));
                }
            }

            @Override
            public void onSuccess(XesCloudResult result) {
                XESToastUtils.showToast(mContext, "complete");
//                mNotificationManager.cancel(1099);

            }

            @Override
            public void onError(XesCloudResult result) {
                XESToastUtils.showToast(mContext, JSON.toJSONString(result));

            }
        });

    }

    public static String getDataSize(long var0) {
        DecimalFormat var2 = new DecimalFormat("###.00");
        return var0 < 1024L ? var0 + "bytes" : (var0 < 1048576L ? var2.format((double) ((float) var0 / 1024.0F))
                + "KB" : (var0 < 1073741824L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F))
                + "MB" : (var0 > 0L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F / 1024.0F))
                + "GB" : "error")));
    }

    /**
     * 是否有相机和语音权限
     *
     * @return
     */
    private boolean isHasRecordPermission() {
        PackageManager pkm = mContext.getPackageManager();
        return (PackageManager.PERMISSION_GRANTED == pkm.checkPermission("android.permission.MODIFY_AUDIO_SETTINGS", mContext.getPackageName())
                && PackageManager.PERMISSION_GRANTED == pkm.checkPermission("android.permission.RECORD_AUDIO", mContext.getPackageName())
                && PackageManager.PERMISSION_GRANTED == pkm.checkPermission("android.permission.CAMERA", mContext.getPackageName()));
    }
}
