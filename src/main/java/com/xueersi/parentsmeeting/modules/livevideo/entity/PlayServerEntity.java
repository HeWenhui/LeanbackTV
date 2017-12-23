package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.util.List;

public class PlayServerEntity {

    /**
     * appname : live_server ccode : 中国 code : 200 icode : 天威视讯 pcode :
     * playserver :
     * [{"acode":"","address":"120.132.42.131","ccode":"中国","group":
     * "小运营商1组","icode"
     * :"小运营商","pcode":"北京","priority":2,"provide":"xueersi"},{"acode"
     * :"","address"
     * :"livesource.xescdn.com","ccode":"中国","group":"","icode":"蓝汛"
     * ,"pcode":"","priority":1,"provide":"lanxun"}]
     */

    private String appname;
    private String ccode;
    private int code;
    private String icode;
    private String pcode;
    private String rtmpkey;
    /**
     * acode : address : 120.132.42.131 ccode : 中国 group : 小运营商1组 icode : 小运营商
     * pcode : 北京 priority : 2 provide : xueersi
     */

    private List<PlayserverEntity> playserver;

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public void setCcode(String ccode) {
        this.ccode = ccode;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setIcode(String icode) {
        this.icode = icode;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

    public void setPlayserver(List<PlayserverEntity> playserver) {
        this.playserver = playserver;
    }

    public String getAppname() {
        return appname;
    }

    public String getCcode() {
        return ccode;
    }

    public int getCode() {
        return code;
    }

    public String getIcode() {
        return icode;
    }

    public String getPcode() {
        return pcode;
    }

    public void setRtmpkey(String rtmpkey) {
        this.rtmpkey = rtmpkey;
    }

    public String getRtmpkey() {
        return rtmpkey;
    }

    public List<PlayserverEntity> getPlayserver() {
        return playserver;
    }

    public static class PlayserverEntity {
        private boolean useFlv;
        private String acode;
        private String address;
        private String ccode;
        private String group;
        private String icode;
        private String pcode;
        private int priority;
        private String provide;
        private String rtmpkey;
        private String httpport;
        private String flvpostfix;

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PlayserverEntity)) {
                return false;
            }
            PlayserverEntity playserverEntity = (PlayserverEntity) obj;
//            return playserverEntity.address.equals(address);
            return playserverEntity.address.equals(address) && playserverEntity.provide.equals(provide);
        }

        public boolean isUseFlv() {
            return useFlv;
        }

        public void setUseFlv(boolean useFlv) {
            this.useFlv = useFlv;
        }

        public void setAcode(String acode) {
            this.acode = acode;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setCcode(String ccode) {
            this.ccode = ccode;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public void setIcode(String icode) {
            this.icode = icode;
        }

        public void setPcode(String pcode) {
            this.pcode = pcode;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public void setProvide(String provide) {
            this.provide = provide;
        }

        public String getAcode() {
            return acode;
        }

        public String getAddress() {
            return address;
        }

        public String getCcode() {
            return ccode;
        }

        public String getGroup() {
            return group;
        }

        public String getIcode() {
            return icode;
        }

        public String getPcode() {
            return pcode;
        }

        public int getPriority() {
            return priority;
        }

        public String getProvide() {
            return provide;
        }

        public String getRtmpkey() {
            return rtmpkey;
        }

        public void setRtmpkey(String rtmpkey) {
            this.rtmpkey = rtmpkey;
        }

        public String getHttpport() {
            return httpport;
        }

        public void setHttpport(String httpport) {
            this.httpport = httpport;
        }

        public String getFlvpostfix() {
            return flvpostfix;
        }

        public void setFlvpostfix(String flvpostfix) {
            this.flvpostfix = flvpostfix;
        }
    }
}
