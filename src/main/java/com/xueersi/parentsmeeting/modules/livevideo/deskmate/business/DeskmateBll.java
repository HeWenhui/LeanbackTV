package com.xueersi.parentsmeeting.modules.livevideo.deskmate.business;

import android.app.Activity;

import com.xueersi.common.business.UserBll;
import com.xueersi.common.entity.MyUserInfoEntity;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

/**
 * 小学找同桌
 */
public class DeskmateBll extends LiveBaseBll {

    public DeskmateBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        boolean isPrimary = activity.getIntent().getBooleanExtra("isPrimary", false);
        if (!isPrimary) {
            mLiveBll.removeBusinessBll(this);
        }
    }

    @Override
    public void onDestory() {
        super.onDestory();
        MyUserInfoEntity userInfoEntity = UserBll.getInstance().getMyUserInfoEntity();
        getHttpManager().saveStuPlanOnlineTime(mGetInfo.getStuId(), userInfoEntity.getGradeCode(), new HttpCallBack(false) {
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
