package com.xueersi.parentsmeeting.modules.livevideo.business;

import com.xueersi.parentsmeeting.modules.livevideo.entity.HonorListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LikeListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LikeProbabilityEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ProgressListEntity;

import java.util.ArrayList;

/**
 * Created by Zhang Yuansun on 2018/1/2.
 * 表扬榜事件
 */

public interface PraiseListAction {
    /**
     * 显示光荣榜
     *
     * @param honorListEntitie
     */
    void onHonerList(HonorListEntity honorListEntitie);

    /**
     * 显示点赞榜
     *
     * @param likeListEntitie
     */
    void onLikeList(LikeListEntity likeListEntitie);

    /**
     * 显示进步榜
     *
     * @param progressListEntitie
     */
    void onProgressList(ProgressListEntity progressListEntitie);

    /**
     * 关闭榜单
     */
    void closePraiseList();

    /**
     * 设置点赞概率
     *
     * @param likeProbabilityEntity
     */
    void setLikeProbability(LikeProbabilityEntity likeProbabilityEntity);

    /**
     * 获取点赞概率
     */
    int getLikeProbability();

    /**
     * 显示老师表扬横幅
     *
     * @param stuName
     * @param tecName
     */
    void showPraiseScroll(String stuName, String tecName);

    /**
     * 收到给我点赞的消息
     *
     * @param stuNames
     */
    void receiveLikeMessage(ArrayList<String> stuNames);

}