package com.xueersi.parentsmeeting.modules.livevideo.enteampk.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveProvide;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveEventBus;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.event.ClassEndEvent;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;

public class ClassEndReg implements LiveProvide {
    private LogToFile logToFile;
    public LiveGetInfo liveGetInfo;
    private Context context;
    private Handler handler = LiveMainHandler.getMainHandler();
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
