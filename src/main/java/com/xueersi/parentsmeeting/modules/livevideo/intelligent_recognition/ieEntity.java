package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition;

public class ieEntity {

    /**
     * liveId :
     * stuId :
     * stuCouId :
     * materialId :
     * materialName :
     * materiaTypeId :
     * content : Whose soccer ball is it?
     * resource : {"audio":{"ball.mp3":"https://xesfile.xesimg.com/knowledge_material/audio/15578235206140.mp3","is.mp3":"https://xesfile.xesimg.com/knowledge_material/audio/15578235209780.mp3","it.mp3":"https://xesfile.xesimg.com/knowledge_material/audio/15578235204341.mp3","soccer.mp3":"https://xesfile.xesimg.com/knowledge_material/audio/15578235204262.mp3","whose.mp3":"https://xesfile.xesimg.com/knowledge_material/audio/15578235212318.mp3","Whose_soccer_ball_is_it.mp3":"https://xesfile.xesimg.com/knowledge_material/audio/15578235214563.mp3"},"img":["https://xesfile.xesimg.com/knowledge_material/600dpi/15578235267974_612x343.png"]}
     * setAnswerTime : 3
     * releaseGold : 2
     * answered :
     */

    private String liveId;
    private String stuId;
    private String stuCouId;
    private String materialId;
    private String materialName;
    private String materiaTypeId;
    private String content;
    private ResourceBean resource;
    private String setAnswerTime;
    private String releaseGold;
    private String answered;

    public String getLiveId() {
        return liveId;
    }

    public void setLiveId(String liveId) {
        this.liveId = liveId;
    }

    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public String getStuCouId() {
        return stuCouId;
    }

    public void setStuCouId(String stuCouId) {
        this.stuCouId = stuCouId;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMateriaTypeId() {
        return materiaTypeId;
    }

    public void setMateriaTypeId(String materiaTypeId) {
        this.materiaTypeId = materiaTypeId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ResourceBean getResource() {
        return resource;
    }

    public void setResource(ResourceBean resource) {
        this.resource = resource;
    }

    public String getSetAnswerTime() {
        return setAnswerTime;
    }

    public void setSetAnswerTime(String setAnswerTime) {
        this.setAnswerTime = setAnswerTime;
    }

    public String getReleaseGold() {
        return releaseGold;
    }

    public void setReleaseGold(String releaseGold) {
        this.releaseGold = releaseGold;
    }

    public String getAnswered() {
        return answered;
    }

    public void setAnswered(String answered) {
        this.answered = answered;
    }

    public static class ResourceBean {

        /**
         * ball : https://xesfile.xesimg.com/knowledge_material/audio/15578235206140.mp3
         * is : https://xesfile.xesimg.com/knowledge_material/audio/15578235209780.mp3
         * it : https://xesfile.xesimg.com/knowledge_material/audio/15578235204341.mp3
         * soccer : https://xesfile.xesimg.com/knowledge_material/audio/15578235204262.mp3
         * whose : https://xesfile.xesimg.com/knowledge_material/audio/15578235212318.mp3
         * Whose_soccer_ball_is_it : https://xesfile.xesimg.com/knowledge_material/audio/15578235214563.mp3
         */

        private String ball;
        private String is;
        private String it;
        private String soccer;
        private String whose;
        private String Whose_soccer_ball_is_it;

        public String getBall() {
            return ball;
        }

        public void setBall(String ball) {
            this.ball = ball;
        }

        public String getIs() {
            return is;
        }

        public void setIs(String is) {
            this.is = is;
        }

        public String getIt() {
            return it;
        }

        public void setIt(String it) {
            this.it = it;
        }

        public String getSoccer() {
            return soccer;
        }

        public void setSoccer(String soccer) {
            this.soccer = soccer;
        }

        public String getWhose() {
            return whose;
        }

        public void setWhose(String whose) {
            this.whose = whose;
        }

        public String getWhose_soccer_ball_is_it() {
            return Whose_soccer_ball_is_it;
        }

        public void setWhose_soccer_ball_is_it(String Whose_soccer_ball_is_it) {
            this.Whose_soccer_ball_is_it = Whose_soccer_ball_is_it;
        }
    }
}
