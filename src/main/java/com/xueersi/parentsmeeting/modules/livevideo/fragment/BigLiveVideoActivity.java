package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoFragment;

/**
 * 大班整合直播间入口
 *
 * @author chekun
 * created  at 2019/9/9 16:38
 */
public class BigLiveVideoActivity extends LiveVideoActivity {

    @Override
    protected LiveVideoFragmentBase getFragment() {
        if (isBigLive()) {
            try {
                String fname = "com.xueersi.parentsmeeting.modules.livebusiness.enter.LiveBusinessFragment";
                LiveVideoFragmentBase fragmentBase = (LiveVideoFragmentBase) Fragment.instantiate(this, fname);
                return fragmentBase;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new LiveVideoFragment();
    }

    /**
     * 判断是否 是大班整合直播间
     *
     * @return
     */
    private boolean isBigLive() {
        Bundle bundle = getIntent().getExtras();
        boolean result = bundle != null && bundle.getBoolean("isBigLive", false);
        return result;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }


    /**
     * 跳转到播放器
     *
     * @param context
     * @param bundle
     */
    public static void intentTo(Activity context, Bundle bundle) {
        Intent intent = new Intent(context, BigLiveVideoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void intentToAfterOther(Activity context, Bundle bundle, Intent otherIntent) {
        Intent intent = new Intent(context, BigLiveVideoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        intent.putExtras(bundle);
        if (otherIntent != null) {
            Intent[] intents = new Intent[2];
            intents[0] = otherIntent;
            intents[1] = intent;
            context.startActivities(intents);
        } else {
            context.startActivity(intent);
        }
    }
}
