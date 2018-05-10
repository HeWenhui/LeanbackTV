package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.base.AbstractBusinessDataCallBack;
import com.xueersi.parentsmeeting.http.BaseHttp;
import com.xueersi.parentsmeeting.http.DownloadCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.StandLiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.util.FontCache;
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
    final String filePath = "/android_stand_live/" + StandLiveConfig.version + "/frame_anim6.zip";
    /** 下载地址，阿里云 */
    final String aliyun = "http://xesftp.oss-cn-beijing.aliyuncs.com" + filePath;
    /** 下载地址，网校 */
    final String xuersi = "http://client.xesimg.com" + filePath;
    /** 更新回调 */
    AbstractBusinessDataCallBack callBack;
    /** 解压任务 */
    StandLiveZipExtractorTask zipExtractorTask;
    /** 是不是取消 */
    boolean cancle = false;
    /** 下载开始时间 */
    long downloadStart;
    /** 下载文件大小 */
    long downloadSize = 117780122;
    Typeface fontFace;
    /** 进度条背景和里面进度的差值 */
    int progGap;
    /** 进度条里面进度的高度 */
    int progHeight;
    /** 进度条里面进度的高度 */
    int progWidth;

    public LiveStandFrameAnim(Activity activity) {
        this.activity = activity;
    }

    public void check(LiveBll liveBll, final AbstractBusinessDataCallBack callBack) {
        StandLiveConfig.createVoice(activity);
        File alldir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/live_stand");
        if (alldir == null) {
            alldir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/live_stand");
        }
        File[] allcache = alldir.listFiles();
        if (allcache != null) {
            for (int i = 0; i < allcache.length; i++) {
                File cache = allcache[i];
                if (!cache.getPath().contains(StandLiveConfig.version)) {
                    FileUtils.deleteDir(cache);
                }
            }
        }
        final File externalFilesDir = new File(alldir, StandLiveConfig.version + "/live_stand");
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
                fontFace = FontCache.getTypeface(activity, "fangzhengyouyuan.ttf");
                //activity_video_live_stand_check
                ViewStub vs_live_stand_update = activity.findViewById(R.id.vs_live_stand_update);
                View view = vs_live_stand_update.inflate();
                init(view);
//                view.setVisibility(View.INVISIBLE);
                view.findViewById(R.id.iv_live_stand_update_back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.finish();
                    }
                });
                LiveZip liveZip = new LiveZip(view, callBack, saveFile, saveFileTemp);
                zipExtractorTask = new StandLiveZipExtractorTask(saveFileZip, saveFileTemp, activity, liveZip);
                zipExtractorTask.execute();
            }
        } else {
            fontFace = FontCache.getTypeface(activity, "fangzhengyouyuan.ttf");
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
            init(view);
            final ProgressBar pb_live_stand_update = view.findViewById(R.id.pb_live_stand_update);
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
                        download(view, saveFileZip, tempFileZip, saveFile, saveFileTemp);
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
                download(view, saveFileZip, tempFileZip, saveFile, saveFileTemp);
            }
        }
    }

    private void init(View view) {
        ProgressBar pb_live_stand_update = view.findViewById(R.id.pb_live_stand_update);
        ViewGroup.LayoutParams layoutParams = pb_live_stand_update.getLayoutParams();
        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.bg_live_stand_update_prog_bg);
        Bitmap bitmap2 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.bg_live_stand_update_prog);
        //设置进度条宽度
        layoutParams.width = bitmap.getWidth();
        pb_live_stand_update.setLayoutParams(layoutParams);
        //进度条背景和里面进度的差值
        progGap = (bitmap.getWidth() - bitmap2.getWidth()) / 2;
        progHeight = bitmap2.getHeight();
        progWidth = bitmap2.getWidth();
        bitmap.recycle();
        bitmap2.recycle();
    }

    private void download(final View view, final File saveFileZip, final File tempFileZip, final File saveFile, final File saveFileTemp) {
        final BaseHttp baseHttp = new BaseHttp(activity);
        final AtomicInteger times = new AtomicInteger();
        final String[] urls = new String[]{aliyun, xuersi};
        final ProgressBar pb_live_stand_update = view.findViewById(R.id.pb_live_stand_update);
        final RelativeLayout rl_live_stand_update_prog = view.findViewById(R.id.rl_live_stand_update_prog);
        final ImageView iv_live_stand_update_prog_light = view.findViewById(R.id.iv_live_stand_update_prog_light);
        final TextView tv_live_stand_update_prog = view.findViewById(R.id.tv_live_stand_update_prog);
        tv_live_stand_update_prog.setTypeface(fontFace);
        downloadStart = System.currentTimeMillis();
        pb_live_stand_update.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                pb_live_stand_update.getViewTreeObserver().removeOnPreDrawListener(this);
                onProgress(pb_live_stand_update.getLeft(), rl_live_stand_update_prog, iv_live_stand_update_prog_light, pb_live_stand_update.getProgress());
                return false;
            }
        });
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
                logHashMap.put("version", "" + StandLiveConfig.version);
                logHashMap.put("downloadsize", "" + downloadSize);
                Loger.d(activity, eventId, logHashMap.getData(), true);
                onProgress(pb_live_stand_update.getLeft(), rl_live_stand_update_prog, iv_live_stand_update_prog_light, 50);
                LiveZip liveZip = new LiveZip(view, callBack, saveFile, saveFileTemp);
                zipExtractorTask = new StandLiveZipExtractorTask(saveFileZip, saveFileTemp, activity, liveZip);
                zipExtractorTask.execute();
            }

            @Override
            public boolean isCancle() {
                return cancle;
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
                    iv_live_stand_update_prog_light.setVisibility(View.VISIBLE);
                }
                progress = progress / 2;
                if (pb_live_stand_update.getProgress() != progress) {
                    pb_live_stand_update.setProgress(progress);
                    tv_live_stand_update_prog.setText(progress + "%");
                    final int finalProgress = progress;
                    tv_live_stand_update_prog.post(new Runnable() {
                        @Override
                        public void run() {
                            onProgress(pb_live_stand_update.getLeft(), rl_live_stand_update_prog, iv_live_stand_update_prog_light, finalProgress);
                        }
                    });
                }
            }
        });
    }

    /**
     * 进度条变化，更新上面的文字和光的位置
     *
     * @param progLeft                        进度条左边
     * @param rl_live_stand_update_prog       进度条上文字布局
     * @param iv_live_stand_update_prog_light 光的布局
     * @param progress                        进度
     */
    private void onProgress(int progLeft, RelativeLayout rl_live_stand_update_prog, ImageView iv_live_stand_update_prog_light, int progress) {
        int progTipWidth = rl_live_stand_update_prog.getWidth();
        int lightWidth = iv_live_stand_update_prog_light.getWidth();
        Loger.d(TAG, "onProgress:progLeft=" + progLeft + ",progTipWidth=" + progTipWidth + ",lightWidth=" + lightWidth);
        int left = (int) (((float) progWidth) * (float) progress / 100.0f);
        Loger.d(TAG, "onProgress:progress=" + progress + ",left=" + left);
        {
            RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) rl_live_stand_update_prog.getLayoutParams();
//                        int left = pb_live_stand_update.getWidth() * progress / 100;
            lp2.leftMargin = left - progTipWidth / 2 + progLeft + progGap - progHeight / 2;
            rl_live_stand_update_prog.setLayoutParams(lp2);
        }
        {
            RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) iv_live_stand_update_prog_light.getLayoutParams();
