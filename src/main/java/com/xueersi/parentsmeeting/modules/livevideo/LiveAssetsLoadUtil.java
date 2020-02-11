package com.xueersi.parentsmeeting.modules.livevideo;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.core.LiveCrashReport;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.sharebusiness.config.ShareBusinessConfig;
import com.xueersi.common.business.sharebusiness.http.downloadAppfile.entity.DownLoadFileInfo;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.util.LoadFileCallBack;
import com.xueersi.common.util.LoadFileUtils;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoTransferActivity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.widget.DataLoadManager;
import com.xueersi.ui.dataload.DataLoadEntity;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import java.util.HashMap;
import java.util.Map;


/**
 * 直播 加载 工具类
 *
 * @author shixiaoqiang
 */
public class LiveAssetsLoadUtil {


    public static DataLoadEntity mDataLoadEntity;

    public static String TAG = "LiveAssetsLoadUtil";
    /**
     * 失败次数
     */
    public static Map failModule = new HashMap<String, Integer>();

    /**
     * 加载assert 文件
     */
    public static void loadAssertsResource(final Activity context, final LoadFileCallBack callback) {

        if (!LiveVideoConfig.assetsDownloadTag) {
            if (callback != null) {
                callback.success();
                return;
            }
        }

        if (!XesPermission.checkPermissionNoAlert(ContextManager.getApplication(), PermissionConfig
                .PERMISSION_CODE_STORAGE)) {
            XESToastUtils.showToast(context, "请检查存储权限");
            if (context instanceof LiveVideoTransferActivity) {
                context.finish();
            }
            return;
        }

        //服务端获取
        DownLoadFileInfo info = LiveVideoConfig.getDownLoadFileInfo();
        mDataLoadEntity = new DataLoadEntity(context);
        LoadFileUtils.loadFileFromServer(context, info, new LoadFileCallBack() {
            long starttime;

            @Override
            public void start() {
                starttime = System.currentTimeMillis();
                //XESToastUtils.showToast(context, "开始加载");
                //https://bugly.qq.com/v2/crash-reporting/crashes/a0df5ed682/350181?pid=1
                try {
                    mDataLoadEntity.beginLoading();
                    DataLoadManager.newInstance().loadDataStyle(context, mDataLoadEntity);
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                }
                callback.start();
            }

            @Override
            public void success() {
                //XESToastUtils.showToast(context, "加载成功");
                mDataLoadEntity.webDataSuccess();
                try {
                    if (!context.isFinishing()) {
                        DataLoadManager.newInstance().loadDataStyle(context, mDataLoadEntity);
                    }
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                }
                try {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("logtype", "success");
                    map.put("downtime", "" + (System.currentTimeMillis() - starttime));
                    UmsAgentManager.umsAgentDebug(context, TAG, map);
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                }
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

                if (!planB("livevdieo", context)) {
                    XESToastUtils.showToast(context, "加载失败,  请重试");
                }
                try {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("logtype", "fail");
                    map.put("downtime", "" + (System.currentTimeMillis() - starttime));
                    map.put("errorCode", "" + errorCode);
                    map.put("errorMsg", "" + errorMsg);
                    String times = "0";
                    if (failModule.get("livevdieo") != null) {
                        times = "" + failModule.get("livevdieo");
                    }
                    map.put("times", "" + times);
                    UmsAgentManager.umsAgentDebug(context, TAG, map);
                } catch (Exception e) {
                    LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                }
                mDataLoadEntity.webDataSuccess();
                DataLoadManager.newInstance().loadDataStyle(context, mDataLoadEntity);

                if (context instanceof LiveVideoTransferActivity) {
                    context.finish();
                }
                callback.fail(errorCode, errorMsg);
            }
        });
    }


    /**
     * 插件降级方案
     *
     * @param name
     * @param context
     * @return
     */
    public static boolean planB(String name, final Context context) {

        int count = 0;
        if (failModule.get(name) == null) {

            failModule.put(name, 1);
        } else {
            count = (int) failModule.get(name);
            failModule.put(name, count + 1);
        }

        if (count + 1 > 6) {


            VerifyCancelAlertDialog dialog = new VerifyCancelAlertDialog(context, ((Activity) context).getApplication
                    (), false,
                    VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);

            //dialog.initInfo("抱歉资源加载失败，是否更新APP ？");
            dialog.setVerifyBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String channel = "allapk";
                    AppBll.getInstance().startUpdateApp(ShareBusinessConfig.URL_UPDATE_GET_NEW_APP + "?channel=" +
                                    channel, 1,
                            null);
                    try {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("logtype", "planBok");
                        map.put("times", "" + failModule.get("livevdieo"));
                        UmsAgentManager.umsAgentDebug(context, TAG, map);
                    } catch (Exception e) {
                        LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                    }
                    ((Activity) context).finish();
                }
            });

            dialog.setCancelBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("logtype", "planBcancle");
                        map.put("times", "" + failModule.get("livevdieo"));
                        UmsAgentManager.umsAgentDebug(context, TAG, map);
                    } catch (Exception e) {
                        LiveCrashReport.postCatchedException(new LiveException(TAG, e));
                    }
                    ((Activity) context).finish();
                }
            });

            dialog.initInfo("抱歉资源加载失败，是否更新APP ？", VerifyCancelAlertDialog.CANCEL_SELECTED).showDialog();
            return true;
        }
        return false;
    }


}
