package com.inside.controller;

import com.inside.enums.VideoStatusEnum;
import com.inside.pojo.Bgm;
import com.inside.pojo.Comments;
import com.inside.pojo.Videos;
import com.inside.service.impl.BgmServiceImpl;
import com.inside.service.impl.VideoServiceImpl;
import com.inside.utils.JSONResult;
import com.inside.utils.MergeVideoBgm;
import com.inside.utils.PagedResult;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@Api(value = "视频相关业务的接口", tags = {"视频相关业务的controller"})
@RestController
@RequestMapping("/video")
public class VideoController extends BasicController {
    @Autowired
    private BgmServiceImpl bgmService;
    @Autowired
    private VideoServiceImpl videoService;


    @ApiOperation(value = "视频上传", notes = "视频上传的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id",
                    required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "bgmId", value = "背景音乐id",
                    required = false, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoSeconds", value = "视频时长",
                    required = true, dataType = "double", paramType = "form"),
            @ApiImplicitParam(name = "videoWidth", value = "视频宽度",
                    required = true, dataType = "int", paramType = "form"),
            @ApiImplicitParam(name = "videoHeight", value = "视频高度",
                    required = true, dataType = "int", paramType = "form"),
            @ApiImplicitParam(name = "desc", value = "视频描述",
                    required = false, dataType = "String", paramType = "form")
    })
    @PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
    public JSONResult upload(String userId, String bgmId, double videoSeconds,
                             int videoWidth, int videoHeight, String desc,
                             @ApiParam(value = "短视频", required = true)
                                     MultipartFile file) throws Exception {
        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("用户id不能为空");
        }
        String uploadPathDB = "/" + userId + "/video";
        String coverPathDB = "/" + userId + "/video";

        String videoInputPath = "";
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            if (file != null) {
                //tmp_9604221.mp4
                String fileName = file.getOriginalFilename();
                //这里能有问题，因为上传文件可能有共同的前缀，比如wx123456.789.mp4 wx123456.987.mp4，所以要根据最后一个.来提取
                //String prefixFileName=fileName.split("\\.")[0];
                String prefixFileName = fileName.substring(0, fileName.lastIndexOf('.'));
                if (StringUtils.isNotBlank(fileName)) {
                    //视频文件保存地址   usr/projects/winnkvideo/userId/video/tmp_9604221.mp4
                    videoInputPath = FILE_SPACE + uploadPathDB + "/" + fileName;
                    //数据库保存路径   usrId/video/tmp_9604221.mp4
                    uploadPathDB += ("/" + fileName);
                    //tmp_9604221.jpg
                    coverPathDB += ("/" + prefixFileName + ".jpg");
                    File outFile = new File(videoInputPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        //创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
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
        //判断bgmId是否为空，如果不为空，就查询BGM信息，合并视频
        if (StringUtils.isNotBlank(bgmId)) {
            Bgm bgm = bgmService.queryById(bgmId);
            // /usr/project/winnnkvideo/bgm/Forever.mp3
            String bgmInputPath = FILE_SPACE + bgm.getPath();
            MergeVideoBgm tool = new MergeVideoBgm(FFMPEG_EXE);
            // 合并后的视频名称 ef545b16-fd86-44b0-a2cc-a2ad50c108f0.mp4
            String videoOutputName = UUID.randomUUID().toString() + ".mp4";
            // 合并后的视频数据库保存位置 /userId/video/ef545b16-fd86-44b0-a2cc-a2ad50c108f0.mp4
            uploadPathDB = "/" + userId + "/video/" + videoOutputName;
            //  合并后的视频保存位置 /usr/project/winnnkvideo/userId/video/ef545b16-fd86-44b0-a2cc-a2ad50c108f0.mp4
            String videoOutputPath = FILE_SPACE + uploadPathDB;
            //  Linux下，需要先取消原视频声音，才能合并声音
            String tempFilePath = tool.removeVideoSound(videoInputPath);
            /*
                linux的FFMPEG如果文件不存在，则不能创建文件
                File videoOutputFile = new File(videoOutputPath);
                videoOutputFile.createNewFile();
                要么先创建文件，要么取消-y，这里采用取消-y指令的方法
             */
            // 利用去除声音的原视频跟BGM合并，会输出一个新视频
            tool.convert(tempFilePath, bgmInputPath, videoSeconds, videoOutputPath);
            // 删除去掉声音的视频
            File tempFile = new File(tempFilePath);
            tempFile.delete();

        }

        //对视频进行截图
        MergeVideoBgm tool = new MergeVideoBgm(FFMPEG_EXE);
        tool.getCover(videoInputPath, FILE_SPACE + coverPathDB);

        //删除原文件，节省空间
        if (StringUtils.isNotBlank(bgmId)) {
            File originFile = new File(videoInputPath);
            originFile.delete();
        }

        //保存视频信息到数据库
        Videos video = new Videos();
        video.setAudioId(bgmId);
        video.setUserId(userId);
        video.setVideoSeconds((float) videoSeconds);
        video.setVideoHeight(videoHeight);
        video.setVideoWidth(videoWidth);
        video.setVideoDesc(desc);
        video.setVideoPath(uploadPathDB);
        video.setCoverPath(coverPathDB);
        video.setStatus(VideoStatusEnum.SUCCESS.value);
        video.setCreateTime(new Date());
        String videoId = videoService.saveVideo(video);
        return JSONResult.ok(videoId);
    }

    /*
        isSaveRecord=1，保存。=0不保存。
     */
    @PostMapping("/showAll")
    public JSONResult showAll(@RequestBody Videos video, Integer isSaveRecord, Integer page, Integer pageSize) {
        if (page == null) page = 1;
        if (pageSize == null) pageSize = PAGE_SIZE;
        PagedResult result = videoService.getAllVideos(video, isSaveRecord, page, PAGE_SIZE);
        return JSONResult.ok(result);
    }

    @PostMapping("/showMyLike")
    public JSONResult showMyLike(String userId, Integer page, Integer pageSize) {
        if (StringUtils.isBlank(userId)) return JSONResult.ok();
        if (page == null) page = 1;
        if (pageSize == null) pageSize = 6;
        PagedResult result = videoService.queryMyLikeVideos(userId, page, pageSize);
        return JSONResult.ok(result);
    }

    @PostMapping("/showMyFollow")
    public JSONResult showMyFollow(String userId, Integer page) {
        if (StringUtils.isBlank(userId)) return JSONResult.ok();
        if (page == null) page = 1;
        int pageSize = 6;
        PagedResult result = videoService.queryMyFollowVideos(userId, page, pageSize);
        return JSONResult.ok(result);
    }

    @PostMapping("/hot")
    public JSONResult hot() {
        return JSONResult.ok(videoService.getHotWords());
    }

    @PostMapping("/userLike")
    public JSONResult userLike(String userId, String videoId, String videoCreatorId) {
        videoService.userLikeVideo(userId, videoId, videoCreatorId);
        return JSONResult.ok();
    }

    @PostMapping("/userUnlike")
    public JSONResult userUnlike(String userId, String videoId, String videoCreatorId) {
        videoService.userUnlikeVideo(userId, videoId, videoCreatorId);
        return JSONResult.ok();
    }

    @PostMapping("/saveComment")
    public JSONResult saveComment(@RequestBody Comments comment, String fatherCommentId, String toUserId) {
        comment.setFatherCommentId(fatherCommentId);
        comment.setToUserId(toUserId);
        videoService.saveComment(comment);
        return JSONResult.ok();
    }

    @PostMapping("/getVideoComments")
    public JSONResult getVideoComments(String videoId, Integer page, Integer pageSize) {
        if (StringUtils.isBlank(videoId)) return JSONResult.ok();
        if (page == null) page = 1;
        if (pageSize == null) pageSize = 10;
        PagedResult list = videoService.getAllComments(videoId, page, pageSize);
        return JSONResult.ok(list);
    }
}
