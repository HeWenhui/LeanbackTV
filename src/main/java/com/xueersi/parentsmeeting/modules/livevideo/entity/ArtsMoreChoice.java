package com.xueersi.parentsmeeting.modules.livevideo.entity;

import java.io.Serializable;

/**
 * Created by David on 2018/8/23.
 */

public class ArtsMoreChoice implements Serializable{
    private String sourceId;
    private String resourceUrl;
    private String templateUrl;

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
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
}
