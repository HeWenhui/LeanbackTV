package com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.text.TextUtils;
import android.view.View;

import com.airbnb.lottie.AssertUtil;
import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.base.BasePager;
import com.xueersi.common.util.FontCache;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;

import java.io.IOException;

import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.IntelligentConstants.INTELLIGENT_LOTTIE_PATH;
import static com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.EvenDriveConstants.EVEN_DRIVE_FAT_AHEAD_LOTTIE_PATH;
import static com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.EvenDriveConstants.EVEN_DRIVE_KING_LOTTIE_PATH;
import static com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.EvenDriveConstants.EVEN_DRIVE_LEVEL1_LOTTIE_PATH;
import static com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.EvenDriveConstants.EVEN_DRIVE_LEVEL2_LOTTIE_PATH;
import static com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.EvenDriveConstants.EVEN_DRIVE_LEVEL3_LOTTIE_PATH;
import static com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.EvenDriveConstants.EVEN_DRIVE_LEVEL4_LOTTIE_PATH;
import static com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.EvenDriveConstants.EVEN_DRIVE_SHARP_LOTTIE_PATH;
import static com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.EvenDriveConstants.EVEN_DRIVE_TOP_LOTTIE_PATH;
import static com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.EvenDriveConstants.EVEN_DRIVE_UNSTOPPABLE_LOTTIE_PATH;

public class QuestionResultEvenDrivePager extends BasePager {
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
//        int contiRihts = arguments.getInt("contiRights");

    }

    private boolean isPrimarySchool() {
        return getInfo != null && (getInfo.getSmallEnglish() ||
                getInfo.isPrimaryChinese() || getInfo.getIsPrimarySchool() == 1 || getInfo.getUseSkin() == 2);
    }

    private void showPriAnimaNum(final int num) {
        String resPath = EVEN_DRIVE_TOP_LOTTIE_PATH + "images";
        String jsonPath = EVEN_DRIVE_TOP_LOTTIE_PATH + "data.json";
        if (num >= 2 && num <= 3) {//锋芒毕露
            resPath = EVEN_DRIVE_SHARP_LOTTIE_PATH + "images";
            jsonPath = EVEN_DRIVE_SHARP_LOTTIE_PATH + "data.json";
        } else if (num >= 4 && num <= 5) {
            //无人能挡
            resPath = EVEN_DRIVE_UNSTOPPABLE_LOTTIE_PATH + "images";
            jsonPath = EVEN_DRIVE_UNSTOPPABLE_LOTTIE_PATH + "data.json";
        } else if (num >= 6 && num <= 7) {
            //遥遥领先
            resPath = EVEN_DRIVE_FAT_AHEAD_LOTTIE_PATH + "images";
            jsonPath = EVEN_DRIVE_FAT_AHEAD_LOTTIE_PATH + "data.json";
        } else if (num >= 8 && num <= 24) {
            resPath = EVEN_DRIVE_KING_LOTTIE_PATH + "images";
            jsonPath = EVEN_DRIVE_KING_LOTTIE_PATH + "data.json";
        }

        final LottieEffectInfo effectInfo = new LottieEffectInfo(resPath, jsonPath);
        scoreLottieView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mContext), resPath);
        //替换json资源文件
        ImageAssetDelegate delegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset asset) {
                String resPath = EVEN_DRIVE_TOP_LOTTIE_PATH + "images",
                        jsonPath = EVEN_DRIVE_TOP_LOTTIE_PATH + "data.json";
                if (num > 1 && num < 25) {
                    if (num >= 2 && num <= 3) {//锋芒毕露
                        resPath = EVEN_DRIVE_SHARP_LOTTIE_PATH + "images";
                        jsonPath = EVEN_DRIVE_SHARP_LOTTIE_PATH + "data.json";
                        if (("img_8.png").equals(asset.getFileName())) {
                            return getEvenDriveBt(getEvenDrive(num), EVEN_DRIVE_SHARP_LOTTIE_PATH, "img_8.png");
                        }
                    } else if (num >= 4 && num <= 5) {
                        //无人能挡
                        resPath = EVEN_DRIVE_UNSTOPPABLE_LOTTIE_PATH + "images";
                        jsonPath = EVEN_DRIVE_UNSTOPPABLE_LOTTIE_PATH + "data.json";
                        if (("img_3.png").equals(asset.getFileName())) {
                            return getEvenDriveBt(getEvenDrive(num), EVEN_DRIVE_UNSTOPPABLE_LOTTIE_PATH, "img_3.png");
                        }
                    } else if (num >= 6 && num <= 7) {
                        //遥遥领先
                        resPath = EVEN_DRIVE_FAT_AHEAD_LOTTIE_PATH + "images";
                        jsonPath = EVEN_DRIVE_FAT_AHEAD_LOTTIE_PATH + "data.json";
                        if (("img_0.png").equals(asset.getFileName())) {
                            return getEvenDriveBt(getEvenDrive(num), EVEN_DRIVE_FAT_AHEAD_LOTTIE_PATH, "img_0.png");
                        }
                    } else if (num >= 8 && num <= 24) {
                        resPath = EVEN_DRIVE_KING_LOTTIE_PATH + "images";
                        jsonPath = EVEN_DRIVE_KING_LOTTIE_PATH + "data.json";
                        if (("img_3.png").equals(asset.getFileName())) {
                            return getEvenDriveBt(getEvenDrive(num), EVEN_DRIVE_KING_LOTTIE_PATH, "img_3.png");
                        }
                    }
                } else if (num >= 25) {
                    resPath = EVEN_DRIVE_TOP_LOTTIE_PATH + "images";
                    jsonPath = EVEN_DRIVE_TOP_LOTTIE_PATH + "data.json";
                }
                LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo(resPath, jsonPath);
                return bubbleEffectInfo.fetchBitmapFromAssets(
                        scoreLottieView,
                        asset.getFileName(),
                        asset.getId(),
                        asset.getWidth(),
                        asset.getHeight(),
                        mContext);
            }
        };
        scoreLottieView.setImageAssetDelegate(delegate);
        doShowAnima();
    }

    private void showMiddleNum(final int num) {

        String replaceFileName = ANIM_ROOT_DIR + "/public/live_business_contiright_" + num + ".png";

        String resPath = EVEN_DRIVE_LEVEL1_LOTTIE_PATH + "images",
                jsonPath = EVEN_DRIVE_LEVEL1_LOTTIE_PATH + "data.json";
        int animLevel = 0;

        if (num >= 2 && num <= 3) {//锋芒毕露
            resPath = EVEN_DRIVE_LEVEL1_LOTTIE_PATH + "images";
            jsonPath = EVEN_DRIVE_LEVEL1_LOTTIE_PATH + "data.json";
            animLevel = 1;
        } else if (num >= 4 && num <= 5) {
            //无人能挡
            resPath = EVEN_DRIVE_LEVEL2_LOTTIE_PATH + "images";
            jsonPath = EVEN_DRIVE_LEVEL2_LOTTIE_PATH + "data.json";
            animLevel = 2;
        } else if (num >= 6 && num <= 7) {
            //遥遥领先
            resPath = EVEN_DRIVE_LEVEL3_LOTTIE_PATH + "images";
            jsonPath = EVEN_DRIVE_LEVEL3_LOTTIE_PATH + "data.json";
            animLevel = 3;
        } else if (num >= 8 && num <= 24) {
            resPath = EVEN_DRIVE_LEVEL4_LOTTIE_PATH + "images";
            jsonPath = EVEN_DRIVE_LEVEL4_LOTTIE_PATH + "data.json";
            animLevel = 4;
        }

        String targetFileName = getTargetFileName(animLevel);
        ContiRightEffectInfo effectInfo = null;
        if (!TextUtils.isEmpty(targetFileName)) {
            effectInfo = new ContiRightEffectInfo(resPath, jsonPath, targetFileName);
        } else {
            effectInfo = new ContiRightEffectInfo(resPath, jsonPath);
        }

        effectInfo.setReplaceFileName(replaceFileName);
//        xesLottieAnimView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mContext));
//        final LottieEffectInfo effectInfo = new LottieEffectInfo(resPath, jsonPath);
        scoreLottieView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mContext), null);

