package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.pager;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.airbnb.lottie.AssertUtil;
import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xrs.bury.xrsbury.XrsBury;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.mvp.IRedPackageView;
import com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.mvp.ReceiveGold;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;

public class LightLiveRedPackageView extends LiveBasePager implements IRedPackageView {
    private LiveViewAction liveViewAction;
    private ImageView ivBusiRedPackClose;
    private LottieAnimationView lvBusiRedPackLight;
    /** 红包领取页 */
    private LottieAnimationView lvBusiRedPackRed;
    /** 红包结果页，直接用领取页替换动画。会缺失帧数 */
    private LottieAnimationView lvBusiRedPackRedResult;
    private boolean small = false;
    private Handler mHandler = LiveMainHandler.getMainHandler();
    private ReceiveGold receiveGold;
    private int operateId;

    public LightLiveRedPackageView(Context context, int interactionId) {
        super(context, false);
        this.operateId = interactionId;
        mView = initView();
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext,R.layout.lightlive_business_red_pkg_layout,null);
        ivBusiRedPackClose = view.findViewById(R.id.iv_lightlive_busi_red_pack_close);
        lvBusiRedPackLight = view.findViewById(R.id.lv_lightlive_busi_red_pack_light);
        lvBusiRedPackRed = view.findViewById(R.id.lv_lightlive_busi_red_pack_red);
        lvBusiRedPackRedResult = view.findViewById(R.id.lv_lightlive_busi_red_pack_red_res);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return view;
    }

    @Override
    public void setReceiveGold(ReceiveGold receiveGold) {
        this.receiveGold = receiveGold;
    }

    @Override
    public void initData() {
        super.initData();
        {
            String LOTTIE_RES_ASSETS_ROOTDIR = "red_packager_light/";
            String resPath = LOTTIE_RES_ASSETS_ROOTDIR + "images";
            String jsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "data.json";
            final LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo(resPath, jsonPath);
            lvBusiRedPackLight.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(mContext), "red_packager_light");
            lvBusiRedPackLight.useHardwareAcceleration(true);
            ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
                @Override
                public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                    String fileName = lottieImageAsset.getFileName();
                    Bitmap bitmap = bubbleEffectInfo.fetchBitmapFromAssets(lvBusiRedPackLight, fileName,
                            lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
                    return bitmap;
                }
            };
            lvBusiRedPackLight.setImageAssetDelegate(imageAssetDelegate);
            int repeatMode = lvBusiRedPackLight.getRepeatMode();
            logger.d("initData:repeatMode=" + repeatMode);
            lvBusiRedPackLight.playAnimation();
        }
        {
            String LOTTIE_RES_ASSETS_ROOTDIR = "red_packager_enter/";
            String resPath = LOTTIE_RES_ASSETS_ROOTDIR + "images";
            String jsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "data.json";
            final LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo(resPath, jsonPath);
            lvBusiRedPackRed.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(mContext), "red_packager_enter");
            lvBusiRedPackRed.useHardwareAcceleration(true);
            ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
                @Override
                public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                    String fileName = lottieImageAsset.getFileName();
                    Bitmap bitmap = bubbleEffectInfo.fetchBitmapFromAssets(lvBusiRedPackRed, fileName,
                            lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
                    return bitmap;
                }
            };
            lvBusiRedPackRed.setImageAssetDelegate(imageAssetDelegate);
            int repeatMode = lvBusiRedPackRed.getRepeatMode();
            logger.d("initData:repeatMode=" + repeatMode);
            lvBusiRedPackRed.playAnimation();
        }
        lvBusiRedPackRed.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                lvBusiRedPackRed.getViewTreeObserver().removeOnPreDrawListener(this);
//                mHandler.postDelayed(runnable, 10000);
//                int scale = ScreenUtils.getScreenWidth() / 759;
//                int width = 315 * scale;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivBusiRedPackClose.getLayoutParams();
//                params.leftMargin = (ScreenUtils.getScreenWidth() + width) / 2;
                params.addRule(RelativeLayout.ALIGN_TOP,R.id.lv_lightlive_busi_red_pack_red);
                params.addRule(RelativeLayout.ALIGN_RIGHT,R.id.lv_lightlive_busi_red_pack_red);
