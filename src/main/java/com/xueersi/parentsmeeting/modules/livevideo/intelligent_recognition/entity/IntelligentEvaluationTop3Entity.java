package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.entity;

import com.tencent.cos.xml.utils.StringUtils;

import java.util.ArrayList;

public class IntelligentEvaluationTop3Entity {
    private String id;
    private String myScore;//学生本人分数
    private String myName;//"学生本人姓名
    private String myNickName;//"学生本人昵称
    private String myEnName;//"学生本人英文名
    private String myAvatarPath;//":"http://xesfile.xesimg.com/user/h/def10002.png"
    private String httpUrl = "";
    private String httpRes = "";

    private ArrayList<Student> students = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMyScore() {
        return myScore;
    }

    public void setMyScore(String myScore) {
        this.myScore = myScore;
    }

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public String getMyNickName() {
        return myNickName;
    }

    public void setMyNickName(String myNickName) {
        this.myNickName = myNickName;
    }

    public String getMyEnName() {
        return myEnName;
    }

    public void setMyEnName(String myEnName) {
        this.myEnName = myEnName;
    }

    public String getMyAvatarPath() {
        return myAvatarPath;
    }

    public void setMyAvatarPath(String myAvatarPath) {
        this.myAvatarPath = myAvatarPath;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<Student> students) {
        this.students = students;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public String getHttpRes() {
        return httpRes;
    }

    public void setHttpRes(String httpRes) {
        this.httpRes = httpRes;
    }

    public static class Student {
        boolean isMe = false;
        String stuId;// "31203",
        String gold;// 15,
        String score;// 15,
        String name;// "lyq2@qq.com",
        String realname;
        String nickname;// "ssss",
        String en_name;// "rer",
        /** 显示的名字 */
        String showName;
        String avatar_path;// "http:\/\/xesfile.xesimg.com\/user\/h\/31203.jpg"
        boolean isRight = false;
        /** 只绘制头像不绘制名字 */
        boolean drawName;
        /** 空对象 */
        boolean nullEntity = false;

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

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRealname() {
            return realname;
        }

        public void setRealname(String realname) {
            this.realname = realname;
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

        public String getShowName() {
            if (StringUtils.isEmpty(showName)) {
                createShowName();
            }
            String newText = getShortName(showName);
            return newText;
        }

        /**
         * 站立直播名称显示，中文4个，英文8个
         *
         * @param name
         * @return
         */
        public String getShortName(String name) {
            if (name == null) {
                return "";
            }
            String newText = name;
            boolean isChinese = isChinese(name);
            int length = name.length();
            if (isChinese) {
                if (length > 4) {
                    newText = name.substring(0, 4) + "...";
                }
            } else {
                if (length > 8) {
                    newText = name.substring(0, 8) + "...";
                }
            }
            return newText;
        }

        public boolean isChinese(String str) {
            if (StringUtils.isEmpty(str)) {
                return false;
            }
            char[] chars = str.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                byte[] bytes = ("" + chars[i]).getBytes();
                if (bytes.length > 2) {
                    return true;
                }
            }
            return false;
        }

        public void createShowName() {
            if (!StringUtils.isEmpty(en_name)) {
                this.showName = en_name;
            } else if (!StringUtils.isEmpty(realname)) {
                this.showName = realname;
            } else if (!StringUtils.isEmpty(nickname)) {
                this.showName = nickname;
            } else {
                this.showName = name;
            }
        }

        public String getAvatar_path() {
            return avatar_path;
        }

        public void setAvatar_path(String avatar_path) {
            this.avatar_path = avatar_path;
        }

        public boolean isRight() {
            return isRight;
        }

        public void setRight(boolean right) {
            isRight = right;
        }

        public boolean isDrawName() {
            return drawName;
        }

        public void setDrawName(boolean drawName) {
            this.drawName = drawName;
        }

        public boolean isNullEntity() {
            return nullEntity;
        }

        public void setNullEntity(boolean nullEntity) {
            this.nullEntity = nullEntity;
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
