package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewStub;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.http.BaseHttp;
import com.xueersi.parentsmeeting.http.DownloadCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.util.ZipExtractorTask;
import com.xueersi.xesalib.utils.log.Loger;

import java.io.File;
import java.io.IOException;

/**
 * Created by linyuqiang on 2018/4/12.
 */

public class LiveStandFrameAnim {
    static String TAG = "LiveStandFrameAnim";
    Activity activity;

    public LiveStandFrameAnim(Activity activity) {
        this.activity = activity;
    }

    public void check(LiveBll liveBll, final AbstractBusinessDataCallBack callBack) {
        final File externalFilesDir = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "live_stand");
        if (!externalFilesDir.exists()) {
            externalFilesDir.mkdirs();
        }

        Loger.d(TAG, "LiveStandFrameAnim:externalFilesDir=" + externalFilesDir);
        BaseHttp baseHttp = new BaseHttp(activity);
        final File saveFileZip = new File(externalFilesDir, "frame_anim.zip");
        final File tempFileZip = new File(externalFilesDir, "frame_anim.zip.tmp");
        final File saveFile = new File(externalFilesDir, "frame_anim");
        final File saveFileTemp = new File(externalFilesDir, "frame_anim.temp");

//        callBack.onDataSucess("");
        if (saveFileZip.exists()) {
            if (saveFile.exists()) {
                callBack.onDataSucess("");
            } else {
                ViewStub vs_live_stand_update = activity.findViewById(R.id.vs_live_stand_update);
                View view = vs_live_stand_update.inflate();
                ProgressBar pb_live_stand_update = view.findViewById(R.id.pb_live_stand_update);
                TextView tv_live_stand_update_zip = view.findViewById(R.id.tv_live_stand_update_zip);
                tv_live_stand_update_zip.setText("解压中");
                StandLiveZipExtractorTask zipExtractorTask = new StandLiveZipExtractorTask(saveFileZip, saveFileTemp, activity, pb_live_stand_update, callBack, saveFile, saveFileTemp);
                zipExtractorTask.execute();
            }
        } else {
            ViewStub vs_live_stand_update = activity.findViewById(R.id.vs_live_stand_update);
            View view = vs_live_stand_update.inflate();
            final ProgressBar pb_live_stand_update = view.findViewById(R.id.pb_live_stand_update);
            final TextView tv_live_stand_update_zip = view.findViewById(R.id.tv_live_stand_update_zip);
            //"http://xesftp.oss-cn-beijing.aliyuncs.com/android_stand_live/2018041301/frame_anim.zip"
            baseHttp.download("http://client.xesimg.com/android_stand_live/2018041301/frame_anim.zip", tempFileZip.getPath(), new DownloadCallBack() {
                @Override
                protected void onDownloadSuccess() {
                    tempFileZip.renameTo(saveFileZip);
                    tv_live_stand_update_zip.setText("解压中");
                    StandLiveZipExtractorTask zipExtractorTask = new StandLiveZipExtractorTask(saveFileZip, saveFileTemp, activity, pb_live_stand_update, callBack, saveFile, saveFileTemp);
                    zipExtractorTask.execute();
                }

                @Override
                protected void onDownloadFailed() {

                }

                @Override
                protected void onDownloading(int progress) {
                    pb_live_stand_update.setProgress(progress);
                }
            });
        }
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
