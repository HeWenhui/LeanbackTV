package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.http.BaseHttp;
import com.xueersi.parentsmeeting.http.DownloadCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.util.ZipExtractorTask;
import com.xueersi.xesalib.utils.app.XESToastUtils;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.network.NetWorkHelper;
import com.xueersi.xesalib.view.alertdialog.VerifyCancelAlertDialog;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by linyuqiang on 2018/4/12.
 * 站立直播资源下载
 */
public class LiveStandFrameAnim {
    static String TAG = "LiveStandFrameAnim";
    Activity activity;
    public static String version = "2018041301";
    AbstractBusinessDataCallBack callBack;

    public LiveStandFrameAnim(Activity activity) {
        this.activity = activity;
    }

    public void check(LiveBll liveBll, final AbstractBusinessDataCallBack callBack) {
        final File externalFilesDir = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/" + version), "live_stand");
        if (!externalFilesDir.exists()) {
            externalFilesDir.mkdirs();
        }

        Loger.d(TAG, "LiveStandFrameAnim:externalFilesDir=" + externalFilesDir);

        final File saveFileZip = new File(externalFilesDir, "frame_anim.zip");
        final File tempFileZip = new File(externalFilesDir, "frame_anim.zip.tmp");
        final File saveFile = new File(externalFilesDir, "frame_anim");
        final File saveFileTemp = new File(externalFilesDir, "frame_anim.temp");
        this.callBack = callBack;
//        callBack.onDataSucess("");
        if (saveFileZip.exists()) {
            if (saveFile.exists()) {
                callBack.onDataSucess("");
            } else {
                ViewStub vs_live_stand_update = activity.findViewById(R.id.vs_live_stand_update);
                View view = vs_live_stand_update.inflate();
                view.findViewById(R.id.iv_live_stand_update_back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.finish();
                    }
                });
                ProgressBar pb_live_stand_update = view.findViewById(R.id.pb_live_stand_update);
                TextView tv_live_stand_update_zip = view.findViewById(R.id.tv_live_stand_update_zip);
                tv_live_stand_update_zip.setText("解压中");
                StandLiveZipExtractorTask zipExtractorTask = new StandLiveZipExtractorTask(saveFileZip, saveFileTemp, activity, pb_live_stand_update, callBack, saveFile, saveFileTemp);
                zipExtractorTask.execute();
            }
        } else {
            ViewStub vs_live_stand_update = activity.findViewById(R.id.vs_live_stand_update);
            View view = vs_live_stand_update.inflate();
            view.findViewById(R.id.iv_live_stand_update_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                }
            });
            final ProgressBar pb_live_stand_update = view.findViewById(R.id.pb_live_stand_update);
            final TextView tv_live_stand_update_zip = view.findViewById(R.id.tv_live_stand_update_zip);
            int netWorkType = NetWorkHelper.getNetWorkState(activity);
            if (netWorkType == NetWorkHelper.MOBILE_STATE) {
                final VerifyCancelAlertDialog cancelDialog = new VerifyCancelAlertDialog(activity, activity.getApplication(), false,
                        VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                cancelDialog.setCancelBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callBack.onDataSucess("");
                    }
                });
                cancelDialog.setVerifyBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        download(pb_live_stand_update, tv_live_stand_update_zip, saveFileZip, tempFileZip, saveFile, saveFileTemp);
                    }
                });
                pb_live_stand_update.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        pb_live_stand_update.getViewTreeObserver().removeOnPreDrawListener(this);
                        cancelDialog.setCancelShowText("取消").setVerifyShowText("继续观看").initInfo("您当前使用的是3G/4G网络，是否继续观看？",
                                VerifyCancelAlertDialog.CANCEL_SELECTED).showDialog();
                        return false;
                    }
                });
            } else {
                download(pb_live_stand_update, tv_live_stand_update_zip, saveFileZip, tempFileZip, saveFile, saveFileTemp);
            }
        }
    }

    private void download(final ProgressBar pb_live_stand_update, final TextView tv_live_stand_update_zip, final File saveFileZip, final File tempFileZip, final File saveFile, final File saveFileTemp) {
        final BaseHttp baseHttp = new BaseHttp(activity);
        final AtomicInteger times = new AtomicInteger();
        String url = "/android_stand_live/" + version + "/frame_anim3.zip";
        final String aliyun = "http://xesftp.oss-cn-beijing.aliyuncs.com" + url;
        String xuersi = "http://client.xesimg.com" + url;
        final String[] urls = new String[]{aliyun, xuersi};
        baseHttp.download(xuersi, tempFileZip.getPath(), new DownloadCallBack() {
            DownloadCallBack downloadCallBack = this;

            @Override
            protected void onDownloadSuccess() {
                tempFileZip.renameTo(saveFileZip);
                tv_live_stand_update_zip.setText("解压中");
                StandLiveZipExtractorTask zipExtractorTask = new StandLiveZipExtractorTask(saveFileZip, saveFileTemp, activity, pb_live_stand_update, callBack, saveFile, saveFileTemp);
                zipExtractorTask.execute();
            }

            @Override
            protected void onDownloadFailed() {
                if (times.get() < 3) {
                    times.getAndIncrement();
                    pb_live_stand_update.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String url = urls[times.get() % urls.length];
                            Loger.d(TAG, "onDownloadFailed:times=" + times.get() + ",url=" + url);
                            baseHttp.download(url, tempFileZip.getPath(), downloadCallBack);
                        }
                    }, 1000);
                } else {
                    XESToastUtils.showToast(activity, "下载失败，请检查您的网络或联络辅导老师");
                    pb_live_stand_update.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onDataSucess("");
                        }
                    }, 1000);
                }
            }

            @Override
            protected void onDownloading(int progress) {
                pb_live_stand_update.setProgress(progress);
            }
        });
    }

    static class StandLiveZipExtractorTask extends ZipExtractorTask {
        ProgressBar pb_live_stand_update;
        AbstractBusinessDataCallBack callBack;
        File saveFile;
        File saveFileTemp;

        public StandLiveZipExtractorTask(File in, File out, Context context, ProgressBar pb_live_stand_update, AbstractBusinessDataCallBack callBack, File saveFile, File saveFileTemp) {
            super(in, out, context, true);
            this.pb_live_stand_update = pb_live_stand_update;
            this.callBack = callBack;
            this.saveFile = saveFile;
            this.saveFileTemp = saveFileTemp;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values.length == 1) {
                Loger.d(TAG, "onProgressUpdate:progress=" + ((float) values[0] * 100f / (float) max));
                pb_live_stand_update.setProgress((int) (100 + ((float) values[0] * 100f / (float) max)));
            }
        }

        @Override
        protected void onPostExecute(Exception exception) {
            super.onPostExecute(exception);
            saveFileTemp.renameTo(saveFile);
            pb_live_stand_update.post(new Runnable() {
                @Override
                public void run() {
                    callBack.onDataSucess("");
                }
            });
        }
    }
}
