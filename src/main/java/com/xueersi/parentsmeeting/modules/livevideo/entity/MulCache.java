package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.util.List;

/**
 * Created by David on 2018/7/5.
 */

public class MulCache {

    private List<MoreCache> list;
    private List<String> resource;
    private List<String> loadpages;

    public List<MoreCache> getList() {
        return list;
    }

    public void setList(List<MoreCache> list) {
        this.list = list;
    }

    public List<String> getResource() {
        return resource;
    }

    public void setResource(List<String> resource) {
        this.resource = resource;
    }

    public List<String> getLoadpages() {
        return loadpages;
    }

    public void setLoadpages(List<String> loadpages) {
        this.loadpages = loadpages;
    }
}
