package com.xueersi.parentsmeeting.modules.livevideo.question.entity;

import java.util.HashMap;

public class ScienceStaticConfig {
    //客户端版本，两个要一致
    public static String THIS_VERSION = "V2";
    public static String THIS_VERSION_HTML = "v2";
    public HashMap<String, Version> stringVersionHashMap = new HashMap<>();

    public static class Version {
        public String version;
        public String localfile;
        public String url;//"url": "Statics\/editConfig",
        public String templateURL;// "templateURL": "https:\/\/live.xueersi.com\/scistatic\/xiaoxuekejian\/v2\/index.html",
        public String tarballURL;//"tarballURL": "https:\/\/live.xueersi.com\/scistatic\/xiaoxuekejian\/v2\/9f58bcbec2dd742172b089695a310980.zip",
        public String assetsHash;//"assetsHash": "c4f5f00e2714432929a4",
        public String templateForLocalURL;// "templateForLocalURL": "",
        public int canUseLocal;//"canUseLocal": "1"
    }
}
