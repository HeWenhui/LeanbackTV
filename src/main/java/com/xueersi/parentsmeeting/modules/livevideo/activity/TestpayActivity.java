package com.xueersi.parentsmeeting.modules.livevideo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

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
    private BroadcastReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testpay);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.leidong.action.MyReceiver");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("Duncan", "BroadcastService接收到了广播");
                finish();
            }
        };
        registerReceiver(receiver, intentFilter);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭悬浮窗
        FloatWindowManager.hide();
        EventBus.getDefault().post(new MiniEvent("Back"));
        unregisterReceiver(receiver);
    }
}
