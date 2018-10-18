package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * create by zyy 9/27/18
 */
public class VideoBannerBuyCourseEntity implements Serializable {
    //参考wifi地址https://wiki.xesv5.com/display/yunying/laoshi
    //消息轮播图
    private Queue<BannerMessage> bannerMessages;

    public Queue<BannerMessage> getBannerMessages() {
        return bannerMessages;
    }

    public void setBannerMessages(Queue<BannerMessage> bannerMessages) {
        this.bannerMessages = bannerMessages;
    }

    /**
     * 聊天信息
     */
    public static class BannerMessage {
        //使用者名称
        private String userName;
        //课程名称
        private String courseName;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }
    }

}
