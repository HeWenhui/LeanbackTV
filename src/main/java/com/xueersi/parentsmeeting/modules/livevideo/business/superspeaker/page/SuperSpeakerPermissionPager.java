package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.page;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.ISuperSpeakerContract;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class SuperSpeakerPermissionPager extends SuperSpeakerCameraPager {

    public SuperSpeakerPermissionPager(Context context, ISuperSpeakerContract.ISuperSpeakerBridge bridge, String liveId, String courseWareId, int answerTime, int recordTime, int back) {
        super(context, bridge, liveId, courseWareId, answerTime, recordTime, back);
    }

    @Override
    protected void performStartRecordVideo() {
        if (isHasRecordPermission()) {
            super.performStartRecordVideo();
        } else {
            boolean have = XesPermission.checkPermission(mContext, new LiveActivityPermissionCallback() {
                        @Override
                        public void onFinish() {

                        }

                        @Override
                        public void onDeny(String permission, int position) {
                            if (position == 0) {
                                UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715018));
                            } else {
                                UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715020));
                            }
                        }

                        @Override
                        public void onGuarantee(String permission, int position) {
                            if (position == 0) {
                                UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715019));
                                performStartPreView(isFacingBack);
                            } else if (position == 1) {
                                UmsAgentManager.umsAgentCustomerBusiness(mContext, mContext.getResources().getString(R.string.livevideo_1715021));
                            }
                            logger.i("permission = " + permission + " position = " + position);

                        }
                    },
                    PermissionConfig.PERMISSION_CODE_CAMERA, PermissionConfig.PERMISSION_CODE_AUDIO);

        }
    }

    /**
     * 是否有相机和语音权限
     *
     * @return
     */
    @Override
    protected boolean isHasRecordPermission() {
        PackageManager pkm = mContext.getPackageManager();
        boolean isDefault = (PackageManager.PERMISSION_GRANTED == pkm.checkPermission("android.permission.MODIFY_AUDIO_SETTINGS", mContext.getPackageName())
                && PackageManager.PERMISSION_GRANTED == pkm.checkPermission("android.permission.RECORD_AUDIO", mContext.getPackageName())
                && PackageManager.PERMISSION_GRANTED == pkm.checkPermission("android.permission.CAMERA", mContext.getPackageName()));
        logger.i("isDefault " + isDefault);
        return isDefault;
    }
}
