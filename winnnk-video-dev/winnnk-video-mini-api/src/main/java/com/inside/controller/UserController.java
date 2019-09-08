package com.inside.controller;

import com.inside.pojo.Users;
import com.inside.pojo.UsersReport;
import com.inside.pojo.vo.PublisherVideo;
import com.inside.pojo.vo.UsersVO;
import com.inside.service.UserService;
import com.inside.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@Api(value = "用户相关业务接口", tags = {"用户相关业务的Controller"})
@RequestMapping("/user")
public class UserController extends BasicController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户头像上传", notes = "用于前端上传头像")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")
    @PostMapping("/uploadAvatar")
    public JSONResult uploadAvatar(String userId,
                                   @RequestParam("file") MultipartFile[] files) throws Exception {
        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("用户id不能为空！");
        }
        String uploadPathDB = "/" + userId + "/avatar";
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            if (files != null && files.length > 0) {
                //得到文件名
                String fileName = files[0].getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    //头像文件保存的路径
                    String filePath = FILE_SPACE + uploadPathDB + "/" + fileName;
                    //数据库保存路径
                    uploadPathDB += ("/" + fileName);
                    File outFile = new File(filePath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        //创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }
                    //输出流
                    fileOutputStream = new FileOutputStream(outFile);
                    //输入流，由前端传过来
                    inputStream = files[0].getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            } else {
                return JSONResult.errorMsg("上传出错！");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return JSONResult.errorMsg("上传出错！");
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
        //更新头像地址到数据库
        Users user = new Users();
        user.setId(userId);
        user.setFaceImage(uploadPathDB);
        userService.updateUserInfo(user);
        return JSONResult.ok(uploadPathDB);
    }

    @ApiOperation(value = "用户信息查询", notes = "用户信息查询的接口")
    @ApiImplicitParam(name = "userId", value = "用户id",
            required = true, dataType = "String", paramType = "query")
    @PostMapping("/query")
    public JSONResult query(String userId, String fanId) {
        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("用户id不能为空！");
        }
        Users user = userService.queryUserInfo(userId);
        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setFollow(userService.queryIfFollow(userId, fanId));
        return JSONResult.ok(userVO);
    }

    @PostMapping("/queryPublisher")
    public JSONResult queryPublisher(String loginUserId, String videoId, String publishUserId) {
        if (StringUtils.isBlank(publishUserId))
            return JSONResult.errorMsg("");
        //查询视频发布者的信息
        Users PubUserInfo = userService.queryUserInfo(publishUserId);
        UsersVO PubUserVO = new UsersVO();
        BeanUtils.copyProperties(PubUserInfo, PubUserVO);
        //查询点赞关系
        boolean userLikeVideo = userService.isUserLikeVideo(loginUserId, videoId);
        PublisherVideo publisherVideo = new PublisherVideo();
        publisherVideo.setPublisher(PubUserVO);
        publisherVideo.setUserLikeVideo(userLikeVideo);
        return JSONResult.ok(publisherVideo);

    }

    @PostMapping("/beYourFans")
    public JSONResult beYourFans(String userId, String fanId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) return JSONResult.errorMsg("");
        userService.saveUserFanRelation(userId, fanId);
        return JSONResult.ok("关注成功");
    }

    @PostMapping("/dontBeYourFans")
    public JSONResult dontBeYourFans(String userId, String fanId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) return JSONResult.errorMsg("");
        userService.deleteUserFanRelation(userId, fanId);
        return JSONResult.ok("取消关注");
    }

    @PostMapping("/reportUser")
    public JSONResult reportUser(@RequestBody UsersReport usersReport) {
        userService.reportUser(usersReport);
        return JSONResult.errorMsg("举报成功！等待审核！");
    }
}
