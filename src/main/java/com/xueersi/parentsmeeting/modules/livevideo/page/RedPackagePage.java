package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FrameAnimation;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.uikit.imageloader.ImageLoader;
import com.xueersi.xesalib.utils.uikit.imageloader.SingleConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lyqai on 2018/3/21.
 */
public class RedPackagePage extends BasePager {
    Context context;
    int operateId;
    RedPackagePageAction redPackageAction;
    View rl_livevideo_redpackage_bg;
    ImageView iv_livevideo_redpackage_bg;
    private ArrayList<FrameAnimation> frameAnimations = new ArrayList<>();
    String userName;
    String headUrl;
    Bitmap headBitmap;
    int clickPackage = 1;

    public RedPackagePage(Context context, int operateId, RedPackagePageAction redPackageAction, String userName, String headUrl) {
        super(context);
        this.operateId = operateId;
        this.redPackageAction = redPackageAction;
        this.headUrl = headUrl;
        this.userName = userName;
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.dialog_live_stand_red_packet_view, null);
        rl_livevideo_redpackage_bg = mView.findViewById(R.id.rl_livevideo_redpackage_bg);
        iv_livevideo_redpackage_bg = mView.findViewById(R.id.iv_livevideo_redpackage_bg);
        mView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                Loger.d(TAG, "onViewDetachedFromWindow");
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                Loger.d(TAG, "onViewDetachedFromWindow:frameAnimations=" + frameAnimations.size());
                for (int i = 0; i < frameAnimations.size(); i++) {
                    FrameAnimation animation = frameAnimations.get(i);
                    animation.destory();
                }
            }
        });
        return mView;
    }

    @Override
    public void initData() {
//        Button btnRedPacket = mView.findViewById(R.id.bt_livevideo_redpackage_cofirm);
//        btnRedPacket.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                redPackageAction.onPackageClick(operateId);
//            }
//        });
//        mView.findViewById(R.id.iv_livevideo_redpackage_close).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                redPackageAction.onPackageClose(operateId);
//            }
//        });
        ImageLoader.with(mContext).load(headUrl).asCircle().asBitmap(new SingleConfig.BitmapListener() {
            @Override
            public void onSuccess(Drawable drawable) {
                headBitmap = ((BitmapDrawable) drawable).getBitmap();
            }

            @Override
            public void onFail() {

            }
        });
//        initEnter2();
    }

    private void initEnter2(final VideoResultEntity entity) {
        final FrameAnimation btframeAnimation1 = createFromAees("Images/redpackage/5_bianshen", false);
        frameAnimations.add(btframeAnimation1);
        btframeAnimation1.setBitmapCreate(new FrameAnimation.BitmapCreate() {
            @Override
            public Bitmap onAnimationCreate(String file) {
                if (file.contains("0017")) {
                    return initHeadAndGold(entity, file);
                }
                return null;
            }
        });
        btframeAnimation1.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                final FrameAnimation btframeAnimation2 = createFromAees("Images/redpackage/4_feichuan", true);
                frameAnimations.add(btframeAnimation2);
                btframeAnimation2.setBitmapCreate(new FrameAnimation.BitmapCreate() {
                    @Override
                    public Bitmap onAnimationCreate(String file) {
                        Loger.d(TAG, "onAnimationCreate:file=" + file);
//                        return headBitmap;
                        return initHeadAndGold(entity, file);
                    }
                });
            }

            @Override
            public void onAnimationRepeat() {

            }
        });
    }

    private Bitmap initHeadAndGold(VideoResultEntity entity, String file) {
        InputStream inputStream = null;
        try {
            inputStream = mContext.getAssets().open(file);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap.setDensity(160);
            Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            canvasBitmap.setDensity(160);
            Canvas canvas = new Canvas(canvasBitmap);
            canvas.drawBitmap(bitmap, 0, 0, null);

            if (headBitmap != null && !headBitmap.isRecycled()) {
                float scaleWidth = 61f / headBitmap.getHeight();
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleWidth);
                Bitmap scalHeadBitmap = Bitmap.createBitmap(headBitmap, 0, 0, headBitmap.getWidth(), headBitmap.getHeight(), matrix, true);
                scalHeadBitmap.setDensity(160);
                float left = (bitmap.getWidth() - scalHeadBitmap.getWidth()) / 2;
                float top = (bitmap.getHeight() - scalHeadBitmap.getHeight()) / 2;
                canvas.drawBitmap(scalHeadBitmap, left + 3f, top + 4, null);

                String gold = "+" + entity.getGoldNum();

                View layout_live_stand_red_mine1 = LayoutInflater.from(mContext).inflate(R.layout.layout_live_stand_red_mine1, null);
                TextView tv_livevideo_redpackage_name = layout_live_stand_red_mine1.findViewById(R.id.tv_livevideo_redpackage_name);
                tv_livevideo_redpackage_name.setText("" + userName);
                TextView tv_livevideo_redpackage_num = layout_live_stand_red_mine1.findViewById(R.id.tv_livevideo_redpackage_num);
                tv_livevideo_redpackage_num.setText(gold);
                layout_live_stand_red_mine1.measure(canvasBitmap.getWidth(), canvasBitmap.getHeight());
                layout_live_stand_red_mine1.layout(0, 0, canvasBitmap.getWidth(), canvasBitmap.getHeight());

                canvas.save();
                canvas.translate((canvasBitmap.getWidth() - layout_live_stand_red_mine1.getMeasuredWidth()) / 2, 160);
                layout_live_stand_red_mine1.draw(canvas);
                canvas.restore();

                scalHeadBitmap.recycle();
                bitmap.recycle();
            }
            return canvasBitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public void initEnter() {
        final FrameAnimation btframeAnimation1 = createFromAees("Images/redpackage/1_kaichang", false);
        frameAnimations.add(btframeAnimation1);
        btframeAnimation1.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                FrameAnimation btframeAnimation2 = null;
                btframeAnimation2 = createFromAees("Images/redpackage/2_xunhuan", true);
                frameAnimations.add(btframeAnimation1);
//                        btMesOpenAnimation.setAnimationListener(null);
                iv_livevideo_redpackage_bg.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btframeAnimation1.destory();
                        frameAnimations.remove(btframeAnimation1);
                    }
                }, 60);
                final AtomicBoolean click = new AtomicBoolean(false);
                final FrameAnimation finalBtframeAnimation = btframeAnimation2;
                iv_livevideo_redpackage_bg.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (click.get()) {
                            return;
                        }
                        iv_livevideo_redpackage_bg.setOnClickListener(null);
                        if (finalBtframeAnimation != null) {
                            finalBtframeAnimation.pauseAnimation();
                        }
                        final FrameAnimation btframeAnimation1 = createFromAees("Images/redpackage/6_suoxiao", false);
                        frameAnimations.add(btframeAnimation1);
                        btframeAnimation1.setAnimationListener(new FrameAnimation.AnimationListener() {
                            FrameAnimation btframeAnimation1;

                            @Override
                            public void onAnimationStart() {
                                redPackageAction.onPackageRight(operateId);
                                iv_livevideo_redpackage_bg.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Loger.d(TAG, "onPackageClick:operateId=" + operateId + "," + view.getTop() + "," + view);
                                        iv_livevideo_redpackage_bg.setOnClickListener(null);
                                        if (btframeAnimation1 != null) {
                                            btframeAnimation1.pauseAnimation();
                                        }
                                        clickPackage = 2;
                                        redPackageAction.onPackageClick(operateId);
                                    }
                                });
                            }

                            @Override
                            public void onAnimationEnd() {
                                rl_livevideo_redpackage_bg.setVisibility(View.GONE);
//                                        initEnter2();
                                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) iv_livevideo_redpackage_bg.getLayoutParams();
                                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                lp.rightMargin = 40;
                                iv_livevideo_redpackage_bg.setLayoutParams(lp);
                                btframeAnimation1 = createFromAees("Images/redpackage/7_xunhuan_xiao", true);
                                frameAnimations.add(btframeAnimation1);
                            }

                            @Override
                            public void onAnimationRepeat() {

                            }
                        });
                    }
                }, 3400);
                iv_livevideo_redpackage_bg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        click.set(true);
                        iv_livevideo_redpackage_bg.setOnClickListener(null);
                        if (finalBtframeAnimation != null) {
                            finalBtframeAnimation.pauseAnimation();
                            iv_livevideo_redpackage_bg.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finalBtframeAnimation.destory();
                                }
                            }, 60);
                        }
                        FrameAnimation btframeAnimation3 = createFromAees("Images/redpackage/3_feichu", false);
                        frameAnimations.add(btframeAnimation3);
                        btframeAnimation3.setAnimationListener(new FrameAnimation.AnimationListener() {
                            @Override
                            public void onAnimationStart() {

                            }

                            @Override
                            public void onAnimationEnd() {
                                clickPackage = 1;
                                redPackageAction.onPackageClick(operateId);
//                                rl_livevideo_redpackage_bg.setBackgroundColor(Color.TRANSPARENT);
//                                redPackageAction.onPackageClose(operateId);
                            }

                            @Override
                            public void onAnimationRepeat() {

                            }
                        });
                    }
                });
            }

            @Override
            public void onAnimationRepeat() {

            }
        });
    }

    public void onGetPackage(VideoResultEntity entity) {
//        if (clickPackage == 1) {
//            FrameAnimation btframeAnimation3 = createFromAees("Images/redpackage/3_feichu", false);
//            frameAnimations.add(btframeAnimation3);
//        } else {
//            initEnter2(entity);
//        }
        initEnter2(entity);
    }

    public void onOtherPackage() {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) iv_livevideo_redpackage_bg.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.bottomMargin = 100;
        iv_livevideo_redpackage_bg.setLayoutParams(lp);
    }

    public FrameAnimation createFromAees(String path, boolean isRepeat) {
        return FrameAnimation.createFromAees(mContext, iv_livevideo_redpackage_bg, path, 50, isRepeat);
    }

    public interface RedPackagePageAction {
        void onPackageClick(int operateId);

        void onPackageClose(int operateId);

        void onPackageRight(int operateId);
    }
}
