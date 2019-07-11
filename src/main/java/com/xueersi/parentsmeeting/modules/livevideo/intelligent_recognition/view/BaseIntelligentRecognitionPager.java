package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view;

import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.parentsmeeting.module.videoplayer.config.MediaPlayer;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoLoadActivity;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;

public class BaseIntelligentRecognitionPager extends IntelligentRecognitionPager {
    public BaseIntelligentRecognitionPager(FragmentActivity context) {
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
                boolean have = XesPermission.checkPermission(this, new LiveActivityPermissionCallback() {

                            @Override
                            public void onFinish() {

                            }

                            @Override
                            public void onDeny(String permission, int position) {

                            }

                            @Override
                            public void onGuarantee(String permission, int position) {
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (MediaPlayer.getIsNewIJK()) {
                                            com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle);
                                        } else {
                                            com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.LiveVideoActivity.intentTo(LiveVideoLoadActivity.this, bundle);
                                        }
                                    }
                                });
                            }
                        },
                        PermissionConfig.PERMISSION_CODE_AUDIO);
                if (have) {

                }
            }
        });
    }
}
