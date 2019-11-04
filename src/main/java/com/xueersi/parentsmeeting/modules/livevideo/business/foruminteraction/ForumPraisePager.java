package com.xueersi.parentsmeeting.modules.livevideo.business.foruminteraction;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.business.foruminteraction
 * @ClassName: ForumPraisePager
 * @Description: java类作用描述
 * @Author: WangDe
 * @CreateDate: 2019/10/30 20:35
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/10/30 20:35
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class ForumPraisePager extends LiveBasePager {

    private boolean isAnimStart;
    private ViewGroup decorView;
    private View praiseRootView;
    private LottieAnimationView animView;
    private String mResPath;
    private String mJsonFilePath;
    private String mAnimScriptCacheKey;
    /**
     * lottie动效资源路径
     **/
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "team_pk/teacher_praise/";
    private static final String LOTTIE_JSON_PRAISE = "teacherPraise";
    public ForumPraisePager(Context context) {
        super(context);
        initData();
    }

    @Override
    public View initView() {
        praiseRootView = View.inflate(mContext, R.layout.teampk_teacher_praise_layout, null);
        animView = praiseRootView.findViewById(R.id.lav_teacher_priase);
        return praiseRootView;
    }

    @Override
    public void initData() {
        super.initData();
//        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams
//                .MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT);
//        decorView.addView(praiseRootView, lp);
        animView.setVisibility(View.VISIBLE);
        startAnim();
    }

    private void startAnim() {
        mResPath = "team_pk/pkresult/teacher_praise/images";
        mJsonFilePath = "team_pk/pkresult/teacher_praise/data.json";
        mAnimScriptCacheKey = LOTTIE_JSON_PRAISE;
        animView.useHardwareAcceleration(true);
        final LottieEffectInfo effectInfo = new LottieEffectInfo(mResPath, mJsonFilePath);
        animView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mContext), mAnimScriptCacheKey);
        animView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return effectInfo.fetchBitmapFromAssets(animView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                        mContext);
            }
        });
        animView.playAnimation();
        animView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                closeTeacherPriase();
            }
        });
    }


    /**
     * 关闭表扬UI
     */
    private void closeTeacherPriase() {
        isAnimStart = false;
        onPagerClose.onClose(this);
    }
}
