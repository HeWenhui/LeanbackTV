package com.xueersi.parentsmeeting.modules.livevideo.achievement.business;

import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamPkRankEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.StarAndGoldEntity;

import java.util.ArrayList;

/**
 * Created by linyuqiang on 2017/7/20.
 * 星星互动
 */
public interface StarInteractAction {

    void onStarStart(ArrayList<String> data, String starid, String answer, String nonce);

    void onStarStop(String id, ArrayList<String> answer, String nonce);

    void onSendMsg(String msg);

    void onGetStar(StarAndGoldEntity starAndGoldEntity);

    void onStarAdd(int star, float x, float y);
}
