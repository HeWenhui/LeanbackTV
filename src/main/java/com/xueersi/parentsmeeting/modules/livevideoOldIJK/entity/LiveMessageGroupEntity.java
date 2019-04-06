package com.xueersi.parentsmeeting.modules.livevideoOldIJK.entity;

import java.util.ArrayList;

/**
 * Created by linyuqiang on 2017/1/6.
 */

public class LiveMessageGroupEntity {
    public long lastid;
    public int count;
    public ArrayList<LivePlayBackMessageEntity> liveMessageEntities = new ArrayList<>();
    public ArrayList<LivePlayBackMessageEntity> otherMessageEntities = new ArrayList<>();
}
