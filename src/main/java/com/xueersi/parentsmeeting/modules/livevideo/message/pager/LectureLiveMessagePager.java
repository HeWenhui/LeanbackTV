package com.xueersi.parentsmeeting.modules.livevideo.message.pager;

import android.content.Context;

import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.User;
import com.xueersi.parentsmeeting.modules.livevideo.widget.BaseLiveMediaControllerBottom;

import java.util.ArrayList;

public class LectureLiveMessagePager extends EvenDriveLiveMessagePager {

    public LectureLiveMessagePager(Context context, BaseLiveMediaControllerBottom liveMediaControllerBottom, ArrayList<LiveMessageEntity> liveMessageEntities, ArrayList<LiveMessageEntity> otherLiveMessageEntities) {
        super(context, liveMediaControllerBottom, liveMessageEntities, otherLiveMessageEntities);
    }

    @Override
    public void onUserList(String channel, final User[] users) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                tvMessageCount.setText("本教室在线"+peopleCount + "人");
            }
        });
    }

    @Override
    public void onJoin(String target, String sender, String login, String hostname) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                tvMessageCount.setText("本教室在线"+peopleCount + "人");
            }
        });
    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname,
                       String reason) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                tvMessageCount.setText("本教室在线"+peopleCount + "人");
            }
        });
    }
}
