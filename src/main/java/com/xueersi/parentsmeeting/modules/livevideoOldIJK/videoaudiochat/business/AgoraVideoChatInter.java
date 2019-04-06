package com.xueersi.parentsmeeting.modules.livevideoOldIJK.videoaudiochat.business;

import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/11.
 * 接麦接口，为了原生和网页的切换
 */
public interface AgoraVideoChatInter {
    View getRootView();

    void startRecord(String method, String room, String nonce, boolean video);

    void stopRecord(String nonce);

    void updateUser(boolean classmateChange, ArrayList<ClassmateEntity> classmateEntities);

    void onNetWorkChange(int netWorkType);

    void removeMe(String nonce);

}
