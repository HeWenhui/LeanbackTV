package com.xueersi.parentsmeeting.modules.livevideo.message.pager;

import android.content.Context;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.ui.adapter.CommonAdapter;

public class SmallChineseLiveMessagePager extends BaseSmallChineseLiveMessagePager {

    public SmallChineseLiveMessagePager(Context context) {
        super(context);
        giftPager = new SmallChineseSendGiftPager(context);
    }

    /** 小学语文送花的pager */
    private SmallChineseSendGiftPager giftPager;

    @Override
    public void closeChat(boolean close) {

    }

    @Override
    public boolean isCloseChat() {
        return false;
    }

    @Override
    public void addMessage(String sender, int type, String text, String headUrl) {

    }

    @Override
    public CommonAdapter<LiveMessageEntity> getMessageAdapter() {
        return null;
    }

    @Override
    public void setOtherMessageAdapter(CommonAdapter<LiveMessageEntity> otherMessageAdapter) {

    }

    @Override
    public View initView() {
        return null;
    }

    @Override
    public void onStartConnect() {

    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onRegister() {

    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onUserList(String channel, User[] users) {

    }

    @Override
    public void onMessage(String target, String sender, String login, String hostname, String text, String headurl) {

    }

    @Override
    public void onPrivateMessage(boolean isSelf, String sender, String login, String hostname, String target, String message) {

    }

    @Override
    public void onJoin(String target, String sender, String login, String hostname) {

    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {

    }

    @Override
    public void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {

    }

    @Override
    public void onDisable(boolean disable, boolean fromNotice) {

    }

    @Override
    public void onopenchat(boolean openchat, String mode, boolean fromNotice) {

    }

    @Override
    public void onOpenbarrage(boolean openbarrage, boolean fromNotice) {

    }

    @Override
    public void onFDOpenbarrage(boolean open, boolean b) {

    }

    @Override
    public void onTeacherModeChange(String oldMode, String mode, boolean isShowNoticeTips, boolean iszjlkOpenbarrage, boolean isFDLKOpenbarrage) {

    }
}
