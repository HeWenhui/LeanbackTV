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

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;

import java.io.IOException;
import java.util.Vector;

/**
 * The class that actually handles the connection to an IRC server
 *
 * @author linyuqiang
 */
public class IRCConnection  {
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private static final String TAG = "IRCConnection";
    private IRCCallback mIRCCallback;
    /** 是不是用户主动调退出方法 */
    private boolean mIsQuitting = false;
    private Vector<String> privMsg;
    String nickname;
    String realname;
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
     * Set the nickname of the user
     *
     * @param nickname The nickname to use
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Set the real name of the user
     *
     * @param realname The realname to use
     */
    public void setRealName(String realname) {
        // XXX: Pircbot uses the version for "real name" and "version".
        // The real "version" value is provided by onVersion()
        this.realname = realname;
    }

    protected void onStartConnect() {
        if (mIRCCallback != null) {
            mIRCCallback.onStartConnect();
        }
    }

    /**
     * On connect
     */
    public void onConnect() {
        logger.d("onConnect");
        if (mIRCCallback != null) {
            mIRCCallback.onConnect(this);
        }
    }

    /**
     * On register
     */
    public void onRegister() {
        if (mIRCCallback != null) {
            mIRCCallback.onRegister();
        }
    }

    /**
     * On channel action
     */
    protected void onAction(String sender, String login, String hostname, String target, String action) {
    }

    /**
     * On Channel Info
     */
    protected void onChannelInfo(String channel, int userCount, String topic) {
        if (mIRCCallback != null) {
            mIRCCallback.onChannelInfo(channel, userCount, topic);
        }
    }

    /**
     * On Deop
     */

    protected void onDeop(String target, String sourceNick, String sourceLogin, String sourceHostname, String recipient) {
    }

    /**
     * On DeVoice
     */

    protected void onDeVoice(String target, String sourceNick, String sourceLogin, String sourceHostname,
                             String recipient) {
    }

    /**
     * On Invite
     */

    protected void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname,
                            String target) {
    }

    protected void onUserList(String channel, User[] users) {
        if (mIRCCallback != null) {
            mIRCCallback.onUserList(channel, users);
        }
    }

    /**
     * On Join
     */
    protected void onJoin(String target, String sender, String login, String hostname) {
        if (mIRCCallback != null) {
            mIRCCallback.onJoin(target, sender, login, hostname);
        }
    }

    protected void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason, String channel) {
        if (mIRCCallback != null) {
            mIRCCallback.onQuit(sourceNick, sourceLogin, sourceHostname, reason, "");
        }
    }

    /**
     * On Kick
     */
    protected void onKick(String target, String kickerNick, String kickerLogin, String kickerHostname,
                          String recipientNick, String reason) {
        if (mIRCCallback != null) {
            mIRCCallback.onKick(target, kickerNick, kickerLogin, kickerHostname, recipientNick, reason);
        }
    }

    /**
     * On Message
     */
    protected void onMessage(String target, String sender, String login, String hostname, String text) {
        if (mIRCCallback != null) {
            mIRCCallback.onMessage(target, sender, login, hostname, text);
        }
    }

    protected void onPrivateMessage(String sender, String login, String hostname, String target, String message) {
        if (mIRCCallback != null) {
            mIRCCallback.onPrivateMessage(false, sender, login, hostname, target, message);
        }
    }

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

    protected void onTopic(String channel, String topic, String setBy, long date, boolean changed, String channelId) {
        if (mIRCCallback != null) {
            logger.i("channel = " + channel + " topic = " + topic + " setBy" + setBy + " date" + date + " changed = " + changed + " channelId" + channelId);
            mIRCCallback.onTopic(channel, topic, setBy, date, changed, channelId);
        }
    }

    long before = 0;





    protected void onUnknown(String line) {
        if (mIRCCallback != null) {
            mIRCCallback.onUnknown(line);
        }
    }

    /**
     * On disconnect
     */
    public void onDisconnect() {
        // Call parent method to ensure "register" status is tracked
        if (mIRCCallback != null) {
            mIRCCallback.onDisconnect(this, mIsQuitting);
        }
    }

    public void quitServer(String reason) {
        mIsQuitting = true;
    }

}
