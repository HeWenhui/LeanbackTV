package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.tencent.bugly.crashreport.CrashReport;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveException;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;

public class PrimaryRelativeLayout extends RelativeLayout {
    private String TAG = "PrimaryRelativeLayout";
    private Logger logger = LiveLoggerFactory.getLogger(TAG);

    public PrimaryRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        logger.d("setVisibility:visibility=" + visibility);
        if (visibility != VISIBLE) {
            CrashReport.postCatchedException(new LiveException(TAG, new Exception()));
        }
    }
}
