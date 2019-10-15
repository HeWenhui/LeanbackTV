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

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.http.BaseHttp;
import com.xueersi.common.http.DownloadCallBack;
import com.xueersi.common.util.FontCache;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.drawable.DrawableHelper;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.config.StandLiveConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StableLogHashMap;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.StandLoadLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveSoundPool;
import com.xueersi.parentsmeeting.modules.livevideo.util.ZipExtractorTask;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by linyuqiang on 2018/4/12.
 * 站立直播资源下载
 */
public class LiveStandFrameAnim {
    String eventId = LiveVideoConfig.LIVE_STAND_RES_UPDATE;
    static String TAG = "LiveStandFrameAnim";
    protected static Logger logger = LoggerFactory.getLogger(TAG);
    Activity activity;
    private final String filePath = "/android_stand_live/" + StandLiveConfig.version + "/frame_anim7.zip";
    /** 下载地址，阿里云 */
    private final String aliyun = "http://xesftp.oss-cn-beijing.aliyuncs.com" + filePath;
    /** 下载地址，网校 */
    private final String xuersi = "http://client.xesimg.com" + filePath;
    /** 更新回调 */
    AbstractBusinessDataCallBack callBack;
    /** 解压任务 */
    private StandLiveZipExtractorTask zipExtractorTask;
    /** 是不是取消 */
    private boolean cancle = false;
    /** 下载开始时间 */
    private long downloadStart;
    /** 下载文件大小 */
    private long downloadSize = 117780122;
    private Typeface fontFace;
    /** 进度条背景和里面进度的差值 */
    private int progGap;
    /** 进度条里面进度的高度 */
    private int progHeight;
    /** 进度条里面进度的高度 */
    private int progWidth;
    private LiveSoundPool liveSoundPool;
    private LiveSoundPool.SoundPlayTask loadTask;
    private LogToFile mLogtf;

    public LiveStandFrameAnim(Activity activity) {
        this.activity = activity;
        mLogtf = new LogToFile(activity, TAG);
    }

    /**
     * 加载音效
     */
    public void loading() {
        loadTask = new LiveSoundPool.SoundPlayTask(R.raw.live_stand_loading, StandLiveConfig.MUSIC_VOLUME_RATIO_FRONT, true);
        int soundId = LiveSoundPool.play(activity, liveSoundPool, loadTask);
    }

