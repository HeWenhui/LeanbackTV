package com.xueersi.parentsmeeting.modules.livevideo.deskmate.business;

import android.app.Activity;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBackBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

import java.util.HashMap;

/**
 * 小学找同桌
 */
public class DeskmateLiveBackBll extends LiveBackBaseBll {

    public DeskmateLiveBackBll(Activity activity, LiveBackBll liveBackBll) {
        super(activity, liveBackBll);
    }

    @Override
    public void onCreate(VideoLivePlayBackEntity mVideoEntity, LiveGetInfo liveGetInfo, HashMap<String, Object> businessShareParamMap) {
        int isPrimarySchool = liveGetInfo.getIsPrimarySchool();
        if (isPrimarySchool != 1) {
            liveBackBll.removeBusinessBll(this);
        }
    }

    @Override
    public void onDestory() {
        super.onDestory();
        getCourseHttpManager().saveStuPlanOnlineTime(liveGetInfo.getStuId(), new HttpCallBack() {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.d("saveStuPlanOnlineTime:onPmSuccess:responseEntity=" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                super.onPmFailure(error, msg);
                logger.d("saveStuPlanOnlineTime:onPmFailure:msg=" + msg, error);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                super.onPmError(responseEntity);
                logger.d("saveStuPlanOnlineTime:onPmError:msg=" + responseEntity.getErrorMsg());
            }
        });
    }
}
