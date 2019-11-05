package com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.view.View;

import com.airbnb.lottie.AssertUtil;
import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;

import java.io.IOException;

public abstract class QuestionResultEvenDrivePager extends BasePager {
    private LiveGetInfo getInfo;
    protected static final String ANIM_ROOT_DIR = "contiright_anim";


    public QuestionResultEvenDrivePager(Context context, LiveGetInfo liveGetInfo) {
        super(context);
        this.getInfo = liveGetInfo;
//        this.context = context;
    }

    protected LottieAnimationView scoreLottieView;

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.item_page_livevideo_even_drive_lottie, null);
        scoreLottieView = view.findViewById(R.id.lottie_view_livevideo_even_drvie);
        return view;
    }


    @Override
    public void initData() {

    }

    protected void doShowAnima() {
        scoreLottieView.useHardwareAcceleration();
        scoreLottieView.playAnimation();
        if (scoreLottieView.getVisibility() != View.VISIBLE) {
            scoreLottieView.setVisibility(View.VISIBLE);
        }
    }

    public abstract void showNum(final int num);

    private int primarySchoolDra[] = new int[]{
            R.drawable.bg_livevideo_even_drive_anim_2,
            R.drawable.bg_livevideo_even_drive_anim_3,
            R.drawable.bg_livevideo_even_drive_anim_4,
            R.drawable.bg_livevideo_even_drive_anim_5,
            R.drawable.bg_livevideo_even_drive_anim_6,
            R.drawable.bg_livevideo_even_drive_anim_7,
            R.drawable.bg_livevideo_even_drive_anim_8,
            R.drawable.bg_livevideo_even_drive_anim_9,
            R.drawable.bg_livevideo_even_drive_anim_10,
            R.drawable.bg_livevideo_even_drive_anim_11,
            R.drawable.bg_livevideo_even_drive_anim_12,
            R.drawable.bg_livevideo_even_drive_anim_13,
            R.drawable.bg_livevideo_even_drive_anim_14,
            R.drawable.bg_livevideo_even_drive_anim_15,
            R.drawable.bg_livevideo_even_drive_anim_16,
            R.drawable.bg_livevideo_even_drive_anim_17,
            R.drawable.bg_livevideo_even_drive_anim_18,
            R.drawable.bg_livevideo_even_drive_anim_19,
            R.drawable.bg_livevideo_even_drive_anim_20,
            R.drawable.bg_livevideo_even_drive_anim_21,
            R.drawable.bg_livevideo_even_drive_anim_22,
            R.drawable.bg_livevideo_even_drive_anim_23,
            R.drawable.bg_livevideo_even_drive_anim_24
    };

    protected Drawable getEvenDrive(int num) {
        if (num > 1 && num < 25) {
            return mContext.getResources().getDrawable(primarySchoolDra[num - 2]);
//            return context.getDrawable(primarySchoolDra[num - 2]);
        }
        return null;
    }

    protected Bitmap getEvenDriveBt(Drawable drawable, String lottieFilPath, String lottieFileName) {
        try {
            if (drawable instanceof NinePatchDrawable) {
                NinePatchDrawable npd = (NinePatchDrawable) drawable;
                Bitmap btm = BitmapFactory.decodeStream(AssertUtil.open(lottieFilPath + "images/" + lottieFileName));
                drawable.setBounds(0, 0, btm.getWidth(), btm.getHeight());
                Bitmap output_bitmap = Bitmap.createBitmap(npd.getIntrinsicWidth(), npd.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(output_bitmap);
                npd.draw(canvas);
                return output_bitmap;
            } else {
                if (drawable instanceof BitmapDrawable) {
                    Bitmap btm = BitmapFactory.decodeStream(AssertUtil.open(lottieFilPath + "images/" + lottieFileName));
                    Bitmap bitmap = Bitmap.createBitmap(btm.getWidth(),
                            btm.getHeight(), drawable.getOpacity() != PixelFormat.OPAQUE
                                    ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
                    Canvas canvas = new Canvas(bitmap);
                    drawable.setBounds(0, 0, btm.getWidth(), btm.getHeight());
                    //设置绘画的边界，此处表示完整绘制
                    drawable.draw(canvas);
                    return bitmap;

//                    Bitmap btm = BitmapFactory.decodeStream(AssertUtil.open(lottieFilPath + "images/" + lottieFileName));
//                    int ri = drawable.getIntrinsicWidth();
//                    int bo = drawable.getIntrinsicHeight();
//                    drawable.setBounds(0, 0, SizeUtils.Px2Dp(mContext, btm.getWidth()), SizeUtils.Px2Dp(mContext, btm.getHeight()));
//                    BitmapDrawable bd = (BitmapDrawable) drawable;
//                    Bitmap rBitmap = bd.getBitmap();

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                        rBitmap.setWidth(btm.getWidth());
//                        rBitmap.setHeight(btm.getHeight());
//                    }
//                    btm.createBitmap(btm.getWidth(), btm.getHeight(), Bitmap.Config.ARGB_8888);
//                    return rBitmap;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (scoreLottieView != null) {
            scoreLottieView.cancelAnimation();
        }
    }

    public void rmLottieView() {
        if (scoreLottieView != null && scoreLottieView.getVisibility() != View.INVISIBLE) {
            scoreLottieView.setVisibility(View.INVISIBLE);
        }
    }
}
