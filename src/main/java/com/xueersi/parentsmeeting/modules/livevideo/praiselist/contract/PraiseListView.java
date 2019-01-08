package com.xueersi.parentsmeeting.modules.livevideo.praiselist.contract;

import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.ExcellentListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.LikeListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.LikeProbabilityEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.ProgressListEntity;

import java.util.ArrayList;

/**
 * Created by Zhang Yuansun on 2018/1/2.
 * 表扬榜View层
 */

public interface PraiseListView {

    void initView(RelativeLayout relativeLayout);

    void setPresenter(PraiseListPresenter presenter);

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
     * @param excellentListEntity
     */
    void onExcellentList(ExcellentListEntity excellentListEntity);

    /**
     * 显示点赞榜
     *
     * @param likeListEntity
     */
    void onLikeList(LikeListEntity likeListEntity);

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
     * @param likeProbabilityEntity
     */
    void receiveLikeNotice(ArrayList<String> stuNames, LikeProbabilityEntity likeProbabilityEntity);

    /**
     * 显示感谢点赞的提示
     *
     */
    void showLikeToast();

    /**
     * 关闭榜单
     */
    void closePraiseList();

    /**
     * 设置点赞按钮是否可点击
     *
     * @param enabled
     */
    void setLikeBtnEnabled(boolean enabled);


    /**
     * 播放器区域变化时更新视图
     *
     * @param liveVideoPoint
     */
    void setVideoLayout(LiveVideoPoint liveVideoPoint);

}