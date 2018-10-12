package com.xueersi.parentsmeeting.modules.livevideo.studyreport.business;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCacheFile;

import java.io.File;

import io.agora.rtc.plugin.rawdata.MediaDataObserverPlugin;
import io.agora.rtc.plugin.rawdata.MediaPreProcessing;

/**
 * 学习报告截图
 *
 * @author 林玉强
 * created  at 2018/10/12
 */
public class StudyReportBll extends LiveBaseBll implements StudyReportAction {
    Handler handler = new Handler(Looper.getMainLooper());
    private LogToFile mLogtf;
    private MediaDataObserverPlugin mediaDataObserverPlugin;

    public StudyReportBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        mLogtf = new LogToFile(activity, TAG);
        putInstance(StudyReportAction.class, this);
    }

    public void onFirstRemoteVideoDecoded(final int uid) {
        boolean load = MediaPreProcessing.isLoad();
        mLogtf.d("onFirstRemoteVideoDecoded:load=" + load);
        if (load) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (mediaDataObserverPlugin == null) {
                        mediaDataObserverPlugin = MediaDataObserverPlugin.the();
                        MediaPreProcessing.setCallback(mediaDataObserverPlugin);
//                        MediaPreProcessing.setVideoCaptureByteBUffer(mediaDataObserverPlugin.byteBufferCapture);
//                        MediaPreProcessing.setAudioRecordByteBUffer(mediaDataObserverPlugin.byteBufferAudioRecord);
//                        MediaPreProcessing.setAudioPlayByteBUffer(mediaDataObserverPlugin.byteBufferAudioPlay);
//                        MediaPreProcessing.setBeforeAudioMixByteBUffer(mediaDataObserverPlugin.byteBufferBeforeAudioMix);
//                        MediaPreProcessing.setAudioMixByteBUffer(mediaDataObserverPlugin.byteBufferAudioMix);
                    }
                    File alldir = LiveCacheFile.geCacheFile(activity, "agora/" + uid);
                    if (!alldir.exists()) {
                        alldir.mkdirs();
                    }
                    File saveFile = new File(alldir, System.currentTimeMillis() + ".jpg");
                    mLogtf.d("onFirstRemoteVideoDecoded:saveFile=" + saveFile);
                    mediaDataObserverPlugin.saveRenderVideoShot(saveFile.getPath(), uid, new MediaDataObserverPlugin.OnRenderVideoShot() {
                        @Override
                        public void onRenderVideoShot(String path) {
                            mLogtf.d("onRenderVideoShot:path=" + path);
                        }
                    });
                }
            });
        }
    }
}
