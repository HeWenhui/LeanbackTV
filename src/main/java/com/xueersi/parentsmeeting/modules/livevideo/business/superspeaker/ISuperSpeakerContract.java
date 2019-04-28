package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker;

import android.view.View;

public interface ISuperSpeakerContract {

    int RECORD_TIME = 1000;

    interface IRecordManager {
        void removeRedPackageView();
    }

    interface ICameraView {
        View initView();

        void updateNum(String num);

        View getView();

        void timeUp();
    }

    interface ICameraPresenter {
        /**
         * 是否是强制提交
         *
         * @param isForce 1：是 2：否
         */
        void submitSpeechShow(String isForce);
    }

    interface IRedPackageView {
        /**
         * 更新金币余额
         *
         * @param num 剩下的金币余额
         */
        void updateNum(String num);

        View getView();
    }

    interface ICommonTip {
        /**
         * 时间到了，结束视频录制
         */
        void timeUp(boolean complete);
    }

    interface ICommonPresenter extends ICameraPresenter {
        void removeView(View view);
    }
//    interface ICameraView{
//
//    }
}
