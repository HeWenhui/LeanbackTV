package com.xueersi.parentsmeeting.modules.livevideo.praiselist.contract;

import java.util.ArrayList;

/**
 * Created by Zhang Yuansun on 2018/1/2.
 * 表扬榜Presenter层
 */
public interface PraiseListPresenter {
    void getExcellentList(final int status);
    void getLikeList();
    void getProgressList(final int status);
    void getLikeProbability(final ArrayList<String> list);
    void sendLike();
    void sendLikeNum(int agreeNum);
    String getStuName();
}
