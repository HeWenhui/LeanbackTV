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

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 直播
 *
 * @author linyuqiang
 */
public class LiveVideoActivity extends LiveVideoActivityBase implements ActivityStatic {

    private String TAG = "LiveVideoActivityLog";

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent event) {

    }

    /**
     * 只在WIFI下使用激活
     *
     * @param onlyWIFIEvent
     * @author zouhao
     * @Create at: 2015-9-24 下午1:57:04
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(AppEvent.OnlyWIFIEvent onlyWIFIEvent) {

    }

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
        liveVideoFragmentBase.onUserBackPressed();
    }

    @Override
    public void onDestroy() {
        AppBll.getInstance().unRegisterAppEvent(this);
        super.onDestroy();
    }

    @Override
    protected LiveVideoFragmentBase getFragment() {
        return new LiveVideoActivity2();
    }

    @Override
    protected void updateIcon() {
        LiveVideoActivity2 liveVideoFragment = (LiveVideoActivity2) liveVideoFragmentBase;
        liveVideoFragment.updateIcon();
    }

    /**
     * 跳转到播放器
     *
     * @param context
     * @param bundle
     * @param requestCode
     */
    public static void intentTo(Activity context, Bundle bundle, int requestCode) {
        Intent intent = new Intent(context, LiveVideoActivity.class);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, requestCode);
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
