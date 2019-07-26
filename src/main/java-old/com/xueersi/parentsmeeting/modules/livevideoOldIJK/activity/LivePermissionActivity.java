package com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xueersi.common.permission.PermissionActivity;

/**
 * Created by linyuqiang on 2018/8/2.
 * 直播的权限
 */
public class LivePermissionActivity extends PermissionActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
