/*
Yaaic - Yet Another Android IRC Client

Copyright 2009-2013 Sebastian Kaspari
Copyright 2012 Daniel E. Moctezuma <democtezuma@gmail.com>

This file is part of Yaaic.

Yaaic is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Yaaic is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Yaaic.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.NickAlreadyInUseException;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.PircBot;
import com.xueersi.parentsmeeting.modules.livevideo.business.irc.jibble.pircbot.User;

import java.io.IOException;
import java.util.Vector;

/**
 * The class that actually handles the connection to an IRC server
 *
 * @author linyuqiang
 */
public class IRCConnection extends PircBot {
    private static final String TAG = "IRCConnection";
    private IRCCallback mIRCCallback;
    /** 是不是用户主动调退出方法 */
    private boolean mIsQuitting = false;

    /**
     * Create a new connection
     */
    public IRCConnection(Vector<String> privMsg) {
        this.privMsg = privMsg;
    }

    public void setCallback(IRCCallback callback) {
        this.mIRCCallback = callback;
    }

    public IRCCallback getCallback() {
        return this.mIRCCallback;
    }

    /**
     * This method handles events when any line of text arrives from the server.
     * <p/>
     * We are intercepting this method call for logging the IRC traffic if this
     * debug option is set.
     */
    @Override
    protected void handleLine(String line) throws NickAlreadyInUseException, IOException {
        super.handleLine(line);
        // Log.d(TAG, "handleLine:line=" + line);
    }

    /**
     * Set the nickname of the user
     *
     * @param nickname The nickname to use
     */
    public void setNickname(String nickname) {
        this.setName(nickname);
    }

    /**
     * Set the real name of the user
     *
     * @param realname The realname to use
     */
    public void setRealName(String realname) {
        // XXX: Pircbot uses the version for "real name" and "version".
        // The real "version" value is provided by onVersion()
        this.setVersion(realname);
    }

    @Override
    protected void onStartConnect() {
        if (mIRCCallback != null) {
            mIRCCallback.onStartConnect();
        }
    }

    /**
     * On connect
     */
    @Override
    public void onConnect() {
        logger.d("onConnect");
        if (mIRCCallback != null) {
            mIRCCallback.onConnect(this);
        }
    }

    /**
     * On register
     */
    @Override
    public void onRegister() {
        // Call parent method to ensure "register" status is tracked
        super.onRegister();
        if (mIRCCallback != null) {
            mIRCCallback.onRegister();
        }
    }

    /**
     * On channel action
     */
    @Override
    protected void onAction(String sender, String login, String hostname, String target, String action) {
    }

    /**
     * On Channel Info
     */
    @Override
    protected void onChannelInfo(String channel, int userCount, String topic) {
        if (mIRCCallback != null) {
            mIRCCallback.onChannelInfo(channel, userCount, topic);
        }
    }

    /**
     * On Deop
     */
    @Override
    protected void onDeop(String target, String sourceNick, String sourceLogin, String sourceHostname, String recipient) {
    }

    /**
     * On DeVoice
     */
    @Override
    protected void onDeVoice(String target, String sourceNick, String sourceLogin, String sourceHostname,
                             String recipient) {
    }

    /**
     * On Invite
     */
    @Override
    protected void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname,
                            String target) {
    }

    @Override
    protected void onUserList(String channel, User[] users) {
        if (mIRCCallback != null) {
            mIRCCallback.onUserList(channel, users);
        }
    }

    /**
     * On Join
     */
    @Override
    protected void onJoin(String target, String sender, String login, String hostname) {
        if (mIRCCallback != null) {
            mIRCCallback.onJoin(target, sender, login, hostname);
        }
    }

    @Override
    protected void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason, String channel) {
        if (mIRCCallback != null) {
            mIRCCallback.onQuit(sourceNick, sourceLogin, sourceHostname, reason, "");
        }
    }

    /**
     * On Kick
     */
    @Override
    protected void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname,
                          String recipientNick, String reason) {
        if (mIRCCallback != null) {
            mIRCCallback.onKick(target, kickerNick, kickerLogin, kickerHostname, recipientNick, reason);
        }
    }

    /**
     * On Message
     */
    @Override
    protected void onMessage(String target, String sender, String login, String hostname, String text) {
        if (mIRCCallback != null) {
            mIRCCallback.onMessage(target, sender, login, hostname, text);
        }
    }

    @Override
    protected void onPrivateMessage(String sender, String login, String hostname, String target, String message) {
        if (mIRCCallback != null) {
            mIRCCallback.onPrivateMessage(false, sender, login, hostname, target, message);
        }
    }

    @Override
    protected void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice, String channel) {
        if (mIRCCallback != null) {
            logger.i("sourceNick = " + sourceNick + ",sourceLogin=" + sourceLogin + ",sourceHostname=" + sourceHostname + ",notice=" + notice + ",channel=" + channel);
            mIRCCallback.onNotice(sourceNick, sourceLogin, sourceHostname, target, notice, channel);
        }
    }

    protected void onTopic(String channel, String topic) {
        if (mIRCCallback != null) {
            mIRCCallback.onTopic(channel, topic, "", 0, false, channel);
        }
    }

    @Override
    protected void onTopic(String channel, String topic, String setBy, long date, boolean changed, String channelId) {
        if (mIRCCallback != null) {
            logger.i("channel = " + channel + " topic = " + topic + " setBy" + setBy + " date" + date + " changed = " + changed + " channelId" + channelId);
            mIRCCallback.onTopic(channel, topic, setBy, date, changed, channelId);
        }
    }

    long before = 0;

    @Override
    protected void onServerPing(String response) {
        super.onServerPing(response);
        before = System.currentTimeMillis();
    }

    @Override
    protected void onPing(String sourceNick, String sourceLogin, String sourceHostname, String target, String pingValue) {
        super.onPing(sourceNick, sourceLogin, sourceHostname, target, pingValue);
    }

    @Override
    protected void onUnknown(String line) {
        super.onUnknown(line);
        if (mIRCCallback != null) {
            mIRCCallback.onUnknown(line);
        }
    }

    /**
     * On disconnect
     */
    @Override
    public void onDisconnect() {
        // Call parent method to ensure "register" status is tracked
        super.onDisconnect();
        if (mIRCCallback != null) {
            mIRCCallback.onDisconnect(this, mIsQuitting);
        }
    }

    @Override
    public void quitServer(String reason) {
        super.quitServer(reason);
        mIsQuitting = true;
    }

    public void setLogin2(String login) {
        setLogin(login);
    }

}
