package com.xueersi.parentsmeeting.modules.livevideo.question.http;

import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.NewCourseSec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CourseWareParse {
    public NewCourseSec parse(ResponseEntity responseEntity) {
        try {
            NewCourseSec newCourseSec = new NewCourseSec();
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            ArrayList<NewCourseSec.Test> tests = newCourseSec.getTests();
            JSONArray array = jsonObject.getJSONArray("testInfos");
            for (int i = 0; i < array.length(); i++) {
                JSONObject testObj = array.getJSONObject(i);
                NewCourseSec.Test test = new NewCourseSec.Test();
                test.setId(testObj.getString("id"));
                test.setPreviewPath(testObj.getString("previewPath"));
                tests.add(test);
            }
            return newCourseSec;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
