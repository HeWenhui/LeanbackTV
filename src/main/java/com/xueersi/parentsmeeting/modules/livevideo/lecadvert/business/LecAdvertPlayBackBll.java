package com.xueersi.parentsmeeting.modules.livevideo.lecadvert.business;

import android.app.Activity;
import android.view.View;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.LiveMediaController;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.VideoPlayAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LecAdvertEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;

/**
 * Created by linyuqiang on 2018/7/18.
 * 直播回放讲座
 */
public class LecAdvertPlayBackBll extends LiveBackBaseBll {
    LecAdvertBll lecAdvertAction;

    public LecAdvertPlayBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object> businessShareParamMap) {

    }


    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        super.initView(bottomContent, mIsLand);
    }

    @Override
    public int[] getCategorys() {
        return new int[]{LocalCourseConfig.CATEGORY_LEC_ADVERT};
    }

    @Override
    public void showQuestion(VideoQuestionEntity oldQuestionEntity, final VideoQuestionEntity questionEntity) {
        mRootView.setVisibility(View.VISIBLE);
        int vCategory = questionEntity.getvCategory();
        switch (vCategory) {
            case LocalCourseConfig.CATEGORY_LEC_ADVERT: {
                VerifyCancelAlertDialog verifyCancelAlertDialog = new VerifyCancelAlertDialog(activity, activity.getApplication(), false,
                        VerifyCancelAlertDialog.TITLE_MESSAGE_VERIRY_CANCEL_TYPE);
                verifyCancelAlertDialog.initInfo("测试提醒", "老师发布了一套测试题，是否现在开始答题？");
                verifyCancelAlertDialog.setVerifyBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        if (vPlayer != null) {
//                            vPlayer.start();
//                        }

                    }
                });
                verifyCancelAlertDialog.setCancelBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LiveMediaController.MediaPlayerControl videoPlayAction = getInstance(LiveMediaController.MediaPlayerControl.class);
                        videoPlayAction.seekTo(questionEntity.getvEndTime() * 1000);
                        videoPlayAction.start();
                    }
                });
                verifyCancelAlertDialog.showDialog();
            }
            break;
            default:
                break;
        }
    }

}
