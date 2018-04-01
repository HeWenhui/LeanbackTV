package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FrameAnimation;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.uikit.imageloader.ImageLoader;
import com.xueersi.xesalib.utils.uikit.imageloader.SingleConfig;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lyqai on 2018/3/21.
 */
public class RedPackagePage extends BasePager {
    Context context;
    int operateId;
    RedPackagePageAction redPackageAction;
    ImageView iv_livevideo_redpackage_bg;
    private ArrayList<FrameAnimation> frameAnimations = new ArrayList<>();
    String headUrl;
    Bitmap headBitmap;

    public RedPackagePage(Context context, int operateId, RedPackagePageAction redPackageAction, String headUrl) {
        super(context);
        this.operateId = operateId;
        this.redPackageAction = redPackageAction;
        this.headUrl = headUrl;
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.dialog_live_stand_red_packet_view, null);
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

    private void initEnter2() {
        final FrameAnimation btframeAnimation1 = createFromAees("Images/redpackage/5_bianshen", false);
        frameAnimations.add(btframeAnimation1);
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
                        return headBitmap;
                    }
                });
            }

            @Override
            public void onAnimationRepeat() {

            }
        });
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
                                iv_livevideo_redpackage_bg.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        iv_livevideo_redpackage_bg.setOnClickListener(null);
                                        if (btframeAnimation1 != null) {
                                            btframeAnimation1.pauseAnimation();
                                        }
                                        initEnter2();
                                    }
                                });
                            }

                            @Override
                            public void onAnimationEnd() {
//                                        initEnter2();
                                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) iv_livevideo_redpackage_bg.getLayoutParams();
                                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                                            lp.width = 200;
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
//                            redPackageAction.onPackageClick(operateId);
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
                                iv_livevideo_redpackage_bg.setBackgroundColor(Color.TRANSPARENT);
                                redPackageAction.onPackageClose(operateId);
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

    public FrameAnimation createFromAees(String path, boolean isRepeat) {
        return FrameAnimation.createFromAees(mContext, iv_livevideo_redpackage_bg, path, 50, isRepeat);
    }

    public interface RedPackagePageAction {
        void onPackageClick(int operateId);

        void onPackageClose(int operateId);
    }
}
