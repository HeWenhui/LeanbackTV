package com.xueersi.parentsmeeting.modules.livevideo.entity;

/**
 * Created by David on 2018/6/7.
 */

public class MoreCache {
    private String packageId;
    private String packageSource;
    private int isTemplate;
    private String pageId;
    private String resourceUrl;
    private String templateUrl;

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getPackageSource() {
        return packageSource;
    }

    public void setPackageSource(String packageSource) {
        this.packageSource = packageSource;
    }

    public int getIsTemplate() {
        return isTemplate;
    }

    public void setIsTemplate(int isTemplate) {
        this.isTemplate = isTemplate;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
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
