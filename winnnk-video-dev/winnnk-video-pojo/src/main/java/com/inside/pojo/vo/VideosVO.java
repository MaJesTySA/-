package com.inside.pojo.vo;

import java.util.Date;

public class VideosVO {

    private String id;
    private String userId;
    private String audioId;
    private String videoDesc;
    private String videoPath;
    private Float videoSeconds;
    private Integer videoWidth;
    private Integer videoHeight;
    private String coverPath;
    private Long likeCounts;
    private Integer status;
    private Date createTime;

    private String faceImage;
    private String nickName;

    public String getFaceImage() {
        return faceImage;
    }

    public void setFaceImage(String faceImage) {
        this.faceImage = faceImage;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAudioId() {
        return audioId;
    }

    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }

    public String getVideoDesc() {
        return videoDesc;
    }

    public void setVideoDesc(String videoDesc) {
        this.videoDesc = videoDesc;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public Float getVideoSeconds() {
        return videoSeconds;
    }

    public void setVideoSeconds(Float videoSeconds) {
        this.videoSeconds = videoSeconds;
    }

    public Integer getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(Integer videoWidth) {
        this.videoWidth = videoWidth;
    }

    public Integer getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(Integer videoHeight) {
        this.videoHeight = videoHeight;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public Long getLikeCounts() {
        return likeCounts;
    }

    public void setLikeCounts(Long likeCounts) {
        this.likeCounts = likeCounts;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}