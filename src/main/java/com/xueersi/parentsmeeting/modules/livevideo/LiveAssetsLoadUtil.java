package com.xueersi.parentsmeeting.modules.livevideo;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;

import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.sharebusiness.http.downloadAppfile.entity.DownLoadFileInfo;
import com.xueersi.common.util.LoadCallback;
import com.xueersi.common.util.LoadFileCallBack;
import com.xueersi.common.util.LoadFileUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.ui.dataload.DataLoadEntity;
import com.xueersi.ui.dataload.DataLoadManager;

public class LiveAssetsLoadUtil {


    public static DataLoadEntity mDataLoadEntity;

    /**
     * 加载assert 文件
     */
    public static void loadAssertsResource(final Activity context, final LoadCallback callback) {


        //服务端获取
        DownLoadFileInfo downLoadInfo = AppBll.getInstance().getDownLoadFileByFileName("assets.zip");
        DownLoadFileInfo info = null;
        if (downLoadInfo != null) {
            info = downLoadInfo;
            info.dirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        mDataLoadEntity = new DataLoadEntity(context);
        if (info == null) {
            info = new DownLoadFileInfo();
            info.fileName = "assets.zip";
            info.fileMD5 = "f94553e8a25d47d107f81fccade5cbcb";
            info.fileType = 0;
            info.fileUrl = "https://xeswxapp.oss-cn-beijing.aliyuncs.com/Android/asserts/livevideo/assets.zip";
            info.needManualDownload = true;
            info.id = 0;
            info.dirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        LoadFileUtils.loadFileFromServer(context, info, new LoadFileCallBack() {

            @Override
            public void start() {
                //XESToastUtils.showToast(context, "开始加载");
                mDataLoadEntity.beginLoading();
                DataLoadManager.newInstance().loadDataStyle(context, mDataLoadEntity);
                callback.start();
            }

            @Override
            public void success() {
                //XESToastUtils.showToast(context, "加载成功");
                mDataLoadEntity.webDataSuccess();
                DataLoadManager.newInstance().loadDataStyle(context, mDataLoadEntity);

                callback.success();
            }

            @Override
            public void progress(float progress, int type) {

                if (type == 0) {
                    mDataLoadEntity.setProgressTip("加载中" + (int) (progress) + "%");
                } else {
                    mDataLoadEntity.setProgressTip("解压中...");
                }


                mDataLoadEntity.beginLoading();
                mDataLoadEntity.setCurrentLoadingStatus(DataLoadEntity.DATA_PROGRESS);
                DataLoadManager.newInstance().loadDataStyle(context, mDataLoadEntity);
                callback.progress(progress, type);
            }

            @Override
            public void fail(int errorCode, String errorMsg) {
                XESToastUtils.showToast(context, "加载失败,  请重试");
                mDataLoadEntity.webDataSuccess();
                DataLoadManager.newInstance().loadDataStyle(context, mDataLoadEntity);

                callback.fail(errorCode, errorMsg);
            }
        });
    }


}
