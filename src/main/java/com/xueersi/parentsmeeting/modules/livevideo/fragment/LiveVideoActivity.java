package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.xueersi.common.business.AppBll;
import com.xueersi.common.http.HttpCall;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoFragment;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityStatic;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.fragment.halfbody.HalfBodyLiveVideoFragement;

/**
 * 直播
 *
 * @author linyuqiang
 */
public class LiveVideoActivity extends LiveVideoActivityBase implements ActivityStatic {
    private String TAG = "LiveVideoActivityLog";

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
    public <T extends View> T findViewById(int id) {
        T t = super.findViewById(id);
        if (t == null) {
            if (liveVideoFragmentBase != null) {
                t = liveVideoFragmentBase.getContentView().findViewById(id);
            }
        }
        return t;
    }

    @Override
    public void onDestroy() {
        AppBll.getInstance().unRegisterAppEvent(this);
        super.onDestroy();
//        System.exit(0);
    }

    @Override
    protected LiveVideoFragmentBase getFragment() {
        int pattern = getIntent().getIntExtra("pattern", 0);
        if (pattern == LiveVideoConfig.LIVE_PATTERN_2) {
            return new StandLiveVideoFragment();
        } else if (pattern == LiveVideoConfig.LIVE_TYPE_HALFBODY) {
            //半身直播
            return new HalfBodyLiveVideoFragement();
        } else if (pattern == LiveVideoConfig.LIVE_TYPE_HALFBODY_CLASS) {
            //半身直播
            return (LiveVideoFragmentBase) Fragment.instantiate(this,"com.xueersi.parentsmeeting.modules.livevideo.fragment.PrimaryClassVideoFragment");
        } else {
            return new LiveVideoFragment();
        }
    }

    @Override
    protected void updateIcon() {
        if (liveVideoFragmentBase instanceof LiveFragmentBase) {
            LiveFragmentBase liveVideoFragment = (LiveFragmentBase) liveVideoFragmentBase;
            liveVideoFragment.updateIcon();
        }
    }

    @Override
    public void addHttpRequest(HttpCall httpCall) {
        //去掉
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

    @Override
    public void finish() {
        super.finish();
        UmsAgentManager.umsAgentDebug(this, TAG + "finish", "finish:" + Log.getStackTraceString(new Exception()));
    }

    @Override
    public void finish(int result) {
        super.finish(result);
        UmsAgentManager.umsAgentDebug(this, TAG + "finish", "finish(result):" + Log.getStackTraceString(new Exception()));

    }
}
