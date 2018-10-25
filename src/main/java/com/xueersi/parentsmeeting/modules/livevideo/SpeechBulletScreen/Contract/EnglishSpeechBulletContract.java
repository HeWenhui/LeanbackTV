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
        /**
         * 弹幕消息
         * @param name 名字
         * @param msg 内容
         * @param headImgUrl 头像Url
         * @param rootView 父布局
         */
        void receiveDanmakuMsg(String name, String msg,String headImgUrl, RelativeLayout rootView);
        /**
         * 表扬消息
         * @param msg 内容
         */
        void receivePraiseMsg(String msg);
    }

    interface EnglishSpeechBulletPresenter extends BasePresenter {
        /**
         * 上传弹幕消息
         */
        void uploadSpeechBulletScreen(String msg, HttpCallBack requestCallBack);
        /**
         * 获取头像url
         */
        String getHeadImgUrl();
    }
}
