package com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.business;

import java.util.ArrayList;

/**
 * Created by Zhang Yuansun on 2018/8/6.
 */

public class VoiceBarrageMsgEntity {
    private String voiceId;

    ArrayList<VoiceBarrageItemEntity> voiceBarrageItemEntities = new ArrayList<>();

    public String getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }

    public ArrayList<VoiceBarrageItemEntity> getVoiceBarrageItemEntities() {
        return voiceBarrageItemEntities;
    }

    public void setVoiceBarrageItemEntities(ArrayList<VoiceBarrageItemEntity> voiceBarrageItemEntities) {
        this.voiceBarrageItemEntities = voiceBarrageItemEntities;
    }

    public class VoiceBarrageItemEntity {
        private String stuId;
        private String msg;
        private int relativeTime;
        private String name;
        private String headImgPath;

        public String getStuId() {
            return stuId;
        }

        public void setStuId(String stuId) {
            this.stuId = stuId;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getRelativeTime() {
            return relativeTime;
        }

        public void setRelativeTime(int relativeTime) {
            this.relativeTime = relativeTime;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHeadImgPath() {
            return headImgPath;
        }

        public void setHeadImgPath(String headImgPath) {
            this.headImgPath = headImgPath;
        }
    }
}
