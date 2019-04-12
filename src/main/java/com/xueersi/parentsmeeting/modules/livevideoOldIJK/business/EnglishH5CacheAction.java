package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business;

/**
 * 英语课件缓存
 * Created by linyuqiang on 2017/12/28.
 */
public interface EnglishH5CacheAction {

    void getCourseWareUrl();

    void start();

    void stop();

    void onNetWorkChange(int netWorkType);

}
