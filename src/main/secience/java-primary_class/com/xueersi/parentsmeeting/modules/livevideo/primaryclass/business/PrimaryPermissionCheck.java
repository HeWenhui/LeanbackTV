package com.xueersi.parentsmeeting.modules.livevideo.primaryclass.business;

import android.app.Activity;
import android.content.Context;

import com.xueersi.common.permission.PermissionItem;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;

import java.util.ArrayList;
import java.util.List;

public class PrimaryPermissionCheck {

    public static int getStatus(Activity activity, final OnPermissionFinish onPermissionFinish) {
        final List<PermissionItem> unList = new ArrayList<>();
        List<PermissionItem> unList2 = XesPermission.checkPermissionUnPerList(activity, new
                LiveActivityPermissionCallback() {
                    boolean onDeny = false;

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onDeny(String permission, int position) {
                        if (unList.size() > 0) {
                            unList.remove(0);
                            onDeny = true;
                        }
                        if (unList.isEmpty()) {
                            onPermissionFinish.onFinish(false);
                        }
                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                        // bugly 16271 TODO 正常情况这地方不会空的
                        if (unList.size() > 0) {
                            unList.remove(0);
                        }
                        if (unList.isEmpty()) {
                            if (onDeny) {
                                onPermissionFinish.onFinish(false);
                            } else {
                                onPermissionFinish.onFinish(true);
                            }
                        }
                    }
                }, PermissionConfig.PERMISSION_CODE_CAMERA, PermissionConfig.PERMISSION_CODE_AUDIO);
        unList.addAll(unList2);
        return unList2.isEmpty() ? 1 : 0;
    }

    public interface OnPermissionFinish {
        void onFinish(boolean allOk);
    }

}