//        scoreLottieView.setImageAssetDelegate(delegate);
        final ContiRightEffectInfo finalEffectInfo = effectInfo;
        scoreLottieView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return finalEffectInfo.fetchBitmapFromAssets(scoreLottieView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                        mContext);
            }
        });
        scoreLottieView.setMaxFrame(90);
        doShowAnima();
    }

    private String getTargetFileName(int animLevel) {
        String fileName = null;
        switch (animLevel) {
            case 1:
            case 2:
                fileName = "img_46.png";
                break;
            case 3:
            case 4:
                fileName = "img_6.png";
                break;
            default:
                break;
        }
        return fileName;

    }

    protected void doShowAnima() {
        scoreLottieView.useHardwareAcceleration();
        scoreLottieView.playAnimation();
        if (scoreLottieView.getVisibility() != View.VISIBLE) {
            scoreLottieView.setVisibility(View.VISIBLE);
        }
    }

    public void showNum(final int num) {
        if (num >= 2) {
            if (isPrimarySchool()) {
                showPriAnimaNum(num);
            } else {
                showMiddleNum(num);
            }
        }
    }


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

    private int middleSchoolDra[] = new int[]{

    };
//    private Bitmap getEvenDriveBp(int num){
//        mContext.getResources().getDrawable()
//        if (num > 1 && num < 25) {
//            return mContext.getResources().getDrawable(primarySchoolDra[num - 2]);
////            return context.getDrawable(primarySchoolDra[num - 2]);
//        }
//        return null;
//    }

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

    protected Bitmap creatFireBitmap(String fireNum, String lottieFileName, int color) {
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(AssertUtil.open(INTELLIGENT_LOTTIE_PATH + "images/" + lottieFileName));
            Bitmap creatBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setTextSize(bitmap.getHeight());
            paint.setColor(color);
            Typeface fontFace = FontCache.getTypeface(mContext, "fangzhengcuyuan.ttf");
            paint.setTypeface(fontFace);
            if (fireNum != null) {
                if (fireNum.length() == 2) {
                    fireNum = " " + fireNum;
                } else if (fireNum.length() == 1) {
                    fireNum = "   " + fireNum;
                }
            }
            canvas.drawText(fireNum, 0, bitmap.getHeight(), paint);
            bitmap.recycle();
            bitmap = creatBitmap;
            return bitmap;
        } catch (Exception e) {
            logger.e("creatFireBitmap", e);
        }
        return null;
    }

    public void rmLottieView() {
        if (scoreLottieView != null && scoreLottieView.getVisibility() != View.INVISIBLE) {
            scoreLottieView.setVisibility(View.INVISIBLE);
        }
    }
}
