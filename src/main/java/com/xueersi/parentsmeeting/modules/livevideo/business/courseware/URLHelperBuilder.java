package com.xueersi.parentsmeeting.modules.livevideo.business.courseware;

public class URLHelperBuilder {
    private String ip;
    private String cdn;
    private String host;
    private String port;

    public URLHelperBuilder setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public URLHelperBuilder setCdn(String cdn) {
        this.cdn = cdn;
        return this;
    }

    public URLHelperBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    public URLHelperBuilder setPort(String port) {
        this.port = port;
        return this;
    }

    public URLHelper createURLHelper() {
        return new URLHelper(ip, cdn, host, port);
    }
}