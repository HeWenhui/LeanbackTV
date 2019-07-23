package com.xueersi.parentsmeeting.modules.livevideo.stablelog;

import android.content.Context;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.business.UserBll;
import com.xueersi.component.cloud.XesCloudUploadBusiness;
import com.xueersi.component.cloud.config.CloudDir;
import com.xueersi.component.cloud.config.XesCloudConfig;
import com.xueersi.component.cloud.entity.CloudUploadEntity;
import com.xueersi.component.cloud.entity.XesCloudResult;
import com.xueersi.component.cloud.listener.XesStsUploadListener;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveAppUserInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
*语文开讲吧 埋点日志
*@author chekun
*created  at 2019/5/23 13:54
*/
public class ChsSpeakLog {
    private static final String EVENT_ID = "live_chs_speak";

    /**
     * 统计学生作答方式
     * @param liveAndBackDebug
     * @param testid   试题id
     * @param mode     0/1 语音/手动
     * @param loadurl  试题加载地址
     * @param isplayback  0/1  是否是直播   直播/回放
     */
    public static void  anserMode(LiveAndBackDebug liveAndBackDebug, String testid,String mode,String loadurl,boolean isplayback){
        StableLogHashMap logHashMap = new StableLogHashMap("answerMode");
        logHashMap.put("testid", testid);
        logHashMap.put("mode", mode);
        logHashMap.put("loadurl", loadurl);
        logHashMap.put("isplayback", isplayback?"1":"0");
        liveAndBackDebug.umsAgentDebugInter(EVENT_ID, logHashMap.getData());
    }

    /**
     * 上传交互日志、阿里云
     *
     * @param msg
     */
    public static  void uploadLOG(Context mContext,final LiveAndBackDebug liveAndBackDebug,String testid, String assessContent, String liveId, File saveVideoFile) {
        final StableLogHashMap mData = new StableLogHashMap("uploadCloud");
        mData.put("userid", LiveAppUserInfo.getInstance().getStuId());
        mData.put("liveid", liveId);
        mData.put("testid", testid);
        mData.put("assessContent", assessContent);
        uploadCloud(mContext,saveVideoFile.getPath(), new AbstractBusinessDataCallBack() {
            @Override
            public void onDataSucess(Object... objData) {
                XesCloudResult result = (XesCloudResult) objData[0];
                mData.put("url", result.getHttpPath());
                mData.put("upload", "success");
                liveAndBackDebug.umsAgentDebugInter(EVENT_ID, mData.getData());
            }

            @Override
            public void onDataFail(int errStatus, String failMsg) {
                super.onDataFail(errStatus, failMsg);
                mData.put("upload", "fail");
                mData.put("url", "");
                liveAndBackDebug.umsAgentDebugInter(EVENT_ID, mData);
            }
        });
    }



    private static void uploadCloud(Context mContext,String path, final AbstractBusinessDataCallBack callBack) {
        XesCloudUploadBusiness uploadBusiness = new XesCloudUploadBusiness(mContext);
        final CloudUploadEntity entity = new CloudUploadEntity();
        entity.setFilePath(path);
        entity.setCloudPath(CloudDir.LIVE_SPEAK_CHINESE);
        entity.setType(XesCloudConfig.UPLOAD_OTHER);
        uploadBusiness.asyncUpload(entity, new XesStsUploadListener() {
            @Override
            public void onProgress(XesCloudResult result, int percent) {

            }

            @Override
            public void onSuccess(XesCloudResult result) {
                callBack.onDataSucess(result);
            }

            @Override
            public void onError(XesCloudResult result) {
                callBack.onDataFail(0, result.getErrorMsg());
            }
        });

    }


}
