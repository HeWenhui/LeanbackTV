package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * 聊天区显示的Entity
 */
public class MessageShowEntity {
    String sender;
    String text;
    String headurl;
    int messageType;
    String login;
    String hostname;
    String target;

    boolean isSelf;
    /** 连对次数 */
    String evenNum;

    public String getEvenNum() {
        return evenNum;
    }

    public void setEvenNum(String evenNum) {
        this.evenNum = evenNum;
    }

    public MessageShowEntity(boolean isSelf, String sender, String login, String hostname, String target, String text) {
        this.isSelf = isSelf;
        this.sender = sender;
        this.login = login;
        this.hostname = hostname;
        this.target = target;
        this.text = text;
    }

    public MessageShowEntity(String target, String sender, String login, String hostname, String text, String headurl) {
        this.target = target;
        this.sender = sender;
        this.login = login;
        this.hostname = hostname;
        this.text = text;
        this.headurl = headurl;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHeadurl() {
        return headurl;
    }

    public void setHeadurl(String headurl) {
        this.headurl = headurl;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
}
