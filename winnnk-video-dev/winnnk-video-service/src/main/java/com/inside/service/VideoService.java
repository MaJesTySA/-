package com.inside.service;

import com.inside.pojo.Comments;
import com.inside.pojo.Videos;
import com.inside.utils.PagedResult;

import java.util.List;

public interface VideoService {
    String saveVideo(Videos video);

    void updateVideoCover(String videoId, String coverPath);

    //分页查询视频列表
    PagedResult getAllVideos(Videos video, Integer isSaveRecord, Integer page, Integer pageSize);

    //热词
    List<String> getHotWords();

    //点赞和取消点赞视频
    void userLikeVideo(String userId, String videoId, String videoCreatorId);

    void userUnlikeVideo(String userId, String videoId, String videoCreatorId);

    //显示收藏的视频
    PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize);

    PagedResult queryMyFollowVideos(String userId, Integer page, int pageSize);

    void saveComment(Comments comment);

    PagedResult getAllComments(String videoId, Integer page, Integer pageSize);
}
