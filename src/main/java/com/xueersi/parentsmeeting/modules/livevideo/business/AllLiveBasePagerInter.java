package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

/**
 * Created by linyuqiang on 2018/7/25.
 * 直播所有pager
 */
public interface AllLiveBasePagerInter {

    void addLiveBasePager(LiveBasePager liveBasePager);

    void removeLiveBasePager(LiveBasePager liveBasePager);
}
