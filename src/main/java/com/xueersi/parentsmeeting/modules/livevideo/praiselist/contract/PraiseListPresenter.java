package com.xueersi.parentsmeeting.modules.livevideo.praiselist.contract;

import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.PraiseListDanmakuEntity;

import java.util.ArrayList;

/**
 * Created by Zhang Yuansun on 2018/1/2.
 * 表扬榜Presenter层
 */
public interface PraiseListPresenter {
    void getExcellentList();
    void getLikeList();
    void getMiniMarketList();
    void getLikeProbability();

    void sendLikeNum(int agreeNum, String teamId, int barrageType);
    String getStuName();
    int getProbability();
}
