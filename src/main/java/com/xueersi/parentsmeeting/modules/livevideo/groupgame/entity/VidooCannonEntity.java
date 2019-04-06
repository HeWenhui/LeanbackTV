package com.xueersi.parentsmeeting.modules.livevideo.groupgame.entity;

import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.TeamMemberEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 语音炮弹统计
 */
public class VidooCannonEntity {
    public int id;
    public int rightNum;
    public TeamMemberEntity teamMemberEntity;
    public HashMap<GroupGameTestInfosEntity.TestInfoEntity.AnswersEntity, ArrayList<Integer>> wordScore = new HashMap<>();
}
