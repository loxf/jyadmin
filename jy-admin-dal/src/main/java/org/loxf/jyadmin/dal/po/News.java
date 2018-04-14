package org.loxf.jyadmin.dal.po;

import org.loxf.jyadmin.base.bean.BaseModel;

import java.util.Date;

public class News extends BaseModel {

    private String titleId;

    private String title;

    private String keyword;

    private String description;

    private Integer status;

    private Integer type;

    private Date deployTime;

    private String source;

    private String contextId;

    private Integer pushWx;

    private Integer readTimes;

    private Integer hasRead;

    private String currentCustId;

    public String getTitleId() {
        return titleId;
    }

    public void setTitleId(String titleId) {
        this.titleId = titleId == null ? null : titleId.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword == null ? null : keyword.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getDeployTime() {
        return deployTime;
    }

    public void setDeployTime(Date deployTime) {
        this.deployTime = deployTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source == null ? null : source.trim();
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public Integer getPushWx() {
        return pushWx;
    }

    public void setPushWx(Integer pushWx) {
        this.pushWx = pushWx;
    }

    public Integer getReadTimes() {
        return readTimes;
    }

    public void setReadTimes(Integer readTimes) {
        this.readTimes = readTimes;
    }

    public Integer getHasRead() {
        return hasRead;
    }

    public void setHasRead(Integer hasRead) {
        this.hasRead = hasRead;
    }

    public String getCurrentCustId() {
        return currentCustId;
    }

    public void setCurrentCustId(String currentCustId) {
        this.currentCustId = currentCustId;
    }
}