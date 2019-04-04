package com.xueersi.parentsmeeting.modules.livevideo.groupgame.entity;

import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * cleanup统计
 */
public class CleanUpEntity {
    public int id;
    public TeamMemberEntity teamMemberEntity;
    /** 总的答题 */
    public List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> answerList = new ArrayList<>();
    /** 正确答题 */
    public List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> rightAnswerList = new ArrayList<>();
}