    public void check(final AbstractBusinessDataCallBack callBack) {
        StandLiveConfig.createVoice(activity);
        File alldir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/live_stand");
        if (alldir == null) {
            String status = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(status)) {
                alldir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/live_stand");
            } else {
                alldir = new File(activity.getFilesDir(), "live_stand");
            }
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

        mLogtf.d("LiveStandFrameAnim:externalFilesDir=" + externalFilesDir);

        final File saveFileZip = new File(externalFilesDir, "frame_anim.zip");
        final File tempFileZip = new File(externalFilesDir, "frame_anim.zip.tmp");
        final File saveFile = new File(externalFilesDir, "frame_anim");
        final File saveFileTemp = new File(externalFilesDir, "frame_anim.temp");
        this.callBack = callBack;
        StandLoadLog.downFile(saveFile, saveFileZip);
//        callBack.onDataSucess("");
        if (saveFileZip.exists()) {
            if (saveFile.exists()) {
                callBack.onDataSucess("");
            } else {
                liveSoundPool = LiveSoundPool.createSoundPool();
                loading();
                fontFace = FontCache.getTypeface(activity, "fangzhengcuyuan.ttf");
                //activity_video_live_stand_check
                ViewStub vsLiveStandUpdate = activity.findViewById(R.id.vs_live_stand_update);
                View view = vsLiveStandUpdate.inflate();
                init(view);
//                view.setVisibility(View.INVISIBLE);
                view.findViewById(R.id.iv_live_stand_update_back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.finish();
                    }
                });
                LiveZip liveZip = new LiveZip(view, callBack, saveFile, saveFileTemp);
                zipExtractorTask = new StandLiveZipExtractorTask(saveFileZip, saveFileTemp, liveZip);
                zipExtractorTask.execute();
            }
        } else {
            liveSoundPool = LiveSoundPool.createSoundPool();
            loading();
            fontFace = FontCache.getTypeface(activity, "fangzhengcuyuan.ttf");
            //activity_video_live_stand_check
            ViewStub vsLiveStandUpdate = activity.findViewById(R.id.vs_live_stand_update);
            final View view = vsLiveStandUpdate.inflate();
//            view.setVisibility(View.INVISIBLE);
            view.findViewById(R.id.iv_live_stand_update_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                }
            });
            init(view);
            final ProgressBar pbLiveStandUpdate = view.findViewById(R.id.pb_live_stand_update);
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
                pbLiveStandUpdate.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        pbLiveStandUpdate.getViewTreeObserver().removeOnPreDrawListener(this);
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
        ProgressBar pbLiveStandUpdate = view.findViewById(R.id.pb_live_stand_update);
        ViewGroup.LayoutParams layoutParams = pbLiveStandUpdate.getLayoutParams();
        Bitmap bitmap = DrawableHelper.bitmapFromResource(activity.getResources(), R.drawable.bg_live_stand_update_prog_bg);
        Bitmap bitmap2 = DrawableHelper.bitmapFromResource(activity.getResources(), R.drawable.bg_live_stand_update_prog);
        //设置进度条宽度
        layoutParams.width = bitmap.getWidth();
        LayoutParamsUtil.setViewLayoutParams(pbLiveStandUpdate, layoutParams);
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
        final ProgressBar pbLiveStandUpdate = view.findViewById(R.id.pb_live_stand_update);
        final RelativeLayout rlLiveStandUpdateProg = view.findViewById(R.id.rl_live_stand_update_prog);
        final ImageView ivLiveStandUpdateProgLight = view.findViewById(R.id.iv_live_stand_update_prog_light);
        final TextView tvLiveStandUpdateProg = view.findViewById(R.id.tv_live_stand_update_prog);
        tvLiveStandUpdateProg.setTypeface(fontFace);
        downloadStart = System.currentTimeMillis();
        pbLiveStandUpdate.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                pbLiveStandUpdate.getViewTreeObserver().removeOnPreDrawListener(this);
                onProgress(pbLiveStandUpdate.getLeft(), rlLiveStandUpdateProg, ivLiveStandUpdateProgLight, pbLiveStandUpdate.getProgress());
                return false;
            }
        });
        baseHttp.downloadRenew(xuersi, tempFileZip, new DownloadCallBack() {
            DownloadCallBack downloadCallBack = this;

            @Override
            protected void onDownloadSuccess() {
                tempFileZip.renameTo(saveFileZip);
                long downTime = (System.currentTimeMillis() - downloadStart) / 1000 + 1;
                double dspeed = downloadSize / downTime;
                String bps;
                if (dspeed >= 1024 * 1024) {
                    bps = String.format("%.2f", dspeed / 1024.0d / 1024.0d) + " MB/s";
                } else {
                    bps = String.format("%.2f", dspeed / 1024.0d) + " KB/s";
                }
                mLogtf.d("onDownloadSuccess:bps=" + bps + ",downTime=" + downTime);
                StableLogHashMap logHashMap = new StableLogHashMap("ondownloadsuccess");
                logHashMap.put("bps", bps);
                logHashMap.put("downTime", "" + downTime);
                logHashMap.put("times", "" + times.get());
                logHashMap.put("version", "" + StandLiveConfig.version);
                logHashMap.put("downloadsize", "" + downloadSize);
//                Loger.d(activity, eventId, logHashMap.getData(), true);
                UmsAgentManager.umsAgentDebug(ContextManager.getContext(), eventId, logHashMap.getData());
                onProgress(pbLiveStandUpdate.getLeft(), rlLiveStandUpdateProg, ivLiveStandUpdateProgLight, 50);
                LiveZip liveZip = new LiveZip(view, callBack, saveFile, saveFileTemp);
                zipExtractorTask = new StandLiveZipExtractorTask(saveFileZip, saveFileTemp, liveZip);
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
                            logger.d("onDownloadFailed:times=" + times.get() + ",url=" + url);
                            downloadStart = System.currentTimeMillis();
                            baseHttp.downloadRenew(url, tempFileZip, downloadCallBack);
                        }
                    }, 1000);
                } else {
                    XESToastUtils.showToast(activity, "下载失败，请检查您的网络或联络辅导老师");
                    if (liveSoundPool != null) {
                        liveSoundPool.stop(loadTask);
                        liveSoundPool.release();
                        liveSoundPool = null;
                    }
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!cancle) {
                                callBack.onDataSucess("");
                            }
                        }
                    }, 1000);
                }
            }

            @Override
            protected void onDownloading(int progress) {
                if (rlLiveStandUpdateProg.getVisibility() != View.VISIBLE) {
                    rlLiveStandUpdateProg.setVisibility(View.VISIBLE);
                    ivLiveStandUpdateProgLight.setVisibility(View.VISIBLE);
                }
                progress = progress / 2;
                if (pbLiveStandUpdate.getProgress() != progress) {
                    pbLiveStandUpdate.setProgress(progress);
                    tvLiveStandUpdateProg.setText(progress + "%");
                    final int finalProgress = progress;
                    tvLiveStandUpdateProg.post(new Runnable() {
                        @Override
                        public void run() {
                            onProgress(pbLiveStandUpdate.getLeft(), rlLiveStandUpdateProg, ivLiveStandUpdateProgLight, finalProgress);
                        }
                    });
                }
            }
        });
    }

    /**
     * 进度条变化，更新上面的文字和光的位置
     *
     * @param progLeft                   进度条左边
     * @param rlLiveStandUpdateProg      进度条上文字布局
     * @param ivLiveStandUpdateProgLight 光的布局
     * @param progress                   进度
     */
    private void onProgress(int progLeft, RelativeLayout rlLiveStandUpdateProg, ImageView ivLiveStandUpdateProgLight, int progress) {
        int progTipWidth = rlLiveStandUpdateProg.getWidth();
        int lightWidth = ivLiveStandUpdateProgLight.getWidth();
        logger.d("onProgress:progLeft=" + progLeft + ",progTipWidth=" + progTipWidth + ",lightWidth=" + lightWidth);
        int left = (int) (((float) progWidth) * (float) progress / 100.0f);
        logger.d("onProgress:progress=" + progress + ",left=" + left);
        {
            RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) rlLiveStandUpdateProg.getLayoutParams();
//                        int left = pbLiveStandUpdate.getWidth() * progress / 100;
            lp2.leftMargin = left - progTipWidth / 2 + progLeft + progGap - progHeight / 2;
            rlLiveStandUpdateProg.setLayoutParams(lp2);
        }
        {
            RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) ivLiveStandUpdateProgLight.getLayoutParams();
