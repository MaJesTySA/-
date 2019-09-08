package com.inside.mapper;

import com.inside.pojo.Comments;
import com.inside.pojo.vo.CommentsVO;
import com.inside.utils.MyMapper;

import java.util.List;

public interface CommentsVOMapper extends MyMapper<Comments> {
    List<CommentsVO> queryComments(String videoId);

}