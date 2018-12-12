package com.xueersi.parentsmeeting.modules.livevideo.betterme.contract;

import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.Contract.BasePresenter;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.Contract.BaseView;

/**
 * Created by ZhangYuansun on 2018/9/14
 */

public interface BetterMeContract {

    interface BetterMeView extends BaseView<BetterMePresenter> {
        void setRootView(RelativeLayout rootView);
        void showIntroductionPager();
        void showLevelDisplayPager();
        void showReceiveTargetPager();
        void showCompleteTargetPager();
    }

    interface BetterMePresenter extends BasePresenter {

    }
}
