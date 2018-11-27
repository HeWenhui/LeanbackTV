package com.xueersi.parentsmeeting.modules.livevideo.praiselist.business;

import com.xueersi.parentsmeeting.modules.livevideo.entity.HonorListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ProgressListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ThumbsUpListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ThumbsUpProbabilityEntity;

import java.util.ArrayList;

/**
 * Created by Zhang Yuansun on 2018/1/2.
 * 表扬榜事件
 */

public interface PraiseListAction {

    /**
     * 收到显示榜单的消息
     *
     * @param listType
     * @param nonce
     */
    void onReceivePraiseList(int listType, String nonce);

    /**
     * 显示优秀榜
     *
     * @param honorListEntity
     */
    void onHonerList(HonorListEntity honorListEntity);

    /**
     * 显示点赞榜
     *
     * @param thumbsUpListEntity
     */
    void onThumbsUpList(ThumbsUpListEntity thumbsUpListEntity);

    /**
     * 显示进步榜
     *
     * @param progressListEntity
     */
    void onProgressList(ProgressListEntity progressListEntity);

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
    void receiveThumbsUpNotice(ArrayList<String> stuNames);

    /**
     * 显示感谢点赞的提示
     *
     */
    void showThumbsUpToast();

    /**
     * 关闭榜单
     */
    void closePraiseList();

    /**
     * 设置点赞概率标识
     *
     * @param thumbsUpProbabilityEntity
     */
    void setThumbsUpProbability(ThumbsUpProbabilityEntity thumbsUpProbabilityEntity);

    /**
     * 获取点赞概率标识
     */
    int getThumbsUpProbability();

    /**
     * 设置点赞按钮是否可点击
     *
     * @param enabled
     */
    void setThumbsUpBtnEnabled(boolean enabled);


    /**
     * 播放器区域变化时更新视图
     *
     * @param width
     * @param height
     */
    void setVideoLayout(LiveVideoPoint liveVideoPoint);

    /**
     * Activity退出
     *
     */
    void destory();

    /**
     * 判断榜单是否正在显示
     *
     */
    boolean isShowing();

    /**
     * 设置当前榜单类型
     *
     * @param listType
     */
    void setCurrentListType(int listType);

    /**
     * 获取当前榜单类型
     *
     */
    int getCurrentListType();
}