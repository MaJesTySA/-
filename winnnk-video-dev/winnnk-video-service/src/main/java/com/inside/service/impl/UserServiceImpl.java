package com.inside.service.impl;

import com.inside.mapper.UsersFansMapper;
import com.inside.mapper.UsersLikeVideosMapper;
import com.inside.mapper.UsersMapper;
import com.inside.mapper.UsersReportMapper;
import com.inside.pojo.Users;
import com.inside.pojo.UsersFans;
import com.inside.pojo.UsersLikeVideos;
import com.inside.pojo.UsersReport;
import com.inside.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;
    @Autowired
    private UsersFansMapper usersFansMapper;
    @Autowired
    private UsersReportMapper usersReportMapper;
    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {
        Users user = new Users();
        user.setUsername(username);
        Users result = usersMapper.selectOne(user);
        return result != null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUser(Users user) {
        String userId = sid.nextShort();
        user.setId(userId);
        usersMapper.insert(user);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String password) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("username", username);
        criteria.andEqualTo("password", password);
        return usersMapper.selectOneByExample(userExample);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserInfo(Users user) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id", user.getId());
        usersMapper.updateByExampleSelective(user, userExample);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfo(String userId) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id", userId);
        return usersMapper.selectOneByExample(userExample);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean isUserLikeVideo(String userId, String videoId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(videoId)) {
            return false;
        }
        Example userExample = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("videoId", videoId);
        List<UsersLikeVideos> list = usersLikeVideosMapper.selectByExample(userExample);
        return (list != null && list.size() > 0);
        //return true;
        // return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUserFanRelation(String userId, String fanId) {
        UsersFans userFan = new UsersFans();
        userFan.setId(sid.nextShort());
        userFan.setUserId(userId);
        userFan.setFanId(fanId);
        usersFansMapper.insert(userFan);
        usersMapper.addFansCount(userId);
        usersMapper.addFollowCount(fanId);
    }

    @Override
    public void deleteUserFanRelation(String userId, String fanId) {
        Example userExample = new Example(UsersFans.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fanId", fanId);
        usersFansMapper.deleteByExample(userExample);
        usersMapper.reduceFansCount(userId);
        usersMapper.reduceFollowCount(fanId);
    }

    @Override
    public boolean queryIfFollow(String userId, String fanId) {
        Example userExample = new Example(UsersFans.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fanId", fanId);
        List<UsersFans> list = usersFansMapper.selectByExample(userExample);
        return (list != null && !list.isEmpty() && list.size() > 0);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void reportUser(UsersReport usersReport) {
        usersReport.setId(sid.nextShort());
        usersReport.setCreateDate(new Date());
        usersReportMapper.insert(usersReport);
    }
}
