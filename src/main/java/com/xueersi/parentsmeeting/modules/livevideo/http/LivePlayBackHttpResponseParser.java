package com.xueersi.parentsmeeting.modules.livevideo.http;

import com.xueersi.parentsmeeting.http.HttpResponseParser;
import com.xueersi.parentsmeeting.http.ResponseEntity;
import com.xueersi.parentsmeeting.logerhelper.MobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.entity.SpeechEvalEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LivePlayBackMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageGroupEntity;
import com.xueersi.parentsmeeting.entity.VideoResultEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 直播回放网络数据解析类
 */
public class LivePlayBackHttpResponseParser extends HttpResponseParser {

    private static final String TAG = "CourseHttpResponseParser";

    /**
     * 解析直播回放互动题获取红包
     *
     * @param responseEntity
     * @return
     */
    public VideoResultEntity redPacketParseParser(ResponseEntity responseEntity) {
        VideoResultEntity entity = new VideoResultEntity();
        try {
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            entity.setResultType(jsonObject.optInt("tip", 1));
            entity.setGoldNum(jsonObject.optInt("gold"));
            entity.setMsg(jsonObject.optString("msg"));

        } catch (Exception e) {
            MobAgent.httpResponseParserError(TAG, "redPacketParseParser", e.getMessage());
        }
        return entity;
    }

    /**
     * 解析直播回放互动题结果
     *
     * @param responseEntity
     * @return
     */
    public VideoResultEntity parseQuestionAnswer(ResponseEntity responseEntity, boolean isVoice) {
        VideoResultEntity entity = new VideoResultEntity();
        try {
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            entity.setResultType(jsonObject.optInt("tip"));
            if (jsonObject.optInt("tip", 0) == 4) {
                entity.setResultType(1);
            }
            entity.setGoldNum(jsonObject.optInt("gold"));
            entity.setMsg(jsonObject.optString("msg"));
            entity.setRightNum(jsonObject.optInt("rightnum", 0));
            if (isVoice) {
                entity.setStandardAnswer(jsonObject.optString("standardAnswer"));
                entity.setYourAnswer(jsonObject.optString("yourAnswer"));
            }
        } catch (Exception e) {
            MobAgent.httpResponseParserError(TAG, "parseQuestionAnswer", e.getMessage());
        }
        return entity;
    }

    public LiveMessageGroupEntity liveMessagesParser(JSONArray array) {
        LiveMessageGroupEntity liveMessageGroupEntity = new LiveMessageGroupEntity();
        ArrayList<LivePlayBackMessageEntity> liveMessageEntities = liveMessageGroupEntity.liveMessageEntities;
        ArrayList<LivePlayBackMessageEntity> otherMessageEntities = liveMessageGroupEntity.otherMessageEntities;
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject object = array.getJSONObject(i);
                long id = object.getLong("id");
                liveMessageGroupEntity.lastid = id;
                LivePlayBackMessageEntity messageEntity = new LivePlayBackMessageEntity();
                messageEntity.setId(id);
                messageEntity.setSender(object.optString("sender", ""));
                messageEntity.setReceiver(object.getString("receiver"));
                messageEntity.setChannel(object.getInt("channel"));
                JSONObject textObj = object.getJSONObject("text");
                LivePlayBackMessageEntity.Text text = new LivePlayBackMessageEntity.Text();
                text.setName(textObj.optString("name"));
                text.setType(textObj.optInt("type"));
                text.setMsg(textObj.optString("msg"));
                messageEntity.setText(text);
                messageEntity.setNotice(object.getInt("notice"));
                messageEntity.setTs(object.getString("ts"));
//                Date date = new Date();
//                date.setTime(id / 1000);
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTime(date);
//                Loger.i(TAG, "liveMessagesParser:id=" + id + ",calendar,Y=" + calendar.get(Calendar.YEAR)
//                        + ",M=" + calendar.get(Calendar.MONTH) + ",d=" + calendar.get(Calendar.DAY_OF_MONTH)
//                        + ",h=" + calendar.get(Calendar.HOUR_OF_DAY)
//                        + ",m=" + calendar.get(Calendar.MINUTE)
//                        + ",s=" + calendar.get(Calendar.SECOND));
                if (text.getType() == XESCODE.TEACHER_MESSAGE) {
                    liveMessageEntities.add(messageEntity);
                } else {
                    otherMessageEntities.add(messageEntity);
                }
                liveMessageGroupEntity.count++;
            } catch (JSONException e) {
                MobAgent.httpResponseParserError(TAG, "liveMessagesParser", e.getMessage());
            }
        }
        return liveMessageGroupEntity;
    }

    public SpeechEvalEntity parseSpeechEval(ResponseEntity responseEntity) {
        JSONObject data = (JSONObject) responseEntity.getJsonObject();
        SpeechEvalEntity speechEvalEntity = new SpeechEvalEntity();
        try {
            speechEvalEntity.setContent(data.getString("content"));
            speechEvalEntity.setAnswer(data.optString("answer", speechEvalEntity.getContent()));
            speechEvalEntity.setSpeechEvalTime(data.getLong("speechEvalTime"));
            speechEvalEntity.setNowTime(data.getLong("nowTime"));
            speechEvalEntity.setSpeechEvalReleaseTime(data.getLong("speechEvalReleaseTime"));
            speechEvalEntity.setEndTime(data.getLong("endTime"));
            speechEvalEntity.setAnswered(data.getInt("answered"));
            speechEvalEntity.setTesttype(data.getString("testtype"));
        } catch (JSONException e) {
            return null;
        }
        return speechEvalEntity;
    }
}
