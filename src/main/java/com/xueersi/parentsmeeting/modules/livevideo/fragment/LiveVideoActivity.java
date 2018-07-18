package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import com.xueersi.common.business.AppBll;
import com.xueersi.common.event.AppEvent;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity2;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityStatic;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 直播
 *
 * @author linyuqiang
 */
public class LiveVideoActivity extends LiveVideoActivityBase implements ActivityStatic {
    private String TAG = "LiveVideoActivityLog";
    /** 直播类型 */
    private int liveType;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            /** 点击空白位置 隐藏软键盘 */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onUserBackPressed() {
        if (liveVideoFragmentBase != null) {
            liveVideoFragmentBase.onUserBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        AppBll.getInstance().unRegisterAppEvent(this);
        super.onDestroy();
    }

    @Override
    protected LiveVideoFragmentBase getFragment() {
        liveType = getIntent().getIntExtra("type", 0);
        if (liveType == LiveVideoConfig.LIVE_TYPE_LECTURE) {
            return new LectureLiveVideoFrame();
        } else {
            int pattern = getIntent().getIntExtra("pattern", 0);
            if (pattern == 2) {
                return new StandLiveVideoActivity2();
            }
            return new LiveVideoActivity2();
        }
    }

    @Override
    protected void updateIcon() {
        if (liveVideoFragmentBase instanceof LiveVideoActivity2) {
            LiveVideoActivity2 liveVideoFragment = (LiveVideoActivity2) liveVideoFragmentBase;
            liveVideoFragment.updateIcon();
        }
    }

    /**
     * 跳转到播放器
     *
     * @param context
     * @param bundle
     */
    public static void intentTo(Activity context, Bundle bundle) {
        Intent intent = new Intent(context, LiveVideoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    private boolean isResume = true;

    @Override
    public boolean isResume() {
        return isResume;
    }


    @Override
    public void onPause() {
        super.onPause();
        isResume = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isResume = true;
    }
}
