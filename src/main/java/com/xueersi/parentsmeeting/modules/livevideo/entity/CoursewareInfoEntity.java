package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: WangDe on 2019/2/28
 */
public class CoursewareInfoEntity {

    /** 课件信息列表 */
    List<LiveCourseware> coursewareList = new ArrayList<>();
    /** cdn列表 */
    List<String> cdns = new ArrayList<>();
    /** ip列表 */
    List<String> ips = new ArrayList<>();
    /** 字体公式资源 */
    List<String> resources = new ArrayList<>();
    List<CoursewareInfoEntity.GroupClassVideoInfo> courses;

    public List<LiveCourseware> getCoursewareList() {
        return coursewareList;
    }

    public void setCoursewareList(List<LiveCourseware> coursewareList) {
        this.coursewareList = coursewareList;
    }

    public List<GroupClassVideoInfo> getCourses() {
        return courses;
    }

    public void setCourses(List<GroupClassVideoInfo> courses) {
        this.courses = courses;
    }

    /** 加载页资源 */
//    List<String> loadpages = new ArrayList<>();
    /** 静态资源 */
//    List<String> staticSources = new ArrayList<>();

    /** Nb 加试 实验预加载资源 **/
//    NbCoursewareInfo nbCoursewareInfo;
    //理科加试实验
    List<NbCoursewareInfo> addExperiments;
    //理科自由实验
    List<NbCoursewareInfo> freeExperiments;

//    public List<AddExperiment> getAddExperiments() {
//        return addExperiments;
//    }

//    public void setAddExperiments(List<AddExperiment> addExperiments) {
//        this.addExperiments = addExperiments;
//    }

//    public List<FreeExperiment> getFreeExperiments() {
//        return freeExperiments;
//    }

//    public void setFreeExperiments(List<FreeExperiment> freeExperiments) {
//        this.freeExperiments = freeExperiments;
//    }


    public List<NbCoursewareInfo> getAddExperiments() {
        return addExperiments;
    }

    public void setAddExperiments(List<NbCoursewareInfo> addExperiments) {
        this.addExperiments = addExperiments;
    }

    public List<NbCoursewareInfo> getFreeExperiments() {
        return freeExperiments;
    }

    public void setFreeExperiments(List<NbCoursewareInfo> freeExperiments) {
        this.freeExperiments = freeExperiments;
    }

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

//    public List<String> getLoadpages() {
//        return loadpages;
//    }

//    public void setLoadpages(List<String> loadpages) {
//        this.loadpages = loadpages;
//    }

//    public List<String> getStaticSources() {
//        return staticSources;
//    }

//    public void setStaticSources(List<String> staticSources) {
//        this.staticSources = staticSources;
//    }

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

    public static class GroupClassVideoInfo {

        List<String> host;
        //直播id
        String liveId;
        //学生id
        String stuId;
        //暂时没有md5
        String md5;
        List<GroupClassPath> groupClassPaths;
        //是否已经下载过
        boolean isDown;

        private String recordPlanId;

        public String getRecordPlanId() {
            return recordPlanId;
        }

        public void setRecordPlanId(String recordPlanId) {
            this.recordPlanId = recordPlanId;
        }

        public boolean isDown() {
            return isDown;
        }

        public void setDown(boolean down) {
            isDown = down;
        }

        public List<GroupClassPath> getGroupClassPaths() {
            return groupClassPaths;
        }

        public void setGroupClassPaths(List<GroupClassPath> groupClassPaths) {
            this.groupClassPaths = groupClassPaths;
        }

        public static class GroupClassPath {
            //路径名
            String path;

            //存储在本地文件名
            String fileName;

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public String getFileName() {
                return fileName;
            }

            public void setFileName(String fileName) {
                this.fileName = fileName;
            }
        }


        public List<String> getHost() {
            return host;
        }

        public void setHost(List<String> host) {
            this.host = host;
        }

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

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
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
        //英语智能测评的实体
        CourseWareIntelligentEntity intelligentEntity;

        public CourseWareIntelligentEntity getIntelligentEntity() {
            return intelligentEntity;
        }

        public void setIntelligentEntity(CourseWareIntelligentEntity intelligentEntity) {
            this.intelligentEntity = intelligentEntity;
        }

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


//    public void setNbCoursewareInfo(NbCoursewareInfo nbCoursewareInfo) {
//        this.nbCoursewareInfo = nbCoursewareInfo;
//    }
//
//    public NbCoursewareInfo getNbCoursewareInfo() {
//        return nbCoursewareInfo;
//    }

    /**
     * 乐步物理实验资源
     */
    public static class NbCoursewareInfo {
        /** 下载地址 **/
        private String resourceUrl;
        /** 文件Md5值 **/
        private String resourceMd5;
        /** 在互动题中对应的id,对应存储的文件名 */
        private String id;

        public String getResourceUrl() {
            return resourceUrl;
        }

        public void setResourceUrl(String resourceUrl) {
            this.resourceUrl = resourceUrl;
        }

        public String getResourceMd5() {
            return resourceMd5;
        }

        public void setResourceMd5(String resourceMd5) {
            this.resourceMd5 = resourceMd5;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    /**
     * 英语智能测评实体
     */
    public static class CourseWareIntelligentEntity {
        //智能反馈资源包   非智能反馈返回空
        String resource;

        String md5;

        public String getResource() {
            return resource;
        }

        public void setResource(String resource) {
            this.resource = resource;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }
    }
//
//    public static class AddExperiment {
//        String zip_path;
//        String zip_md5;
//
//        public String getZip_path() {
//            return zip_path;
//        }
//
//        public void setZip_path(String zip_path) {
//            this.zip_path = zip_path;
//        }
//
//        public String getZip_md5() {
//            return zip_md5;
//        }
//
//        public void setZip_md5(String zip_md5) {
//            this.zip_md5 = zip_md5;
//        }
//    }
//
//    public static class FreeExperiment {
//        String zip_path;
//        String zip_md5;
//
//        public String getZip_path() {
//            return zip_path;
//        }
//
//        public void setZip_path(String zip_path) {
//            this.zip_path = zip_path;
//        }
//
//        public String getZip_md5() {
//            return zip_md5;
//        }
//
//        public void setZip_md5(String zip_md5) {
//            this.zip_md5 = zip_md5;
//        }
//    }
}
