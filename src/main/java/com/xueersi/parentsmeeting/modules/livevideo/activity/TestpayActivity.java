package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.os.Bundle;

import com.xueersi.parentsmeeting.base.XesActivity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.event.MiniEvent;

import org.greenrobot.eventbus.EventBus;

import floatwindow.xishuang.float_lib.FloatWindowManager;

/**
 * Created by Administrator on 2018/3/29.
 */
/* 测试画中画效果，待删除的测试类*/
public class TestpayActivity extends XesActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testpay);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭悬浮窗
        FloatWindowManager.hide();
        EventBus.getDefault().post(new MiniEvent("Back"));
    }
}
