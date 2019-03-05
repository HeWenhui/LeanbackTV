package com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.Contract;

import android.widget.RelativeLayout;

import com.xueersi.common.http.HttpCallBack;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;

public interface ScienceSpeechBullletContract {

    interface ScienceSpeechBulletView extends BaseView<ScienceSpeechBullletContract.ScienceSpeechBulletPresenter> {
        /**
         * 展示语音弹幕
         *
         * @param rootView 父布局
         */
        void showSpeechBullet(RelativeLayout rootView);

        /**
         * 关闭语音弹幕
         *
         * @param hasTip 是否弹关闭提示
         */
        void closeSpeechBullet(boolean hasTip);

        /**
         * 弹幕消息
         *
         * @param name       名字
         * @param msg        内容
         * @param headImgUrl 头像Url
         * @param isGuset    true:别人发的 false:自己发的
         * @param rootView   父布局
         */
        void receiveDanmakuMsg(String name, String msg, String headImgUrl, boolean isGuset, RelativeLayout rootView);
        /**
         * 表扬消息
         *
         * @param msg 内容
         */
        void receivePraiseMsg(String msg);

        void setVideoLayout(LiveVideoPoint liveVideoPoint);
        void onStop();
    }

    public interface ScienceSpeechBulletPresenter extends BasePresenter {
        /**
         * 上传弹幕消息
         */
        void uploadSpeechBulletScreen(String msg, HttpCallBack requestCallBack);
        void sendDanmakuMessage(String msg);
        /**
         * 获取voiceId
         */
        String getVoiceId();

        /**
         * 头像网络地址
         */
        String getHeadImgUrl();

        /**
         * 学生性别
         */
        String getStuSex();
    }
}
