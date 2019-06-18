package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.courseware;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PreloadStaticStorage {
    /**
     * 预加载的AB测的liveId
     */
    public static List<String> preloadLiveId = new CopyOnWriteArrayList<>();
    /**
     * 预加载下载失败的LiveId
     */
    public static List<String> failPreLoadDownInfo = new CopyOnWriteArrayList<>();

}
