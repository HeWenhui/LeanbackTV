package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view;

import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;

public class IntelligentRecognitionPermissionPager extends BaseIntelligentRecognitionPager {

    public IntelligentRecognitionPermissionPager(FragmentActivity context) {
        super(context);
    }

    protected boolean checkPermission() {
        PackageManager pkm = mContext.getPackageManager();
        boolean isDefault = (PackageManager.PERMISSION_GRANTED ==
                pkm.checkPermission("android.permission.MODIFY_AUDIO_SETTINGS", mContext.getPackageName())
                && PackageManager.PERMISSION_GRANTED ==
                pkm.checkPermission("android.permission.RECORD_AUDIO", mContext.getPackageName()));
        logger.i("isDefault " + isDefault);
        return isDefault;
    }

    @Override
    protected void performOpenViewStart() {
        if (checkPermission()) {
            super.performOpenViewStart();
        } else {
            if (settingViewGroup != null && settingViewGroup.getVisibility() != View.VISIBLE) {
                settingViewGroup.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void performStartWaveLottie() {
        if (checkPermission()) {
            super.performStartWaveLottie();
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        settingViewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean have = XesPermission.checkPermission(mActivity, new LiveActivityPermissionCallback() {

                            @Override
                            public void onFinish() {

                            }

                            @Override
                            public void onDeny(String permission, int position) {

                            }

                            @Override
                            public void onGuarantee(String permission, int position) {

                            }
                        },
                        PermissionConfig.PERMISSION_CODE_AUDIO);

            }
        });
    }
}
