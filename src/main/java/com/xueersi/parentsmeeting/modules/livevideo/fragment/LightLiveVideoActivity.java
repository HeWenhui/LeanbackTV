package com.xueersi.parentsmeeting.modules.livevideo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.fragment
 * @ClassName: LightLiveVideoActivity
 * @Description: java类作用描述
 * @Author: WangDe
 * @CreateDate: 2019/11/28 20:12
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/28 20:12
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class LightLiveVideoActivity extends LiveVideoActivity {

    LiveVideoFragmentBase videoFragmentBase;

    @Override
    protected LiveVideoFragmentBase getFragment() {

        videoFragmentBase = new LightLiveVideoFragment();
        return videoFragmentBase;
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
        Intent intent = new Intent(context, LightLiveVideoActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void onResume() {
        LiveVideoConfig.isLightLive = true;
        super.onResume();
    }

    @Override
    public void onDestroy() {
        LiveVideoConfig.isLightLive = false;
        super.onDestroy();
    }
}
