package com.xueersi.parentsmeeting.modules.livevideo.miracast;

/**
 * Created by: WangDe on 2019/2/26
 */
public interface IMiracastState {

    void onConnect();

    void onDisConnect();

    void onSearch();

    void onStart();

    void onPause();

    void onStop();

}
