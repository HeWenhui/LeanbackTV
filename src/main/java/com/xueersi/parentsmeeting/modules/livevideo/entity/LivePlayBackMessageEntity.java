package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * Created by linyuqiang on 2016/12/30.
 */

public class LivePlayBackMessageEntity {

    /**
     * id : 1481347772555526
     * sender : s_2_11144_2492652_0
     * receiver : #2L11144
     * channel : 1
     * text : {"name":"杨宛萤","type":"130","msg":"哇啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊"}
     * notice : 0
     * ts :  2016-12-10 13:30:13
     */
    private long id;
    private String sender;
    private String receiver;
    private int channel;
    private int notice;
    private String ts;
    private Text text;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getNotice() {
        return notice;
    }

    public void setNotice(int notice) {
        this.notice = notice;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public static class Text {
        /**
         * name : 杨宛萤
         * type : 130
         * msg : 哇啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊
         */
        private String name;
        private int type;
        private String msg;
        private CharSequence charSequence;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public CharSequence getCharSequence() {
            return charSequence;
        }

        public void setCharSequence(CharSequence charSequence) {
            this.charSequence = charSequence;
        }
    }
}
