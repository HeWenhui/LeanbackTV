package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/3/14.
 */

public class ExPerienceLiveMessage implements Serializable{
    private List<LiveExMsg> msg;
    public static class LiveExMsg implements Serializable{
        private String id;
        private String sender;
        private String receiver;
        private String channel;
        private DetailText text;
        public static class DetailText implements Serializable{
            private String choiceType;
            private String gold;
            private String id;
            private String isTestUseH5;
            private String nonce;
            private String num;
            private String ptype;
            private Boolean refresh;
            private String time;
            private String type;
            private String by;
            private String msg;
            private String name;

            public String getBy() {
                return by;
            }

            public void setBy(String by) {
                this.by = by;
            }

            public String getMsg() {
                return msg;
            }

            public void setMsg(String msg) {
                this.msg = msg;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getChoiceType() {
                return choiceType;
            }

            public void setChoiceType(String choiceType) {
                this.choiceType = choiceType;
            }

            public String getGold() {
                return gold;
            }

            public void setGold(String gold) {
                this.gold = gold;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getIsTestUseH5() {
                return isTestUseH5;
            }

            public void setIsTestUseH5(String isTestUseH5) {
                this.isTestUseH5 = isTestUseH5;
            }

            public String getNonce() {
                return nonce;
            }

            public void setNonce(String nonce) {
                this.nonce = nonce;
            }

            public String getNum() {
                return num;
            }

            public void setNum(String num) {
                this.num = num;
            }

            public String getPtype() {
                return ptype;
            }

            public void setPtype(String ptype) {
                this.ptype = ptype;
            }

            public Boolean getRefresh() {
                return refresh;
            }

            public void setRefresh(Boolean refresh) {
                this.refresh = refresh;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
        private String notice;
        private String ts;

        public String getId() {
            return id;
        }

        public void setId(String id) {
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

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public DetailText getText() {
            return text;
        }

        public void setText(DetailText text) {
            this.text = text;
        }

        public String getNotice() {
            return notice;
        }

        public void setNotice(String notice) {
            this.notice = notice;
        }

        public String getTs() {
            return ts;
        }

        public void setTs(String ts) {
            this.ts = ts;
        }
    }
    private List<LiveExNum> onlineNum;
    public static class LiveExNum implements Serializable{
        private String id;
        private String onlineNum;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOnlineNum() {
            return onlineNum;
        }

        public void setOnlineNum(String onlineNum) {
            this.onlineNum = onlineNum;
        }

    }

    public List<LiveExMsg> getMsg() {
        return msg;
    }

    public void setMsg(List<LiveExMsg> msg) {
        this.msg = msg;
    }

    public List<LiveExNum> getOnlineNum() {
        return onlineNum;
    }

    public void setOnlineNum(List<LiveExNum> onlineNum) {
        this.onlineNum = onlineNum;
    }
}

