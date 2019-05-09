package com.xueersi.parentsmeeting.modules.livevideo.entity;

public class Teacher {
    private String _nick;
    /** 是不是离开 */
    public boolean isLeave = false;

    public Teacher(String _nick) {
        this._nick = _nick;
    }

    public void set_nick(String nick){
        this._nick = nick;
    }

    public boolean equals(Object o) {
        if (o instanceof Teacher) {
            Teacher other = (Teacher) o;
            return other._nick.equals(_nick);
        }
        return false;
    }

    public String get_nick() {
        return _nick;
    }

}
