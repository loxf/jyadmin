package org.loxf.jyadmin.client.dto;

import org.loxf.jyadmin.base.bean.BaseModel;

public class VideoConfigDto extends BaseModel {
    private String videoId;

    private String videoName;

    private String videoUrl;

    private Integer status;

    private String progress;

    private String videoUnique;

    private String remark;

    private String videoOutId;

    private String metaData;

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId == null ? null : videoId.trim();
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName == null ? null : videoName.trim();
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl == null ? null : videoUrl.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getVideoUnique() {
        return videoUnique;
    }

    public void setVideoUnique(String videoUnique) {
        this.videoUnique = videoUnique;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getVideoOutId() {
        return videoOutId;
    }

    public void setVideoOutId(String videoOutId) {
        this.videoOutId = videoOutId == null ? null : videoOutId.trim();
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }
}