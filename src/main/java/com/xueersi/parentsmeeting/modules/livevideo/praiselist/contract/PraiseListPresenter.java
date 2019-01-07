package com.xueersi.parentsmeeting.modules.livevideo.praiselist.contract;

import com.xueersi.parentsmeeting.modules.livevideo.entity.ThumbsUpProbabilityEntity;

import java.util.ArrayList;

/**
 * Created by Zhang Yuansun on 2018/1/2.
 * 表扬榜Presenter层
 */
public interface PraiseListPresenter {
    void getHonorList(final int status);
    void getThumbsUpList();
    void getProgressList(final int status);
    void getThumbsUpProbability(final ArrayList<String> list);
    void sendThumbsUp();
    void sendThumbsUpNum(int agreeNum);
    String getStuName();
}
