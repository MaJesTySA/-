package com.inside.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.inside.mapper.*;
import com.inside.pojo.Comments;
import com.inside.pojo.SearchRecords;
import com.inside.pojo.UsersLikeVideos;
import com.inside.pojo.Videos;
import com.inside.pojo.vo.CommentsVO;
import com.inside.pojo.vo.VideosVO;
import com.inside.service.VideoService;
import com.inside.utils.PagedResult;
import com.inside.utils.TimeAgoUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideosMapper videosMapper;
    @Autowired
    private VideosVOMapper videosVOMapper;
    @Autowired
    private SearchRecordsMapper searchRecordsMapper;
    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private CommentsMapper commentsMapper;
    @Autowired
    private CommentsVOMapper commentsVOMapper;
    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveVideo(Videos video) {
        String id = sid.nextShort();
        video.setId(id);
        videosMapper.insertSelective(video);
        return id;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateVideoCover(String videoId, String coverPath) {
        Videos video = new Videos();
        video.setId(videoId);
        video.setCoverPath(coverPath);
        videosMapper.updateByPrimaryKeySelective(video);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public PagedResult getAllVideos(Videos video, Integer isSaveRecord, Integer page, Integer pageSize) {
        //保存视频热搜词
        String desc = video.getVideoDesc();
        //用于信息页面的视频查询
        String userId = video.getUserId();
        if (isSaveRecord != null && isSaveRecord == 1) {
            SearchRecords record = new SearchRecords();
            record.setId(sid.nextShort());
            record.setContent(desc);
            searchRecordsMapper.insert(record);
        }
        PageHelper.startPage(page, pageSize);
        List<VideosVO> list = videosVOMapper.queryAllVideos(desc, userId);
        PageInfo<VideosVO> pageList = new PageInfo<>(list);
        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageList.getTotal());
        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<String> getHotWords() {
        return searchRecordsMapper.getHotWords();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userLikeVideo(String userId, String videoId, String videoCreatorId) {
        String likeId = sid.nextShort();
        UsersLikeVideos usersLikeVideos = new UsersLikeVideos();
        usersLikeVideos.setId(likeId);
        usersLikeVideos.setUserId(userId);
        usersLikeVideos.setVideoId(videoId);
        usersLikeVideosMapper.insert(usersLikeVideos);
        videosVOMapper.addVideoLikeCount(videoId);
        usersMapper.addReceiveLikeCount(videoCreatorId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userUnlikeVideo(String userId, String videoId, String videoCreatorId) {
        Example example = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("videoId", videoId);
        usersLikeVideosMapper.deleteByExample(example);
        videosVOMapper.reduceVideoLikeCount(videoId);
        usersMapper.reduceReceiveLikeCount(videoCreatorId);
    }

    @Override
    public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<VideosVO> list = videosVOMapper.queryMyLikeVideos(userId);
        PageInfo<VideosVO> pageList = new PageInfo<>(list);
        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageList.getTotal());
        return pagedResult;
    }

    @Override
    public PagedResult queryMyFollowVideos(String userId, Integer page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<VideosVO> list = videosVOMapper.queryMyFollowVideos(userId);
        PageInfo<VideosVO> pageList = new PageInfo<>(list);
        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageList.getTotal());
        return pagedResult;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComment(Comments comment) {
        comment.setId(sid.nextShort());
        comment.setCreateTime(new Date());
        commentsMapper.insert(comment);
    }

    @Override
    public PagedResult getAllComments(String videoId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<CommentsVO> list = commentsVOMapper.queryComments(videoId);
        for (CommentsVO comment : list) {
            String timeAgo = TimeAgoUtils.format(comment.getCreateTime());
            comment.setTimeAgoStr(timeAgo);
        }
        PageInfo<CommentsVO> pageList = new PageInfo<>(list);
        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageList.getTotal());
        return pagedResult;
    }
}
