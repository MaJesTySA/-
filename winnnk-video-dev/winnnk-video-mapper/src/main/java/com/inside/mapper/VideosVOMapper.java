package com.inside.mapper;

import com.inside.pojo.Videos;
import com.inside.pojo.vo.VideosVO;
import com.inside.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VideosVOMapper extends MyMapper<Videos> {
    List<VideosVO> queryAllVideos(@Param("videoDesc") String videoDesc,
                                  @Param("userId") String userId);

    //喜欢数累加和累减
    void addVideoLikeCount(String videoId);

    void reduceVideoLikeCount(String videoId);

    List<VideosVO> queryMyLikeVideos(String userId);

    List<VideosVO> queryMyFollowVideos(String userId);
}