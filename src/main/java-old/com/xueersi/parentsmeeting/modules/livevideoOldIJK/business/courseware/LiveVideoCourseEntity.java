package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.courseware;

import com.xueersi.parentsmeeting.modules.livevideo.entity.CoursewareInfoEntity.LiveCourseware;

import java.util.List;

public class LiveVideoCourseEntity {

    public static class LiveVideoCourseItemInfo {

        private LiveCourseware liveCourseware;

        private String cdn;

        private String ip;

        private String resource;

        private String loadpage;

        private String staticSource;

        public LiveCourseware getLiveCourseware() {
            return liveCourseware;
        }

        public void setLiveCourseware(LiveCourseware liveCourseware) {
            this.liveCourseware = liveCourseware;
        }

        public String getCdn() {
            return cdn;
        }

        public void setCdn(String cdn) {
            this.cdn = cdn;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getResource() {
            return resource;
        }

        public void setResource(String resource) {
            this.resource = resource;
        }

        public String getLoadpage() {
            return loadpage;
        }

        public void setLoadpage(String loadpage) {
            this.loadpage = loadpage;
        }

        public String getStaticSource() {
            return staticSource;
        }

        public void setStaticSource(String staticSource) {
            this.staticSource = staticSource;
        }
    }

    List<LiveVideoCourseItemInfo> liveVideoCourseItemInfos;

    public List<LiveVideoCourseItemInfo> getLiveVideoCourseItemInfos() {
        return liveVideoCourseItemInfos;
    }

    public void setLiveVideoCourseItemInfos(List<LiveVideoCourseItemInfo> liveVideoCourseItemInfos) {
        this.liveVideoCourseItemInfos = liveVideoCourseItemInfos;
    }
}
