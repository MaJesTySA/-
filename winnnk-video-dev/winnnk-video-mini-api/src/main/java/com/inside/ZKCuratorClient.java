package com.inside;

import com.inside.config.ResourceConfig;
import com.inside.enums.BGMOperatorTypeEnum;
import com.inside.utils.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.Map;

@Component
public class ZKCuratorClient {
    //ZK客户端
    private CuratorFramework client = null;
    @Autowired
    private ResourceConfig resourceConfig;

    public void init() {
        if (client != null)
            return;
        //重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);
        //创建zk客户端
        client = CuratorFrameworkFactory.builder().connectString(resourceConfig.getZookeeperServer()).
                sessionTimeoutMs(10000).retryPolicy(retryPolicy).namespace("admin").build();
        client.start();
        try {
            addChildWatch("/bgm");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addChildWatch(String nodePath) throws Exception {
        final PathChildrenCache cache = new PathChildrenCache(client, nodePath, true);
        cache.start();
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                //无论添加还是删除，都会添加一个节点
                if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                    //获取ZK添加的bgmId
                    String path = event.getData().getPath();
                    String optObj = new String(event.getData().getData(), "UTF-8");
                    //字符串转成Map
                    Map<String, String> map = JsonUtils.jsonToPojo(optObj, Map.class);
                    String optType = map.get("optType");
                    //bgmPath = /bgm/music.mp3
                    String bgmPath = map.get("path");
                    String filePath = resourceConfig.getFileSpace() + bgmPath;
                    String bgmUrl = resourceConfig.getBgmServer() + bgmPath;
                    if (optType.equals(BGMOperatorTypeEnum.ADD.type)) {
                        //下载BGM到SpringBoot服务器
                        URL url = new URL(bgmUrl);
                        File file = new File(filePath);
                        FileUtils.copyURLToFile(url, file);
                        //删除ZK服务器上已经处理的节点
                        client.delete().forPath(path);
                    } else if (optType.equals(BGMOperatorTypeEnum.DELETE.type)) {
                        File file = new File(filePath);
                        FileUtils.forceDelete(file);
                        client.delete().forPath(path);
                    }
                }
            }
        });
    }
}
