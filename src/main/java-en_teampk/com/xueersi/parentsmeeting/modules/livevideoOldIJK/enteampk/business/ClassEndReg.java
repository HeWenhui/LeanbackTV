package com.xueersi.parentsmeeting.modules.livevideoOldIJK.enteampk.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.core.LiveEventBus;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.enteampk.event.ClassEndEvent;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.util.ProxUtil;

public class ClassEndReg {
    private LogToFile logToFile;
    public LiveGetInfo liveGetInfo;
    private Context context;
    private Handler handler = new Handler(Looper.getMainLooper());
    private EndRunnable endRunnable;

    ClassEndReg(Context context, LiveGetInfo liveGetInfo) {
        logToFile = new LogToFile(context, "ClassEndReg");
        this.liveGetInfo = liveGetInfo;
        this.context = context;
        ProxUtil.getProxUtil().put(context, ClassEndReg.class, this);
        endRunnable = new EndRunnable();
        long delayMillis = -1;
        long now = System.currentTimeMillis() / 1000;
        long eTime = liveGetInfo.geteTime();
        if (now < eTime) {
            delayMillis = (eTime - now) * 1000;
        }
        logToFile.d("ClassEndReg:delayMillis=" + delayMillis);
        if (delayMillis > 0) {
            handler.postDelayed(endRunnable, delayMillis * 1000);
        }
    }

    class EndRunnable implements Runnable {

        @Override
        public void run() {
            logToFile.d("onEnd");
            onEnd();
        }
    }

    public void destory() {
        handler.removeCallbacks(endRunnable);
    }

    private void onEnd() {
        LiveEventBus.getDefault(context).post(new ClassEndEvent());
    }
}