//                        int left = pbLiveStandUpdate.getWidth() * progress / 100;
            lp2.leftMargin = left - lightWidth / 2 + progLeft + progGap - progHeight / 2;
            ivLiveStandUpdateProgLight.setLayoutParams(lp2);
        }
    }

    class LiveZip implements ZipProg {
        AbstractBusinessDataCallBack callBack;
        File saveFile;
        File saveFileTemp;
        ProgressBar pbLiveStandUpdate;
        RelativeLayout rlLiveStandUpdateProg;
        ImageView ivLiveStandUpdateProgLight;
        TextView tvLiveStandUpdateProg;
        boolean cancle = false;
        long startTime;
        int max;

        public LiveZip(View view, AbstractBusinessDataCallBack callBack, File saveFile, File saveFileTemp) {
            pbLiveStandUpdate = view.findViewById(R.id.pb_live_stand_update);
            rlLiveStandUpdateProg = view.findViewById(R.id.rl_live_stand_update_prog);
            ivLiveStandUpdateProgLight = view.findViewById(R.id.iv_live_stand_update_prog_light);
            tvLiveStandUpdateProg = view.findViewById(R.id.tv_live_stand_update_prog);
            this.callBack = callBack;
            this.saveFile = saveFile;
            this.saveFileTemp = saveFileTemp;
            //解压开始，要删除以前旧的
            FileUtils.deleteDir(saveFile);
            FileUtils.deleteDir(saveFileTemp);
            startTime = System.currentTimeMillis();
        }

        @Override
        public void setCancle(boolean cancle) {
            this.cancle = cancle;
        }

        @Override
        public void setMax(int max) {
            this.max = max;
        }

        @Override
        public void onProgressUpdate(Integer... values) {
            if (values.length == 1) {
                if (rlLiveStandUpdateProg.getVisibility() != View.VISIBLE) {
                    rlLiveStandUpdateProg.setVisibility(View.VISIBLE);
                    ivLiveStandUpdateProgLight.setVisibility(View.VISIBLE);
                }
                float progressF = (50 + (float) values[0] * 100f / (float) max / 2);
//                logger.d( "onProgressUpdate:progress=" + ((float) values[0] * 100f / (float) max));
                final int progress = (int) progressF;
                if (pbLiveStandUpdate.getProgress() != progress) {
                    pbLiveStandUpdate.setProgress(progress);
                    tvLiveStandUpdateProg.setText(progress + "%");
                    tvLiveStandUpdateProg.post(new Runnable() {
                        @Override
                        public void run() {
                            onProgress(pbLiveStandUpdate.getLeft(), rlLiveStandUpdateProg, ivLiveStandUpdateProgLight, progress);
                        }
                    });
                }
            }
        }

        @Override
        public void onPostExecute(Exception exception) {
            if (exception == null) {
                int progress = 100;
                int progTipWidth = rlLiveStandUpdateProg.getWidth();
                int lightWidth = ivLiveStandUpdateProgLight.getWidth();
                int left = (int) (((float) progWidth) * (float) progress / 100.0f);
                {
                    RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) rlLiveStandUpdateProg.getLayoutParams();
//                        int left = pbLiveStandUpdate.getWidth() * progress / 100;
                    lp2.leftMargin = left - progTipWidth / 2 + pbLiveStandUpdate.getLeft() + progGap - progHeight / 2;
                    rlLiveStandUpdateProg.setLayoutParams(lp2);
                }
                {
                    RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) ivLiveStandUpdateProgLight.getLayoutParams();
//                        int left = pbLiveStandUpdate.getWidth() * progress / 100;
                    lp2.leftMargin = left - lightWidth / 2 + pbLiveStandUpdate.getLeft() + progGap - progHeight / 2;
                    ivLiveStandUpdateProgLight.setLayoutParams(lp2);
                }
                saveFileTemp.renameTo(saveFile);
                if (liveSoundPool != null) {
                    liveSoundPool.stop(loadTask);
                    liveSoundPool.release();
                    liveSoundPool = null;
                }
                pbLiveStandUpdate.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onDataSucess("");
                    }
                });
                StandLoadLog.zipFileFailSuc(startTime, saveFile);
            } else {
                mLogtf.e("onPostExecute:cancle=" + cancle, exception);
                if (!cancle) {
                    pbLiveStandUpdate.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!cancle) {
                                XESToastUtils.showToast(pbLiveStandUpdate.getContext(), "解压失败，请联络辅导老师");
                                if (liveSoundPool != null) {
                                    liveSoundPool.stop(loadTask);
                                    liveSoundPool.release();
                                    liveSoundPool = null;
                                }
                                pbLiveStandUpdate.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        callBack.onDataSucess("");
                                    }
                                }, 1000);
                            }
                        }
                    });
                }
                StandLoadLog.zipFileFailErr(startTime, exception);
            }
        }
    }

    interface ZipProg {
        void onProgressUpdate(Integer... values);

        void onPostExecute(Exception exception);

        void setMax(int max);

        void setCancle(boolean cancle);
    }

    static class StandLiveZipExtractorTask extends ZipExtractorTask {

        boolean cancle = false;
        ZipProg zipProg;

        public StandLiveZipExtractorTask(File in, File out, ZipProg zipProg) {
            super(in, out, true, null);
            this.zipProg = zipProg;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (cancle) {
                setCancle(true);
                zipProg.setCancle(true);
                logger.d("onProgressUpdate:cancle");
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

    public void onDestroy() {
        cancle = true;
        if (zipExtractorTask != null) {
            logger.d("onDestroy:cancle");
            zipExtractorTask.cancle = true;
        }
        if (liveSoundPool != null && loadTask != null) {
            liveSoundPool.stop(loadTask);
            liveSoundPool.release();
            liveSoundPool = null;
        }
    }
}
