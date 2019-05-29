package com.xueersi.parentsmeeting.modules.livevideo.event;

public class UpdatePkState {
    String where;

    public UpdatePkState(String where) {
        this.where = where;
    }

    public String getWhere() {
        return where;
    }
}
