package com.inside.service;

import com.inside.pojo.Users;
import com.inside.pojo.UsersReport;

public interface UserService {
    //判断用户名是否存在
    boolean queryUsernameIsExist(String username);

    void saveUser(Users user);

    Users queryUserForLogin(String username, String password);

    void updateUserInfo(Users user);

    //查询用户信息
    Users queryUserInfo(String userId);

    //查询用户是否点赞
    boolean isUserLikeVideo(String userId, String videoId);

    //增加和删除用户跟粉丝的关系
    void saveUserFanRelation(String userId, String fanId);

    void deleteUserFanRelation(String userId, String fanId);

    //是否关注
    boolean queryIfFollow(String userId, String fanId);

    void reportUser(UsersReport usersReport);
}