//                        int left = pb_live_stand_update.getWidth() * progress / 100;
            lp2.leftMargin = left - lightWidth / 2 + progLeft + progGap - progHeight / 2;
            iv_live_stand_update_prog_light.setLayoutParams(lp2);
        }
    }

    class LiveZip implements ZipProg {
        AbstractBusinessDataCallBack callBack;
        File saveFile;
        File saveFileTemp;
        ProgressBar pb_live_stand_update;
        RelativeLayout rl_live_stand_update_prog;
        ImageView iv_live_stand_update_prog_light;
        TextView tv_live_stand_update_prog;
        boolean cancle = false;
        int max;

        public LiveZip(View view, AbstractBusinessDataCallBack callBack, File saveFile, File saveFileTemp) {
            pb_live_stand_update = view.findViewById(R.id.pb_live_stand_update);
            rl_live_stand_update_prog = view.findViewById(R.id.rl_live_stand_update_prog);
            iv_live_stand_update_prog_light = view.findViewById(R.id.iv_live_stand_update_prog_light);
            tv_live_stand_update_prog = view.findViewById(R.id.tv_live_stand_update_prog);
            this.callBack = callBack;
            this.saveFile = saveFile;
            this.saveFileTemp = saveFileTemp;
            //解压开始，要删除以前旧的
            FileUtils.deleteDir(saveFile);
            FileUtils.deleteDir(saveFileTemp);
        }

        @Override
        public void setMax(int max) {
            this.max = max;
        }

        @Override
        public void onProgressUpdate(Integer... values) {
            if (values.length == 1) {
                if (rl_live_stand_update_prog.getVisibility() != View.VISIBLE) {
                    rl_live_stand_update_prog.setVisibility(View.VISIBLE);
                    iv_live_stand_update_prog_light.setVisibility(View.VISIBLE);
                }
                float progressF = (50 + (float) values[0] * 100f / (float) max / 2);
//                Loger.d(TAG, "onProgressUpdate:progress=" + ((float) values[0] * 100f / (float) max));
                final int progress = (int) progressF;
                if (pb_live_stand_update.getProgress() != progress) {
                    pb_live_stand_update.setProgress(progress);
                    tv_live_stand_update_prog.setText(progress + "%");
                    tv_live_stand_update_prog.post(new Runnable() {
                        @Override
                        public void run() {
                            onProgress(pb_live_stand_update.getLeft(), rl_live_stand_update_prog, iv_live_stand_update_prog_light, progress);
                        }
                    });
                }
            }
        }

        @Override
        public void onPostExecute(Exception exception) {
            if (exception == null) {
                int progress = 100;
                int progTipWidth = rl_live_stand_update_prog.getWidth();
                int lightWidth = iv_live_stand_update_prog_light.getWidth();
                int left = (int) (((float) progWidth) * (float) progress / 100.0f);
                {
                    RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) rl_live_stand_update_prog.getLayoutParams();
//                        int left = pb_live_stand_update.getWidth() * progress / 100;
                    lp2.leftMargin = left - progTipWidth / 2 + pb_live_stand_update.getLeft() + progGap - progHeight / 2;
                    rl_live_stand_update_prog.setLayoutParams(lp2);
                }
                {
                    RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) iv_live_stand_update_prog_light.getLayoutParams();
//                        int left = pb_live_stand_update.getWidth() * progress / 100;
                    lp2.leftMargin = left - lightWidth / 2 + pb_live_stand_update.getLeft() + progGap - progHeight / 2;
                    iv_live_stand_update_prog_light.setLayoutParams(lp2);
                }
                saveFileTemp.renameTo(saveFile);
                pb_live_stand_update.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onDataSucess("");
                    }
                });
            } else {
                if (!cancle) {
                    pb_live_stand_update.post(new Runnable() {
                        @Override
                        public void run() {
                            XESToastUtils.showToast(pb_live_stand_update.getContext(), "解压失败，请联络辅导老师");
                            pb_live_stand_update.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.onDataSucess("");
                                }
                            }, 1000);
                        }
                    });
                }
            }
        }
    }

    interface ZipProg {
        void onProgressUpdate(Integer... values);

        void onPostExecute(Exception exception);

        void setMax(int max);
    }

    static class StandLiveZipExtractorTask extends ZipExtractorTask {

        boolean cancle = false;
        ZipProg zipProg;

        public StandLiveZipExtractorTask(File in, File out, Context context, ZipProg zipProg) {
            super(in, out, context, true);
            this.zipProg = zipProg;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (cancle) {
                setCancle(true);
                Loger.d(TAG, "onProgressUpdate:cancle");
                return;
            }
            super.onProgressUpdate(values);
            zipProg.setMax(max);
            zipProg.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Exception exception) {
            super.onPostExecute(exception);
            zipProg.onPostExecute(exception);
        }
    }

    public void onDestory() {
        cancle = true;
        if (zipExtractorTask != null) {
            Loger.d(TAG, "onDestory:cancle");
            zipExtractorTask.cancle = true;
        }
    }
}
