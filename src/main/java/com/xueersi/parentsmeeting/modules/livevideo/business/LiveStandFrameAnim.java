package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.http.BaseHttp;
import com.xueersi.parentsmeeting.http.DownloadCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.util.ZipExtractorTask;
import com.xueersi.xesalib.utils.app.XESToastUtils;
import com.xueersi.xesalib.utils.file.FileUtils;
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
    String eventId = LiveVideoConfig.LIVE_STAND_RES_UPDATE;
    static String TAG = "LiveStandFrameAnim";
    Activity activity;
    public static String version = "2018041501";
    final String filePath = "/android_stand_live/" + version + "/frame_anim4.zip";
    /** 下载地址，阿里云 */
    final String aliyun = "http://xesftp.oss-cn-beijing.aliyuncs.com" + filePath;
    /** 下载地址，网校 */
    final String xuersi = "http://client.xesimg.com" + filePath;
    /** 更新回调 */
    AbstractBusinessDataCallBack callBack;
    long downloadStart;
    long downloadSize = 117780122;

    public LiveStandFrameAnim(Activity activity) {
        this.activity = activity;
    }

    public void check(LiveBll liveBll, final AbstractBusinessDataCallBack callBack) {
        File alldir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/live_stand");
        File[] allcache = alldir.listFiles();
        if (allcache != null) {
            for (int i = 0; i < allcache.length; i++) {
                File cache = allcache[i];
                if (!cache.getPath().contains(version)) {
                    FileUtils.deleteDir(cache);
                }
            }
        }
        final File externalFilesDir = new File(alldir, version + "/live_stand");
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
                //activity_video_live_stand_check
                ViewStub vs_live_stand_update = activity.findViewById(R.id.vs_live_stand_update);
                View view = vs_live_stand_update.inflate();
//                view.setVisibility(View.INVISIBLE);
                view.findViewById(R.id.iv_live_stand_update_back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.finish();
                    }
                });
                TextView tv_live_stand_update_zip = view.findViewById(R.id.tv_live_stand_update_zip);
                tv_live_stand_update_zip.setText("解压中");
                StandLiveZipExtractorTask zipExtractorTask = new StandLiveZipExtractorTask(saveFileZip, saveFileTemp, activity, view, callBack, saveFile, saveFileTemp);
                zipExtractorTask.execute();
            }
        } else {
            //activity_video_live_stand_check
            ViewStub vs_live_stand_update = activity.findViewById(R.id.vs_live_stand_update);
            final View view = vs_live_stand_update.inflate();
//            view.setVisibility(View.INVISIBLE);
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
                        download(view, tv_live_stand_update_zip, saveFileZip, tempFileZip, saveFile, saveFileTemp);
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
                download(view, tv_live_stand_update_zip, saveFileZip, tempFileZip, saveFile, saveFileTemp);
            }
        }
    }

    private void download(final View view, final TextView tv_live_stand_update_zip, final File saveFileZip, final File tempFileZip, final File saveFile, final File saveFileTemp) {
        final BaseHttp baseHttp = new BaseHttp(activity);
        final AtomicInteger times = new AtomicInteger();
        final String[] urls = new String[]{aliyun, xuersi};
        final ProgressBar pb_live_stand_update = view.findViewById(R.id.pb_live_stand_update);
        final RelativeLayout rl_live_stand_update_prog = view.findViewById(R.id.rl_live_stand_update_prog);
        final TextView tv_live_stand_update_prog = view.findViewById(R.id.tv_live_stand_update_prog);
        downloadStart = System.currentTimeMillis();
        baseHttp.downloadRenew(xuersi, tempFileZip, new DownloadCallBack() {
            DownloadCallBack downloadCallBack = this;

            @Override
            protected void onDownloadSuccess() {
                tempFileZip.renameTo(saveFileZip);
                long downTime = (System.currentTimeMillis() - downloadStart) / 1000;
                double dspeed = downloadSize / downTime;
                String bps;
                if (dspeed >= 1024 * 1024) {
                    bps = String.format("%.2f", dspeed / 1024.0d / 1024.0d) + " MB/s";
                } else {
                    bps = String.format("%.2f", dspeed / 1024.0d) + " KB/s";
                }
                Loger.d(TAG, "onDownloadSuccess:bps=" + bps + ",downTime=" + downTime);
                StableLogHashMap logHashMap = new StableLogHashMap();
                logHashMap.put("bps", bps);
                logHashMap.put("downTime", "" + downTime);
                logHashMap.put("times", "" + times.get());
                logHashMap.put("version", "" + version);
                logHashMap.put("downloadsize", "" + downloadSize);
                Loger.d(activity, eventId, logHashMap.getData(), true);
                tv_live_stand_update_zip.setText("解压中");
                StandLiveZipExtractorTask zipExtractorTask = new StandLiveZipExtractorTask(saveFileZip, saveFileTemp, activity, view, callBack, saveFile, saveFileTemp);
                zipExtractorTask.execute();
            }

            @Override
            public boolean isCancle() {
                return activity.isFinishing();
            }

            @Override
            protected void onDownloadFailed() {
                if (times.get() < 3) {
                    times.getAndIncrement();
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String url = urls[times.get() % urls.length];
                            Loger.d(TAG, "onDownloadFailed:times=" + times.get() + ",url=" + url);
                            downloadStart = System.currentTimeMillis();
                            baseHttp.downloadRenew(url, tempFileZip, downloadCallBack);
                        }
                    }, 1000);
                } else {
                    XESToastUtils.showToast(activity, "下载失败，请检查您的网络或联络辅导老师");
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onDataSucess("");
                        }
                    }, 1000);
                }
            }

            @Override
            protected void onDownloading(int progress) {
                if (rl_live_stand_update_prog.getVisibility() != View.VISIBLE) {
                    rl_live_stand_update_prog.setVisibility(View.VISIBLE);
                }
                progress = progress / 2;
                pb_live_stand_update.setProgress(progress);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) pb_live_stand_update.getLayoutParams();
                RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) rl_live_stand_update_prog.getLayoutParams();
                int left = pb_live_stand_update.getWidth() * progress / 100;
                lp2.leftMargin = left - rl_live_stand_update_prog.getWidth() / 2 + lp.leftMargin;
                rl_live_stand_update_prog.setLayoutParams(lp2);
                tv_live_stand_update_prog.setText(progress + "%");
            }
        });
    }

    static class StandLiveZipExtractorTask extends ZipExtractorTask {
        AbstractBusinessDataCallBack callBack;
        File saveFile;
        File saveFileTemp;
        ProgressBar pb_live_stand_update;
        RelativeLayout rl_live_stand_update_prog;
        TextView tv_live_stand_update_prog;

        public StandLiveZipExtractorTask(File in, File out, Context context, View view, AbstractBusinessDataCallBack callBack, File saveFile, File saveFileTemp) {
            super(in, out, context, true);
            pb_live_stand_update = view.findViewById(R.id.pb_live_stand_update);
            rl_live_stand_update_prog = view.findViewById(R.id.rl_live_stand_update_prog);
            tv_live_stand_update_prog = view.findViewById(R.id.tv_live_stand_update_prog);
            this.callBack = callBack;
            this.saveFile = saveFile;
            this.saveFileTemp = saveFileTemp;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values.length == 1) {
                if (rl_live_stand_update_prog.getVisibility() != View.VISIBLE) {
                    rl_live_stand_update_prog.setVisibility(View.VISIBLE);
                }
                float progress = (50 + (float) values[0] * 100f / (float) max / 2);
                Loger.d(TAG, "onProgressUpdate:progress=" + ((float) values[0] * 100f / (float) max));
                pb_live_stand_update.setProgress((int) progress);
                tv_live_stand_update_prog.setText(((int) progress) + "%");
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) pb_live_stand_update.getLayoutParams();
                RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) rl_live_stand_update_prog.getLayoutParams();
                int left = (int) (pb_live_stand_update.getWidth() * progress / 100);
                lp2.leftMargin = left - rl_live_stand_update_prog.getWidth() / 2 + lp.leftMargin;
                rl_live_stand_update_prog.setLayoutParams(lp2);
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
