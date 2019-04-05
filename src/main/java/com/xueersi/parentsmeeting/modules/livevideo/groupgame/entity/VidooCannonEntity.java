package com.xueersi.parentsmeeting.modules.livevideo.groupgame.entity;

import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 语音炮弹统计
 */
public class VidooCannonEntity {
    public int id;
    public TeamMemberEntity teamMemberEntity;
    /** 正确答题 */
    public List<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity> rightAnswerList = new ArrayList<>();
}
