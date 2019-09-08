package com.inside.controller;

import com.inside.pojo.Users;
import com.inside.pojo.vo.UsersVO;
import com.inside.service.UserService;
import com.inside.utils.JSONResult;
import com.inside.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Api(value = "用户注册登录接口", tags = {"注册和登录的Controller"})
public class RegistLoginController extends BasicController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户注册", notes = "用户注册的接口")
    @PostMapping("/regist")
    public JSONResult regist(@RequestBody Users user) throws Exception {
        //1. 判断用户名和密码不为空
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            return JSONResult.errorMsg("用户名和密码不能为空");
        }
        //2. 判断用户名是否存在
        boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());
        //3. 保存用户注册信息
        if (!usernameIsExist) {
            user.setNickname(user.getUsername());
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            user.setFansCounts(0);
            user.setFollowCounts(0);
            user.setReceiveLikeCounts(0);
            user.setFaceImage("/default/avatar/noneface.png");
            userService.saveUser(user);
        } else {
            return JSONResult.errorMsg("用户名已经存在！");
        }
        user.setPassword("");
        UsersVO userVO = setUserRedisSessionToken(user);
        return JSONResult.ok(userVO);
    }

    @ApiOperation(value = "用户登录", notes = "用户登录的接口")
    @PostMapping("/login")
    public JSONResult login(@RequestBody Users user) throws Exception {
        String username = user.getUsername();
        String password = user.getPassword();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return JSONResult.ok("用户名和密码不能为空");
        }
        Users userResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));
        if (userResult != null) {
            userResult.setPassword("");
            UsersVO userVO = setUserRedisSessionToken(userResult);
            return JSONResult.ok(userVO);
        } else {
            return JSONResult.errorMsg("用户名或者密码不正确，请重试");
        }
    }

    @ApiOperation(value = "用户注销", notes = "用户注销的接口")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String",
            paramType = "query")
    @PostMapping("/logout")
    public JSONResult logout(String userId) {
        redis.del(USER_REDIS_SESSION + ":" + userId);
        return JSONResult.ok();
    }

    private UsersVO setUserRedisSessionToken(Users user) {
        //用户的唯一token
        String uniqueToken = UUID.randomUUID().toString();
        redis.set(USER_REDIS_SESSION + ":" + user.getId(), uniqueToken, 1000 * 60 * 30);
        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setUserToken(uniqueToken);
        return userVO;
    }
}
