package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.TeamPKBll;
import com.xueersi.parentsmeeting.modules.livevideo.widget.CoinAwardDisplayer;
import com.xueersi.xesalib.utils.log.Loger;

/**
 * Created by chenkun on 2018/4/12
 * 战队 pk 结果页
 */
public class TeamPkResultPager extends BasePager{
    private static final String TAG = "TeamPkResultPager";
    private LottieAnimationView lottieAnimationView;
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "team_pk/pkresult/";
    private final TeamPKBll mTeamPkBll;
    private static final int ANIM_TYPE_PRIASE = 1;       //老师点赞动画
    private static final int ANIM_TYPE_PK_ADVERSARY = 2; //pk 对手动画
    private static final int ANIM_TYPE_PK_REUSLT = 3;    //pk 结果


    public TeamPkResultPager(Context context, TeamPKBll pkBll){
        super(context);
        mTeamPkBll = pkBll;
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_teampk_pkresult, null);
        lottieAnimationView = view.findViewById(R.id.lav_teampk_pkresult_praise);
        showTeacherRraise();
        return view;
    }

    /**
     * 展示老师点赞
     */
    public void showTeacherRraise() {
        String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "teacher_praise/images";
        String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR +"teacher_praise/data.json";
        lottieAnimationView.setImageAssetsFolder(lottieResPath);
        LottieComposition.Factory.fromAssetFileName(mContext, lottieJsonPath, new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                lottieAnimationView.setComposition(lottieComposition);
                lottieAnimationView.resumeAnimation();
            }
        });

      //  lottieAnimationView.addAnimatorListener();
        lottieAnimationView.addAnimatorListener(new PkAnimListener(ANIM_TYPE_PRIASE));

    }

    private class PkAnimListener implements Animator.AnimatorListener{
        private int animType;
        PkAnimListener(int animType){
            this.animType = animType;
        }
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            switch (animType) {
                case ANIM_TYPE_PRIASE:
                    removeLottieView();
                    break;
                case ANIM_TYPE_PK_ADVERSARY:
                    break;
                case  ANIM_TYPE_PK_REUSLT:
                    break;
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    private void removeLottieView() {
        lottieAnimationView.cancelAnimation();
        lottieAnimationView.removeAllAnimatorListeners();
        lottieAnimationView.setVisibility(View.GONE);
    }


    @Override
    public void initData() {
        Loger.e(TAG,"======> initData called");
    }
}
