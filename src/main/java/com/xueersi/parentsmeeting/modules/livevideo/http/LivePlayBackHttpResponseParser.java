package com.xueersi.parentsmeeting.modules.livevideo.http;

import com.xueersi.common.http.HttpResponseParser;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.logerhelper.MobAgent;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.business.VoiceBarrageMsgEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageGroupEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LivePlayBackMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RecommondCourseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.SpeechEvalEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoBannerBuyCourseEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

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
//                logger.i( "liveMessagesParser:id=" + id + ",calendar,Y=" + calendar.get(Calendar.YEAR)
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

    /**
     * 解析回放弹幕
     *
     * @param responseEntity
     * @return
     */
    public ArrayList<VoiceBarrageMsgEntity> parseVoiceBarrageMsg(ResponseEntity responseEntity) {
        Object data = responseEntity.getJsonObject();
        ArrayList<VoiceBarrageMsgEntity> arrayList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(data.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                VoiceBarrageMsgEntity voiceBarrageMsgEntity = new VoiceBarrageMsgEntity();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                voiceBarrageMsgEntity.setVoiceId(jsonObject.optString("voiceId"));
                ArrayList<VoiceBarrageMsgEntity.VoiceBarrageItemEntity> voiceBarrageItemEntities = new ArrayList<>();
                JSONArray itemArray = jsonObject.getJSONArray("msgData");
                for (int j = 0; j < itemArray.length(); j++) {
                    JSONObject itemObject = itemArray.getJSONObject(j);
                    VoiceBarrageMsgEntity.VoiceBarrageItemEntity voiceBarrageItemEntity = voiceBarrageMsgEntity.new
                            VoiceBarrageItemEntity();
                    voiceBarrageItemEntity.setStuId(itemObject.optString("stuId"));
                    voiceBarrageItemEntity.setMsg(itemObject.optString("msg"));
                    voiceBarrageItemEntity.setRelativeTime(itemObject.optInt("relativeTime"));
                    voiceBarrageItemEntity.setName(itemObject.optString("name"));
                    voiceBarrageItemEntity.setHeadImgPath(itemObject.optString("headImgPath"));
                    voiceBarrageItemEntities.add(voiceBarrageItemEntity);
                }
                voiceBarrageMsgEntity.setVoiceBarrageItemEntities(voiceBarrageItemEntities);
                arrayList.add(voiceBarrageMsgEntity);
            }

        } catch (Exception e) {
            return null;
        }
        return arrayList;
    }

    /**
     * 解析站立直播体验课推荐课程信息
     *
     * @param responseEntity
     * @return
     */
    public RecommondCourseEntity parseRecommondCourseInfo(ResponseEntity responseEntity) {
        RecommondCourseEntity recommondCourseEntity = new RecommondCourseEntity();
        try {
            JSONObject jsonObject = (JSONObject) responseEntity.getJsonObject();
            recommondCourseEntity.setCourseName(jsonObject.optString("courseName"));
            recommondCourseEntity.setCoursePrice(jsonObject.optString("coursePrice"));
            recommondCourseEntity.setCourseId(jsonObject.optString("courseId"));
            recommondCourseEntity.setClassId(jsonObject.optString("classId"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recommondCourseEntity;
    }

    /**
     * 解析站立直播体验课课中购买推荐课程后的轮播消息(假的)
     *
     * @param responseEntity
     * @return
     */
    public VideoBannerBuyCourseEntity parseBannerBuyCourseEntity(ResponseEntity responseEntity) {
        VideoBannerBuyCourseEntity bannerBuyCourseEntity = new VideoBannerBuyCourseEntity();
        try {
            Queue<VideoBannerBuyCourseEntity.BannerMessage> messageList = new LinkedList<>();
            JSONArray jsonArray = (JSONArray) responseEntity.getJsonObject();
            for (int i = 0; i < jsonArray.length(); i++) {
                VideoBannerBuyCourseEntity.BannerMessage bannerMessage = new VideoBannerBuyCourseEntity.BannerMessage();
                JSONObject itemJSON = jsonArray.getJSONObject(i);
                bannerMessage.setCourseName(itemJSON.optString("courseName"));
                bannerMessage.setUserName(itemJSON.optString("userName"));
                messageList.add(bannerMessage);
            }
            bannerBuyCourseEntity.setBannerMessages(messageList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bannerBuyCourseEntity;
    }

    public void parseLiveGetInfo(JSONObject data, LiveGetInfo liveGetInfo, int mLiveType, int isArts) {
        // TODO: 2018/12/5  
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            if (isArts == 1) {
                parseLiveGetInfoLibarts(data, liveGetInfo);
            } else {
                parseLiveGetInfoScience(data, liveGetInfo);
            }
        }
    }

    /**
     * 解析getInfo 理科
     *
     * @param data
     * @param getInfo
     */
    public void parseLiveGetInfoScience(JSONObject data, LiveGetInfo getInfo) {
        int isPrimarySchool = data.optInt("isPrimarySchool", 0);
        getInfo.setIsPrimarySchool(isPrimarySchool);
        getInfo.setEducationStage(data.optString("educationStage", "1"));
        getInfo.setGetCourseWareHtmlNew(data.optString("getCourseWareHtml"));
        getInfo.setGetCourseWareHtmlZhongXueUrl(data.optString("getCourseWareHtmlZhongXueUrl"));
        if (!data.has("getCourseWareHtml")){
            getInfo.setGetCourseWareHtmlNew(data.optString("getCourseWareWeb"));
            getInfo.setSubjectiveItem2AIUrl(data.optString("subjectiveItem2AIUrl"));
        }
    }

    /**
     * 解析getInfo 文科
     *
     * @param data
     * @param getInfo
     */
    public void parseLiveGetInfoLibarts(JSONObject data, LiveGetInfo getInfo) {

    }
}
