package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FrameAnimation;
import com.xueersi.xesalib.utils.log.Loger;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by lyqai on 2018/3/21.
 */
public class RedPackagePage extends BasePager {
    Context context;
    int operateId;
    RedPackagePageAction redPackageAction;
    ImageView iv_livevideo_redpackage_bg;
    private ArrayList<FrameAnimation> frameAnimations = new ArrayList<>();

    public RedPackagePage(Context context, int operateId, RedPackagePageAction redPackageAction) {
        super(context);
        this.operateId = operateId;
        this.redPackageAction = redPackageAction;
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.dialog_red_packet_view, null);
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
        Button btnRedPacket = mView.findViewById(R.id.bt_livevideo_redpackage_cofirm);
        btnRedPacket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redPackageAction.onPackageClick(operateId);
            }
        });
        mView.findViewById(R.id.iv_livevideo_redpackage_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redPackageAction.onPackageClose(operateId);
            }
        });
        initEnter();
    }

    private void initEnter() {
        try {
            String[] files = mContext.getAssets().list("Images/redpackage/1_kaichang");
            for (int i = 0; i < files.length; i++) {
                files[i] = "Images/redpackage/1_kaichang/" + files[i];
            }
            final FrameAnimation btframeAnimation1 = new FrameAnimation(iv_livevideo_redpackage_bg, files, 50, false);
            frameAnimations.add(btframeAnimation1);
            btframeAnimation1.setAnimationListener(new FrameAnimation.AnimationListener() {
                @Override
                public void onAnimationStart() {

                }

                @Override
                public void onAnimationEnd() {
                    FrameAnimation btframeAnimation2 = null;
                    try {
                        String[] files = mContext.getAssets().list("Images/redpackage/2_xunhuan");
                        for (int i = 0; i < files.length; i++) {
                            files[i] = "Images/redpackage/2_xunhuan/" + files[i];
                        }
                        btframeAnimation2 = new FrameAnimation(iv_livevideo_redpackage_bg, files, 50, true);
                        frameAnimations.add(btframeAnimation1);
//                        btMesOpenAnimation.setAnimationListener(null);
                        iv_livevideo_redpackage_bg.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                btframeAnimation1.destory();
                                frameAnimations.remove(btframeAnimation1);
                            }
                        }, 60);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    final FrameAnimation finalBtframeAnimation = btframeAnimation2;
                    iv_livevideo_redpackage_bg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            redPackageAction.onPackageClick(operateId);
                            if (finalBtframeAnimation != null) {
                                finalBtframeAnimation.pauseAnimation();
                                iv_livevideo_redpackage_bg.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finalBtframeAnimation.destory();
                                    }
                                }, 60);
                            }
                            try {
                                String[] files = mContext.getAssets().list("Images/redpackage/3_feichu");
                                for (int i = 0; i < files.length; i++) {
                                    files[i] = "Images/redpackage/3_feichu/" + files[i];
                                }
                                FrameAnimation btframeAnimation3 = new FrameAnimation(iv_livevideo_redpackage_bg, files, 50, false);
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
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void onAnimationRepeat() {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface RedPackagePageAction {
        void onPackageClick(int operateId);

        void onPackageClose(int operateId);
    }
}
