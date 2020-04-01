package com.xueersi.parentsmeeting.modules.livevideo.util;

import com.xueersi.lib.log.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * json工具
 *
 * @author linyuqiang
 */
public class LiveJsonUtil {
    static Logger logger = LiveLoggerFactory.getLogger("LiveJsonUtil");

    /** 对比json, 返回差别 */
    public static JSONObject getDiffJson(JSONObject diffJson, JSONObject lastJson) {
        try {
            Iterator<String> keys = lastJson.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object diffValue = diffJson.get(key);
                Object lastValue = lastJson.get(key);
                if (lastValue instanceof JSONObject) {
                    //https://bugly.qq.com/v2/crash-reporting/errors/a0df5ed682/454687?pid=1
                    if (diffValue instanceof JSONObject) {
                        JSONObject removeJosn = getDiffJson((JSONObject) diffValue, (JSONObject) lastValue);
                        if ("{}".equals("" + removeJosn)) {
                            diffJson.remove(key);
                        }
                    }
                } else if (lastValue instanceof JSONArray && diffValue instanceof JSONArray) {
                    JSONArray diffArr = (JSONArray) diffValue;
                    getDiffJson(diffArr, (JSONArray) lastValue);
                    if (diffArr.length() == 0) {
                        diffJson.remove(key);
                    }
                } else {
                    if (lastValue.equals(diffValue)) {
                        diffJson.remove(key);
                        logger.d("getDiffJson:key=" + key + ",value=" + diffValue);
                    }
                }
            }
//                logger.d("getDiffJson:diffJson=" + diffJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return diffJson;
    }

    private static void getDiffJson(JSONArray diffArray, JSONArray lastArray) {
        if (diffArray.length() == lastArray.length()) {
            for (int i = 0; i < diffArray.length(); i++) {
                try {
                    Object diffValue = diffArray.get(i);
                    Object lastValue = lastArray.get(i);
                    if (diffValue instanceof JSONArray && lastValue instanceof JSONArray) {
                        getDiffJson((JSONArray) diffValue, (JSONArray) lastValue);
                    } else if (diffValue instanceof JSONObject) {
                        getDiffJson((JSONObject) diffValue, (JSONObject) lastValue);
                    } else {
                        if (lastValue.equals(diffValue)) {
                            diffArray.remove(i);
                            i--;
//                            logger.d("getDiffJson:key=" + lastValue + ",value=" + diffValue);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
