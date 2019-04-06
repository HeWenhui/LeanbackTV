package com.xueersi.parentsmeeting.modules.livevideoOldIJK.entity;

import java.util.List;

/**
 * Created by: WangDe on 2019/2/28
 */
public class CoursewareInfoEntity {

    /** 课件信息列表 */
    List<LiveCourseware> coursewareList;
    /** cdn列表 */
    List<String> cdns;
    /** ip列表 */
    List<String> ips;
    /** 字体公式资源 */
    List<String> resources;
    /** 加载页资源 */
    List<String> loadpages;
    /** 静态资源 */
    List<String> staticSources;

    public List<LiveCourseware> getCoursewaresList() {
        return coursewareList;
    }

    public void setCoursewaresList(List<LiveCourseware> coursewareList) {
        this.coursewareList = coursewareList;
    }

    public List<String> getCdns() {
        return cdns;
    }

    public void setCdns(List<String> cdns) {
        this.cdns = cdns;
    }

    public List<String> getIps() {
        return ips;
    }

    public void setIps(List<String> ips) {
        this.ips = ips;
    }

    public List<String> getResources() {
        return resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }

    public List<String> getLoadpages() {
        return loadpages;
    }

    public void setLoadpages(List<String> loadpages) {
        this.loadpages = loadpages;
    }

    public List<String> getStaticSources() {
        return staticSources;
    }

    public void setStaticSources(List<String> staticSources) {
        this.staticSources = staticSources;
    }

    public static class LiveCourseware {
        /** 课件id */
        String liveId;
        long stime;
        List<ItemCoursewareInfo> coursewareInfos;

        public String getLiveId() {
            return liveId;
        }

        public void setLiveId(String liveId) {
            this.liveId = liveId;
        }

        public long getStime() {
            return stime;
        }

        public void setStime(long stime) {
            this.stime = stime;
        }

        public List<ItemCoursewareInfo> getCoursewareInfos() {
            return coursewareInfos;
        }

        public void setCoursewareInfos(List<ItemCoursewareInfo> coursewareInfos) {
            this.coursewareInfos = coursewareInfos;
        }
    }

    public static class ItemCoursewareInfo {
        /** 页面包ID */
        String packageId;
        /** 页面ID */
        String pageId;
        /** 试题ID 英语用到 */
        String sourceId;
        /**
         * 页面包来源
         * 英语(1:设计部本地批量上传;2:讲义生成或在线创建;3:模板改编创建;4:未来客户端课件)
         * 理科 文科 1：为课件 2 为非课件
         */
        String packageSource;
        /** 是否是有模板 */
        boolean isTemplate;
        /** 资源地址 （去除域名） 可通过资源地址+'/thumbnail.png'页面缩略图地址 */
        String resourceUrl;
        /** 模板地址 （去除域名） */
        String templateUrl;
        /** MD5 */
//        String md5;

        String resourceMd5;
        String templateMd5;

        public String getPackageId() {
            return packageId;
        }

        public void setPackageId(String packageId) {
            this.packageId = packageId;
        }

        public String getPageId() {
            return pageId;
        }

        public void setPageId(String pageId) {
            this.pageId = pageId;
        }

        public String getSourceId() {
            return sourceId;
        }

        public void setSourceId(String sourceId) {
            this.sourceId = sourceId;
        }

        public String getPackageSource() {
            return packageSource;
        }

        public void setPackageSource(String packageSource) {
            this.packageSource = packageSource;
        }

        public boolean isTemplate() {
            return isTemplate;
        }

        public void setTemplate(boolean template) {
            isTemplate = template;
        }

        public String getResourceUrl() {
            return resourceUrl;
        }

        public void setResourceUrl(String resourceUrl) {
            this.resourceUrl = resourceUrl;
        }

        public String getTemplateUrl() {
            return templateUrl;
        }

        public void setTemplateUrl(String templateUrl) {
            this.templateUrl = templateUrl;
        }

//        public String getMd5() {
//            return md5;
//        }
//
//        public void setMd5(String md5) {
//            this.md5 = md5;
//        }

        public String getResourceMd5() {
            return resourceMd5;
        }

        public void setResourceMd5(String resourceMd5) {
            this.resourceMd5 = resourceMd5;
        }

        public String getTemplateMd5() {
            return templateMd5;
        }

        public void setTemplateMd5(String templateMd5) {
            this.templateMd5 = templateMd5;
        }
    }
}
