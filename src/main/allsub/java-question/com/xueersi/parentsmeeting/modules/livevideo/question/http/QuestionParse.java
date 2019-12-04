package com.xueersi.parentsmeeting.modules.livevideo.question.http;

import com.xueersi.common.http.ResponseEntity;

import org.json.JSONArray;

import java.util.ArrayList;

public class QuestionParse {

    public ArrayList<String> parseQueCache(ResponseEntity responseEntity) {
        ArrayList<String> list = new ArrayList<>();
        try {
            JSONArray array = (JSONArray) responseEntity.getJsonObject();
            for (int i = 0; i < array.length(); i++) {
                list.add(array.getString(i));
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        return list;
    }

}
