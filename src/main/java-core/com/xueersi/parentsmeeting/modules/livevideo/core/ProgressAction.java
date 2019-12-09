package com.xueersi.parentsmeeting.modules.livevideo.core;

/**
 * @Date on 2019/10/17 16:43
 * @Author zhangyuansun
 * @Description 英语1v2 直播进度回调
 */
public interface ProgressAction {
    void onProgressChanged(int progress);
    void onProgressBegin(int beginProgress);
}
