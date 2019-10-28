package com.xueersi.parentsmeeting.modules.livevideo.englishname.business;

import com.xueersi.common.http.HttpResponseParser;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.entity.EngLishNameEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EnlishNameParser extends HttpResponseParser {

    public EnlishNameParser() {

    }

    public List<EngLishNameEntity> pareseEnglishName(String nameString, int type, List<EngLishNameEntity> indexList) {
        try {
            List<EngLishNameEntity> listName;
            JSONObject jsonObject = new JSONObject(nameString);
            JSONObject nameJson = null;
            if (LiveVideoConfig.LIVE_GROUP_CLASS_USER_SEX_BOY == type) {
                nameJson = jsonObject.optJSONObject("male");
            } else {
                nameJson = jsonObject.optJSONObject("female");
            }
            listName = new ArrayList<>();
            EngLishNameEntity entity;
            JSONArray wordArray = null;
            EngLishNameEntity entityIndex;
            int indexPosition = 0;
            for (int i = 0; i < indexList.size(); i++) {
                wordArray = nameJson.optJSONArray(indexList.get(i).getWordIndex());
                indexList.get(i).setIndexPostion(indexPosition);
                if (wordArray == null || wordArray.length() == 0) {
                    continue;
                }
                entityIndex = new EngLishNameEntity();
                if(i==0) {
                    entityIndex.setSelect(true);
                }
                //indexEntity.setAreaListIndex(j+listName.size());
                entityIndex.setIndex(true);
                entityIndex.setSpanNum(4);
                entityIndex.setWordIndex(indexList.get(i).getWordIndex());
                entityIndex.setIndexPostion(i);
                listName.add(entityIndex);
                indexPosition++;
                for (int j = 0; j < wordArray.length(); j++) {
                    entity = new EngLishNameEntity();
                    JSONObject wordJson = wordArray.optJSONObject(j);
                    entity.setSpanNum(1);
                    entity.setName(wordJson.optString("name"));
                    entity.setIndexPostion(i);
                    listName.add(entity);
                    indexPosition++;
                }


            }
            return listName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
