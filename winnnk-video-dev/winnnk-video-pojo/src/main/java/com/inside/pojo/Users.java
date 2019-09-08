package com.inside.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@ApiModel(value = "用户对象", description = "用户Model对象")
public class Users {
    @ApiModelProperty(hidden = true)
    @Id
    private String id;

    @ApiModelProperty(value = "用户名", name = "username", example = "someUser", required = true)
    private String username;

    @ApiModelProperty(value = "密码", name = "password", example = "123456", required = true)
    private String password;

    @ApiModelProperty(hidden = true)
    @Column(name = "face_image")
    private String faceImage;

    @ApiModelProperty(hidden = true)
    private String nickname;

    @ApiModelProperty(hidden = true)
    @Column(name = "fans_counts")
    private Integer fansCounts;

    @ApiModelProperty(hidden = true)
    @Column(name = "follow_counts")
    private Integer followCounts;

    @ApiModelProperty(hidden = true)
    @Column(name = "receive_like_counts")
    private Integer receiveLikeCounts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFaceImage() {
        return faceImage;
    }

    public void setFaceImage(String faceImage) {
        this.faceImage = faceImage;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getFansCounts() {
        return fansCounts;
    }

    public void setFansCounts(Integer fansCounts) {
        this.fansCounts = fansCounts;
    }

    public Integer getFollowCounts() {
        return followCounts;
    }

    public void setFollowCounts(Integer followCounts) {
        this.followCounts = followCounts;
    }

    public Integer getReceiveLikeCounts() {
        return receiveLikeCounts;
    }

    public void setReceiveLikeCounts(Integer receiveLikeCounts) {
        this.receiveLikeCounts = receiveLikeCounts;
    }
}

