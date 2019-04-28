package com.xueersi.parentsmeeting.modules.livevideo.config;

/**
*理科战队pk 常量定义
*@author chekun
*created  at 2019/2/22 13:22
*/
public interface TeamPkConfig {
    /**领先**/
    int PK_STATE_LEAD = 1;
    /**落后**/
    int PK_STATE_BEHIND = 2;
    /**平手**/
    int PK_STATE_DRAW = 3;

    /**topic 中pk流程值：明星榜**/
    int TOPIC_PKSTEP_STAR_RANK_LIST = 3;

    /**topic 中pk流程值：黑马榜**/
    int TOPIC_PKSTEP_BLACK_RANK_LIST = 4;

    /**topic 中pk流程值：pk结束**/
    int TOPIC_PKSTEP_PK_END = 5;

}
