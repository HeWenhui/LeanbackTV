package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.content_view;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.airbnb.lottie.AssertUtil;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.util.FontCache;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;

import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants.INTELLIGENT_LOTTIE_PATH;

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
                                settingViewGroup.setVisibility(View.GONE);
                            }
                        },
                        PermissionConfig.PERMISSION_CODE_AUDIO);

            }
        });
    }

    /**
     * 更新火焰数量图片
     *
     * @param fireNum
     * @return
     */
    @Override
    protected Bitmap creatFireBitmap(String fireNum, String lottieId) {
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(AssertUtil.open(INTELLIGENT_LOTTIE_PATH + "images/" + lottieId));
            Bitmap creatBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setTextSize(bitmap.getHeight());
            paint.setColor(0xFFFFE376);
            Typeface fontFace = FontCache.getTypeface(mContext, "fangzhengcuyuan.ttf");
            paint.setTypeface(fontFace);
            canvas.drawText("+" + fireNum, 0, bitmap.getHeight(), paint);
            bitmap.recycle();
            bitmap = creatBitmap;
            return bitmap;
        } catch (Exception e) {
            logger.e("creatFireBitmap", e);
        }
        return null;
    }

    private void addPager() {

    }
}
