package com.xueersi.parentsmeeting.modules.livevideo.deskmate.business;

import android.app.Activity;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;

/**
 * 小学找同桌
 */
public class DeskmateBll extends LiveBaseBll {
    public DeskmateBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        boolean isPrimary = context.getIntent().getBooleanExtra("isPrimary", false);
        if (!isPrimary) {
            liveBll.removeBusinessBll(this);
        }
    }

    @Override
    public void onDestory() {
        super.onDestory();
        getHttpManager().saveStuPlanOnlineTime(mGetInfo.getStuId(), new HttpCallBack() {
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
