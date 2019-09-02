package com.xueersi.parentsmeeting.modules.livevideoOldIJK.videoaudiochat.page;

import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/10/17
 * 接麦接口，为了原生和网页的切换
 */
public interface VideoChatInter {
    View getRootView();

    void startRecord(String method, String room, String nonce);

    void stopRecord();

    void updateUser(boolean classmateChange, ArrayList<ClassmateEntity> classmateEntities);

    void onNetWorkChange(int netWorkType);
}
