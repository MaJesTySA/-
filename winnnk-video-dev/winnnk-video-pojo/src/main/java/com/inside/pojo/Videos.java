package com.inside.pojo;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

public class Videos {
    @Id
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "audio_id")
    private String audioId;

    @Column(name = "video_desc")
    private String videoDesc;

    @Column(name = "video_path")
    private String videoPath;

    @Column(name = "video_seconds")
    private Float videoSeconds;

    @Column(name = "video_width")
    private Integer videoWidth;

    @Column(name = "video_height")
    private Integer videoHeight;

    @Column(name = "cover_path")
    private String coverPath;

    @Column(name = "like_counts")
    private Long likeCounts;

    private Integer status;

    @Column(name = "create_time")
    private Date createTime;

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