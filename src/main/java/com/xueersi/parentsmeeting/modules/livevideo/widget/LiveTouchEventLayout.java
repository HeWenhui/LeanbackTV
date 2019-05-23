package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;


/**
* 事件监听
*@author chenkun
*created 2019/4/19 下午3:44
*version 1.0
*/
public class LiveTouchEventLayout extends LinearLayout {
    String TAG = "LiveTouchEventLayout";
    DispatchTouchEventListener mDisPatchTouchEventListener;
    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    public LiveTouchEventLayout(Context context) {
        super(context);
    }

    public LiveTouchEventLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LiveTouchEventLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(mDisPatchTouchEventListener != null){
            mDisPatchTouchEventListener.onDispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }


    public void setDisPatchTouchEventListener(DispatchTouchEventListener disPatchTouchEventListener) {
        this.mDisPatchTouchEventListener = disPatchTouchEventListener;
    }



    /**
     * 事件分发监听器
     */
    public static interface DispatchTouchEventListener{

        /**
         * 分发回调
         * @param ev
         */
        void onDispatchTouchEvent(MotionEvent ev);

    }

}
