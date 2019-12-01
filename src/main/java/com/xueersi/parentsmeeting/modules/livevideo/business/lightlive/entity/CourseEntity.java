package com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.business.lightlive.entity
 * @ClassName: CourseEntity
 * @Description: 推荐课程实体
 * @Author: WangDe
 * @CreateDate: 2019/11/28 10:46
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/28 10:46
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class CourseEntity  {
    /**
     * 专属辅导老师
     */
    public static final int EXCTEACHERCOURSE = 1;
    public static final int TYPE_PROMOTION_PRE_SALE = 12;
    public static final int TYPE_PROMOTION_GROUPON = 8;
    public static final int TYPE_PROMOTION_UNIT_BUY = 3;
    /**
     * 1206新加subjectName
     */
    protected String subjectName;
    /**
     * 课程ID
     */
    protected String courseId;
    /**
     * 组ID
     */
    protected String groupID;
    /**
     * 小班ID
     */
    protected String classID;
    /**
     * 课程名称
     */
    protected String courseName;
    /**
     * 难度，直播有
     */
    protected int courseDifficulity;
    /**
     * 课程实际支付价格
     */
    protected int coursePrice;
    /**
     * 课程原始价格
     */
    protected int courseOrignPrice;
    /**
     * 剩余人数，直播有
     */
    protected String remainPeople;
    /**
     * 主讲老师
     */
    protected ArrayList<CourseTeacherEntity> lstMainTeacher = new ArrayList<CourseTeacherEntity>();

    /**
     * 辅导老师，直播有
     */
    protected ArrayList<CourseTeacherEntity> lstCoachTeacher = new ArrayList<CourseTeacherEntity>();
    /**
     * 外教老师信息
     */
    protected ArrayList<CourseTeacherEntity> lstForeignTeacher = new ArrayList<CourseTeacherEntity>();
    /**
     * 直播课上课时间
     */
    private String liveShowTime;
    /**
     * 是否报满
     */
    private String isFull;
    /**
     * 截止提醒
     */
    private String deadTime;
    /**
     * 副标题
     */
    private String secondTitle;
    /**
     * 共多少讲
     */
    private String chapterCount;
    /**
     * 学科ID
     */
    protected int subjectID;

    private boolean isGroupon;

    private List<SubJect> subJects;

    /**
     * 辅导老师类型 1：专属老师
     * 是否专属老师课程
     */
    private int excTeacherCourse;

    private int type;

    private int showCounselorTeacher;//	int	是否展示辅导老师
    /**
     * subjectName 6.0新增课程属性
     */
    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getExcTeacherCourse() {
        return excTeacherCourse;
    }
    /**
     * 是否是专属老师
     *
     * @return
     */
    public boolean isExcTeacherCourse() {
        return EXCTEACHERCOURSE == excTeacherCourse;
    }

    public void setExcTeacherCourse(int excTeacherCourse) {
        this.excTeacherCourse = excTeacherCourse;
    }

    public CourseEntity() {
    }

    public CourseEntity(String courseName, int type) {
        this.courseName = courseName;
    }
    /**
     * 组ID
     */
    public String getGroupID() {
        return groupID;
    }

    /**
     * 组ID
     */
    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    /**
     * 小班ID
     */
    public String getClassID() {
        return classID;
    }

    /**
     * 小班ID
     */
    public void setClassID(String classID) {
        this.classID = classID;
    }


    /**
     * 课程ID
     */
    public String getCourseId() {
        return courseId;
    }

    /**
     * 课程ID
     */
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }


    /**
     * 课程名
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    /**
     * 课程价格
     */
    public String getCoursePriceStr() {
        return coursePrice + "";
    }

    /**
     * 课程价格
     */
    public int getCoursePrice() {
        return coursePrice;
    }

    /**
     * 课程价格
     */
    public void setCoursePrice(int coursePrice) {
        this.coursePrice = coursePrice;
    }

    /**
     * 课程原始价格
     */
    public int getCourseOrignPrice() {
        return courseOrignPrice;
    }

    /**
     * 课程原始价格
     */
    public void setCourseOrignPrice(int courseOrignPrice) {
        this.courseOrignPrice = courseOrignPrice;
    }

    /**
     * 主讲老师
     */
    public ArrayList<CourseTeacherEntity> getLstMainTeacher() {
        return lstMainTeacher;
    }

    /**
     * 主讲老师
     */
    public void setLstMainTeacher(ArrayList<CourseTeacherEntity> lstMainTeacher) {
        this.lstMainTeacher = lstMainTeacher;
    }

    /**
     * 辅导老师，直播有
     */
    public ArrayList<CourseTeacherEntity> getLstCoachTeacher() {
        return lstCoachTeacher;
    }

    /**
     * 辅导老师，直播有
     */
    public void setLstCoachTeacher(ArrayList<CourseTeacherEntity> lstCoachTeacher) {
        this.lstCoachTeacher = lstCoachTeacher;
    }

    /**
     * 课程名称
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * 难度，直播有
     */
    public int getCourseDifficulity() {
        return courseDifficulity;
    }

    /**
     * 难度，直播有
     */
    public void setCourseDifficulity(int courseDifficulity) {
        this.courseDifficulity = courseDifficulity;
    }

    /**
     * 剩余人数，直播有
     */
    public String getRemainPeople() {
        return remainPeople;
    }

    /**
     * 剩余人数，直播有
     */
    public void setRemainPeople(String remainPeople) {
        this.remainPeople = remainPeople;
    }

    public String getLiveShowTime() {
        return liveShowTime;
    }

    public void setLiveShowTime(String liveShowTime) {
        this.liveShowTime = liveShowTime;
    }

    public String getIsFull() {
        return isFull;
    }

    public void setIsFull(String isFull) {
        this.isFull = isFull;
    }

    public String getDeadTime() {
        return deadTime;
    }

    public void setDeadTime(String deadTime) {
        this.deadTime = deadTime;
    }


    public String getSecondTitle() {
        return secondTitle;
    }

    public void setSecondTitle(String secondTitle) {
        this.secondTitle = secondTitle;
    }

    public ArrayList<CourseTeacherEntity> getLstForeignTeacher() {
        return lstForeignTeacher;
    }

    public void setLstForeignTeacher(ArrayList<CourseTeacherEntity> lstForeignTeacher) {
        this.lstForeignTeacher = lstForeignTeacher;
    }

    public String getChapterCount() {
        return chapterCount;
    }

    public void setChapterCount(String chapterCount) {
        this.chapterCount = chapterCount;
    }


    public int getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }

//    public boolean isGroupon() {
//        return isGroupon;
//    }
//
//    public void setGroupon(boolean groupon) {
//        isGroupon = groupon;
//    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isPreSale() {
        return type == TYPE_PROMOTION_PRE_SALE;
    }

    public boolean isGroupOn() {
        return type == TYPE_PROMOTION_GROUPON;
    }

    public int getShowCounselorTeacher() {
        return showCounselorTeacher;
    }

    public void setShowCounselorTeacher(int showCounselorTeacher) {
        this.showCounselorTeacher = showCounselorTeacher;
    }

    public List<SubJect> getSubJects() {
        return subJects;
    }

    public void setSubJects(List<SubJect> subJects) {
        this.subJects = subJects;
    }

    public static class SubJect{

        String id;
        String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