//                params.rightMargin = SizeUtils.Dp2Px(mContext,-7);
                LayoutParamsUtil.setViewLayoutParams(ivBusiRedPackClose, params);
                return false;
            }
            });
    }

    @Override
    public void initListener() {
        super.initListener();
        lvBusiRedPackRed.setOnClickListener(onClickListener);
        ivBusiRedPackClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XrsBury.clickBury(mContext.getResources().getString(R.string.click_03_63_003));
                cancleAnim();
                mView.setOnClickListener(null);
                mView.setClickable(false);
                onPagerClose.onClose(LightLiveRedPackageView.this);
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
//            lvBusiRedPackRed.setOnClickListener(null);
            receiveGold.sendReceiveGold(operateId, new ReceiveGold.OnRedPackageSend() {

                @Override
                public void onReceiveGold(int gold) {
                    initResult("red_packager_succ/", gold);
                }

                @Override
                public void onReceiveFail() {
//                    lvBusiRedPackRed.setOnClickListener(onClickListener);
                }

                @Override
                public void onReceiveError(int errStatus, String failMsg, int code) {
//                    lvBusiRedPackRed.setOnClickListener(onClickListener);
                }

                @Override
                public void onHaveReceiveGold() {
                    initResult("red_packager_error/", -1);
                }
            });
//            if (AppConfig.DEBUG) {
//                initResult("red_packager_error/", -1);
//            }
        }
    };



    private void cancleAnim() {
//        mHandler.removeCallbacks(runnable);
        if (translateValueAnimator != null) {
            translateValueAnimator.cancel();
        }
    }

//    private Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            ivBusiRedPackClose.setVisibility(View.GONE);
//            mView.setBackgroundColor(Color.TRANSPARENT);
//            mView.setOnClickListener(null);
//            mView.setClickable(false);
//            lvBusiRedPackLight.setVisibility(View.GONE);
//            small = true;
////            Line();
//            animBezier();
//        }
//    };

    private ValueAnimator translateValueAnimator;

//    private void animBezier() {
//        final Point startPoint = new Point(lvBusiRedPackRed.getX(), lvBusiRedPackRed.getY());
//        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
//        final int endWidth = SizeUtils.Dp2Px(mContext, 90);
//        final int endHeight = SizeUtils.Dp2Px(mContext, 90);
//        final Point endPoint = new Point(ScreenUtils.getScreenWidth() - liveVideoPoint.getRightMargin() - endWidth, (ScreenUtils.getScreenHeight() - endWidth) / 2);
//        translateValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
//        translateValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
//        translateValueAnimator.setDuration(600);
//        Point controlPoint = new Point(startPoint.x + (endPoint.x - startPoint.x) / 2, lvBusiRedPackRed.getY() - 30);
//        final BezierEvaluator bezierEvaluator = new BezierEvaluator(controlPoint);
//        final int statWidth = lvBusiRedPackRed.getWidth();
//        final int statHeight = lvBusiRedPackRed.getHeight();
//        logger.d("runnable:statWidth=" + statWidth);
//        translateValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float fraction = animation.getAnimatedFraction();
//                Point currentPoint = bezierEvaluator.evaluate(animation.getAnimatedFraction(), startPoint, endPoint);
//                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) lvBusiRedPackRed.getLayoutParams();
//                params.removeRule(RelativeLayout.CENTER_IN_PARENT);
//                params.topMargin = (int) currentPoint.getY();
//                params.leftMargin = (int) currentPoint.getX();
//                params.width = (int) (statWidth + (endWidth - statWidth) * fraction);
//                params.height = (int) (statHeight + (endHeight - statHeight) * fraction);
//                logger.d("onAnimationUpdate:fraction=" + fraction + ",leftMargin=" + params.leftMargin + ",width=" + params.width);
//                LayoutParamsUtil.setViewLayoutParams(lvBusiRedPackRed, params);
//            }
//        });
//        translateValueAnimator.start();
//    }

    private void initResult(final String dir, final int gold) {
        ivBusiRedPackClose.setVisibility(View.VISIBLE);
        mView.setBackgroundColor(mContext.getResources().getColor(R.color.COLOR_80000000));
        if (small) {
            lvBusiRedPackLight.setVisibility(View.VISIBLE);
            lvBusiRedPackLight.playAnimation();
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) lvBusiRedPackRed.getLayoutParams();
//            params.addRule(RelativeLayout.CENTER_IN_PARENT);
//            params.topMargin = 0;
//            params.leftMargin = 0;
//            params.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
//            params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
//            LayoutParamsUtil.setViewLayoutParams(lvBusiRedPackRed, params);
        } else {
            cancleAnim();
        }
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lvBusiRedPackRed.setVisibility(View.GONE);
            }
        }, 40);
        lvBusiRedPackRedResult.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                lvBusiRedPackRedResult.getViewTreeObserver().removeOnPreDrawListener(this);
