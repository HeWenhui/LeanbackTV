package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;

/**
 * 战队pk 二期 点赞UI
 *
 * @author chekun
 * created  at 2019/1/30 15:57
 */
public class TeamPkPraiseLayout extends FrameLayout {

    private RecyclerView recyclerView;
    private ImageView ivPraise;
    private LottieAnimationView loopAnimationView;
    /**
     * lottie 资源根路径
     **/
    private static final String ANIM_RES_DIR = "team_pk/praise/";
    private LottieAnimationView clickAnimView;


    public TeamPkPraiseLayout(@NonNull Context context) {
        this(context, null);
    }

    public TeamPkPraiseLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TeamPkPraiseLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.teampk_praise_layout, this);
        recyclerView = findViewById(R.id.rcl_teampk_praise);
        ivPraise = findViewById(R.id.iv_teampk_praise);
        loopAnimationView = findViewById(R.id.lav_teampk_praise);
        clickAnimView = findViewById(R.id.lav_teampk_praise_click);
        ivPraise.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playClickAnim();
            }
        });
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getMeasuredWidth() > 0) {
                    playLoopAnim();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            }
        });
    }

    private void playClickAnim() {
        loopAnimationView.setVisibility(GONE);
        loopAnimationView.cancelAnimation();
        loopAnimationView.destroyDrawingCache();
        Log.e("TeamPkPraiseLayout","======> playClickAnim:"+clickAnimView.getComposition());
        if(clickAnimView.getComposition() == null){
            String lottieResPath = ANIM_RES_DIR + "click/images";
            String lottieJsonPath = ANIM_RES_DIR + "click/data.json";
            final LottieEffectInfo effectInfo = new LottieEffectInfo(lottieResPath, lottieJsonPath);
            clickAnimView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(getContext()));
            clickAnimView.useHardwareAcceleration(true);
            clickAnimView.setRepeatCount(0);
            clickAnimView.setImageAssetDelegate(new ImageAssetDelegate() {
                @Override
                public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                    return effectInfo.fetchBitmapFromAssets(clickAnimView, lottieImageAsset.getFileName(),
                            lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                            getContext());
                }
            });
        }
        if(!clickAnimView.isAnimating()){
            clickAnimView.playAnimation();
        }
    }

    private void playLoopAnim() {
        String lottieResPath = ANIM_RES_DIR + "loop/images";
        String lottieJsonPath = ANIM_RES_DIR + "loop/data.json";
        final LottieEffectInfo effectInfo = new LottieEffectInfo(lottieResPath, lottieJsonPath);
        loopAnimationView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(getContext()));
        loopAnimationView.useHardwareAcceleration(true);
        loopAnimationView.setRepeatCount(-1);
        loopAnimationView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return effectInfo.fetchBitmapFromAssets(loopAnimationView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                        getContext());
            }
        });
        loopAnimationView.playAnimation();
    }
}
