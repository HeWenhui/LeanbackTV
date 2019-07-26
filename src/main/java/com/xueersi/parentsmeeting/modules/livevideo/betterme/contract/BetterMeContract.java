package com.xueersi.parentsmeeting.modules.livevideo.betterme.contract;

import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.Contract.BasePresenter;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.Contract.BaseView;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.AimRealTimeValEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.BetterMeEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.StuAimResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.entity.StuSegmentEntity;

/**
 * Created by ZhangYuansun on 2018/9/14
 */

public interface BetterMeContract {

    interface BetterMeView extends BaseView<BetterMePresenter> {
        void setRootView(RelativeLayout rootView);

        void showIntroductionPager(boolean showPK);

        void showLevelDisplayPager();

        void showReceiveTargetPager(boolean showPK);

        void showCompleteTargetPager(StuAimResultEntity stuAimResultEntity);
    }

    interface BetterMePresenter extends BasePresenter {
        void getStuSegment(int method);
        void getBetterMe(int method);
        void getStuAimResult();
        void updateBetterMe(boolean isShowBubble);
        void getBetterMeAndPkMiddlePage();
        BetterMeEntity getBetterMeEntity();
        StuSegmentEntity getStuSegmentEntity();
    }
}
