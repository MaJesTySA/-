package com.inside.controller;

import com.inside.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {
    @Autowired
    public RedisOperator redis;
    public static final String USER_REDIS_SESSION = "user-redis-session";
    public static final String FILE_SPACE = "/usr/projects/winnnkvideo";
    public static final String FFMPEG_EXE = "/home/upload/ffmpeg-4.2-amd64-static/ffmpeg";
    public static final Integer PAGE_SIZE = 5;
}
