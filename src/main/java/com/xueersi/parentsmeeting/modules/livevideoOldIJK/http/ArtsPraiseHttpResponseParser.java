package com.xueersi.parentsmeeting.modules.livevideoOldIJK.http;

import com.xueersi.common.http.HttpResponseParser;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.entity.ArtsRraiseEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 文科表扬榜解析
 *
 * @author chenkun
 * @version 1.0, 2018/7/18 上午9:57
 */

public class ArtsPraiseHttpResponseParser extends HttpResponseParser {

    public ArtsRraiseEntity parsePraise(ResponseEntity responseEntity) {
        ArtsRraiseEntity artsRraiseEntity = new ArtsRraiseEntity();
        try {
            JSONObject data = (JSONObject) responseEntity.getJsonObject();
            artsRraiseEntity.setRankTitle(data.optString("rankTitle", ""));
            artsRraiseEntity.setRankType(data.optInt("rankType"));
            artsRraiseEntity.setWord(data.optString("word"));
            artsRraiseEntity.setCounselorName(data.optString("counselorName"));
            artsRraiseEntity.setCounselorAvatar(data.optString("counselorAvatar"));

            if (data.has("list")) {
                JSONArray jsonArray = data.getJSONArray("list");
                List<ArtsRraiseEntity.RankEntity> praiseList = new ArrayList<ArtsRraiseEntity.RankEntity>();
                ArtsRraiseEntity.RankEntity rankData = null;
                JSONObject jsonObject = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    rankData = new ArtsRraiseEntity.RankEntity();
                    jsonObject = jsonArray.getJSONObject(i);
                    rankData.setStuId(jsonObject.optString("stuId"));
                    rankData.setRealName(jsonObject.optString("realname"));
                    rankData.setNumber(jsonObject.optInt("number"));
                    rankData.setInList(jsonObject.optInt("inList"));
                    praiseList.add(rankData);
                }
                artsRraiseEntity.setRankEntities(praiseList);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return artsRraiseEntity;
    }

}
