package com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.Contract;

import android.widget.RelativeLayout;

import com.xueersi.common.http.HttpCallBack;

/**
 * Created by ZhangYuansun on 2018/9/14
 */

public interface EnglishSpeechBulletContract {

    interface EnglishSpeechBulletView extends BaseView<EnglishSpeechBulletPresenter> {
        /**
         * 展示语音弹幕
         * @param rootView 父布局
         */
        void showSpeechBullet(RelativeLayout rootView);
        /**
         * 关闭语音弹幕
         * @param hasTip 是否弹关闭提示
         */
        void closeSpeechBullet(boolean hasTip);
    }

    interface EnglishSpeechBulletPresenter extends BasePresenter {
        /**
         * 发送语音弹幕
         */
        void sendDanmakuMessage(String msg);
        /**
         * 上传发言语句
         */
        void uploadSpeechBulletScreen(String msg, HttpCallBack requestCallBack);
        /**
         * 获取头像url
         */
        String getHeadImgUrl();
        /**
         * 获取语音弹幕场次ID
         */
        String getVoiceId();
    }
}
