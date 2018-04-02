package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.util.ArrayList;

/**
 * Created by linyuqiang on 2018/4/2.
 */

public class GoldTeamStatus {
    private ArrayList<Student> students = new ArrayList<>();

    public ArrayList<Student> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<Student> students) {
        this.students = students;
    }

    public static class Student {
        boolean isMe = false;
        String stuId;// "31203",
        String gold;// 15,
        String name;// "lyq2@qq.com",
        String nickname;// "ssss",
        String en_name;// "rer",
        String avatar_path;// "http:\/\/xesfile.xesimg.com\/user\/h\/31203.jpg"

        public boolean isMe() {
            return isMe;
        }

        public void setMe(boolean me) {
            isMe = me;
        }

        public String getStuId() {
            return stuId;
        }

        public void setStuId(String stuId) {
            this.stuId = stuId;
        }

        public String getGold() {
            return gold;
        }

        public void setGold(String gold) {
            this.gold = gold;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getEn_name() {
            return en_name;
        }

        public void setEn_name(String en_name) {
            this.en_name = en_name;
        }

        public String getAvatar_path() {
            return avatar_path;
        }

        public void setAvatar_path(String avatar_path) {
            this.avatar_path = avatar_path;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Student)) {
                return false;
            }
            Student other = (Student) obj;
            return ("" + stuId).equals(other.stuId);
        }
    }
}