//                mHandler.postDelayed(runnable, 10000);
//                int scale = ScreenUtils.getScreenWidth() / 759;
//                int width = 315 * scale;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivBusiRedPackClose.getLayoutParams();
//                params.leftMargin = (ScreenUtils.getScreenWidth() + width) / 2;
                params.addRule(RelativeLayout.ALIGN_TOP,R.id.lv_lightlive_busi_red_pack_red_res);
                params.addRule(RelativeLayout.ALIGN_RIGHT,R.id.lv_lightlive_busi_red_pack_red_res);
//                params.rightMargin = SizeUtils.Dp2Px(mContext,-7);
                LayoutParamsUtil.setViewLayoutParams(ivBusiRedPackClose, params);
                return false;
            }
        });
        lvBusiRedPackRedResult.setVisibility(View.VISIBLE);
        String resPath = dir + "images";
        String jsonPath = dir + "data.json";
        final LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo(resPath, jsonPath);
        lvBusiRedPackRedResult.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(mContext), dir);
        lvBusiRedPackRedResult.useHardwareAcceleration(true);
        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                String fileName = lottieImageAsset.getFileName();
                if (gold >= 0 && lottieImageAsset.getId().equals("image_15")) {
                    return creatGoldBitmap(dir, gold, lottieImageAsset.getFileName());
                }
                Bitmap bitmap = bubbleEffectInfo.fetchBitmapFromAssets(lvBusiRedPackRedResult, fileName,
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(), mContext);
                return bitmap;
            }
        };
        lvBusiRedPackRedResult.setImageAssetDelegate(imageAssetDelegate);
        int repeatMode = lvBusiRedPackRedResult.getRepeatMode();
        logger.d("initSuccess:repeatMode=" + repeatMode + ",duration=" + lvBusiRedPackRedResult.getDuration());
        lvBusiRedPackRedResult.playAnimation();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                onPagerClose.onClose(LightLiveRedPackageView.this);
            }
        }, 3000);
//        lvBusiRedPackRed.setOnClickListener(null);
    }

    int[] numRes = {R.drawable.liveideo_hongbao_0, R.drawable.liveideo_hongbao_1, R.drawable.liveideo_hongbao_2, R.drawable.liveideo_hongbao_3, R.drawable.liveideo_hongbao_4,
            R.drawable.liveideo_hongbao_5, R.drawable.liveideo_hongbao_6, R.drawable.liveideo_hongbao_7, R.drawable.liveideo_hongbao_8, R.drawable.liveideo_hongbao_9};

    /**
     * 更新金币数量图片
     *
     * @param gold
     * @param lottieId
     * @return
     */
    public Bitmap creatGoldBitmap(String dir, int gold, String lottieId) {
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(AssertUtil.open(dir + "images/" + lottieId));
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Bitmap creatBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);
            View view = LayoutInflater.from(mContext).inflate(R.layout.lightlive_layout_red_pack_gold, null);
            LinearLayout ll_live_busi_red_pack_gold = view.findViewById(R.id.ll_lightlive_busi_red_pack_gold);
//            ll_live_busi_red_pack_gold.removeAllViews();
            for (int i = 0; i < ("" + gold).length(); i++) {
                char c = ("" + gold).charAt(i);
                ImageView imageView = new ImageView(mContext);
                int res = -1;
                if (c - '0' < numRes.length) {
                    res = numRes[c - '0'];
                }
                if (res != -1) {
                    Bitmap bitmap1 = BitmapFactory.decodeResource(mContext.getResources(), res);
                    imageView.setImageResource(res);
                    int imgWidth = bitmap1.getWidth() * 90 / bitmap1.getHeight();
                    ll_live_busi_red_pack_gold.addView(imageView, imgWidth, 90);
                }
            }

            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            view.measure(widthMeasureSpec, heightMeasureSpec);
            view.layout(0, 0, width, height);
            view.draw(canvas);

            bitmap.recycle();
            bitmap = creatBitmap;
            return bitmap;
        } catch (Exception e) {
            logger.e("creatGoldBitmap", e);
        }
        return null;
    }

    @Override
    public void onDismiss() {
        onPagerClose.onClose(LightLiveRedPackageView.this);
    }
}
