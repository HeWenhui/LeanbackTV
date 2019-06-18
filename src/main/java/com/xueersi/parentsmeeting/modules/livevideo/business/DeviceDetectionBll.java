package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.content.Context;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.DeviceDetectionEntity;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpManager;
import com.xueersi.parentsmeeting.modules.livevideo.http.LiveHttpResponseParser;
import com.xueersi.ui.dataload.DataLoadEntity;

/**
 * Created by ZhangYuansun on 2018/9/13
 */

public class DeviceDetectionBll extends BaseBll {
    LiveHttpManager mLiveHttpManager;
    LiveHttpResponseParser mLiveHttpResponseParser;
    public DeviceDetectionBll(Context context) {
        super(context);
        mLiveHttpManager = new LiveHttpManager(context);
        mLiveHttpResponseParser = new LiveHttpResponseParser(context);
    }
    /**
     * 低端设备检测信息
     */
    public void getDeviceDetectionInfo(final DataLoadEntity dataLoadEntity, final AbstractBusinessDataCallBack businessDataCallBack) {
        postDataLoadEvent(dataLoadEntity.beginLoading());
        mLiveHttpManager.getDeviceDetectionInfo(new HttpCallBack(dataLoadEntity) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                logger.i("onPmSuccess: " + responseEntity.getJsonObject().toString());
                DeviceDetectionEntity deviceDetectionEntity = mLiveHttpResponseParser.parseDeviceDetectionInfo
                        (responseEntity);
                if (!isEmpty(deviceDetectionEntity, dataLoadEntity)) {
                    businessDataCallBack.onDataSucess(deviceDetectionEntity);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                logger.i("onPmFailure: " + msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                logger.i("onPmError: " + responseEntity.getErrorMsg());
            }
        });
    }
}
