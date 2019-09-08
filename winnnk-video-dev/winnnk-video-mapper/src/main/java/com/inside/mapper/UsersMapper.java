package com.inside.mapper;

import com.inside.utils.MyMapper;
import com.inside.pojo.Users;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersMapper extends MyMapper<Users> {
    void addReceiveLikeCount(String userId);

    void reduceReceiveLikeCount(String userId);

    //增加、减少粉丝数量
    void addFansCount(String userId);

    void reduceFansCount(String userId);

    //增加、减少关注数
    void addFollowCount(String userId);

    void reduceFollowCount(String userId);

}