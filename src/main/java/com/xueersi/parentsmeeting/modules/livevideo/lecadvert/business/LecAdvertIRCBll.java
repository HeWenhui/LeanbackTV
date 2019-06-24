package com.xueersi.parentsmeeting.modules.livevideo.lecadvert.business;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.module.videoplayer.media.VideoView;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LecAdvertEntity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;

/**
 * Created by lyqai on 2018/7/18.
 */
public class LecAdvertIRCBll extends LiveBaseBll implements NoticeAction, LecAdvertHttp {
    LecAdvertBll lecAdvertAction;
    LecAdvertPopBll lecAdvertPopBll;

    public LecAdvertIRCBll(Activity activity, LiveBll2 liveBll2) {
        super(activity, liveBll2);
        lecAdvertPopBll = new LecAdvertPopBll(activity);
    }

    @Override
    public void onCreate(HashMap<String, Object> data) {
        super.onCreate(data);
        VideoView videoView = (VideoView) data.get("videoView");
        lecAdvertPopBll.setVideoView(videoView);
    }

    @Override
    public void onNotice(String sourceNick, String target, final JSONObject object, int type) {
        switch (type) {
            case XESCODE.LEC_ADVERT: {
                if (lecAdvertAction == null) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            lecAdvertAction = new LecAdvertBll(activity);
                            lecAdvertAction.setLecAdvertHttp(LecAdvertIRCBll.this);
                            lecAdvertAction.setLiveid(mLiveId);
                            lecAdvertAction.initView(mRootView, mLiveBll.getmIsLand().get());
                            startAdvert(object);
                        }
                    });
                } else {
                    startAdvert(object);
                }
            }
            break;
            default:
                break;
        }
    }

    private void startAdvert(JSONObject object) {
        if (lecAdvertAction != null) {
            LecAdvertEntity entity = new LecAdvertEntity();
            entity.course_id = object.optString("course_id");
            entity.id = object.optString("id");
            entity.nonce = object.optString("nonce");
            lecAdvertAction.start(entity);
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.LEC_ADVERT};
    }

    @Override
    public void getAdOnLL(final LecAdvertEntity lecAdvertEntity, final AbstractBusinessDataCallBack callBack) {
        getHttpManager().getAdOnLL(lecAdvertEntity.course_id, new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                logger.d( "getAdOnLL:onPmSuccess=" + responseEntity.getJsonObject());
                JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
                int isLearn = jsonObject.optInt("isLearn", 0);
                lecAdvertEntity.isLearn = isLearn;
                if (isLearn == 0) {
                    lecAdvertEntity.limit = jsonObject.optString("limit");
                    lecAdvertEntity.signUpUrl = jsonObject.optString("signUpUrl");
                    lecAdvertEntity.saleName = jsonObject.optString("saleName");
                    lecAdvertEntity.courseId = jsonObject.optString("courseId");
                    lecAdvertEntity.classId = jsonObject.optString("classId");
                }
                callBack.onDataSucess();
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                logger.d( "getAdOnLL:onPmError=" + responseEntity.getErrorMsg());
//                if(AppConfig.DEBUG){
//                    callBack.onDataSucess();
//                }
//                PageDataLoadManager.newInstance().loadDataStyle(pageDataLoadEntity.webDataError(responseEntity
// .getErrorMsg()));
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                logger.d( "getAdOnLL:onFailure", e);
//                if(AppConfig.DEBUG){
//                    callBack.onDataSucess();
//                }
//                PageDataLoadManager.newInstance().loadDataStyle(pageDataLoadEntity.webDataError());
            }
        });
    }

    @Override
    public void initView(RelativeLayout bottomContent, AtomicBoolean mIsLand) {
        if (lecAdvertAction != null) {
            lecAdvertAction.initView(bottomContent, mIsLand.get());
        }
        lecAdvertPopBll.setmIsLand(mIsLand);
    }

    @Override
    public void onResume() {
        super.onResume();
        lecAdvertPopBll.onResume();
    }

    @Override
    public void onDestory() {
        if (lecAdvertPopBll != null) {
            lecAdvertPopBll.onDestory();
        }
    }
}
