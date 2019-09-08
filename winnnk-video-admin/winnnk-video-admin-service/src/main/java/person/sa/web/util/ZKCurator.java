package person.sa.web.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZKCurator {
    //ZK客户端
    private CuratorFramework client = null;
    private final static Logger log = LoggerFactory.getLogger(ZKCurator.class);

    public ZKCurator(CuratorFramework client) {
        this.client = client;
    }

    public void init() {
        //命名空间
        client = client.usingNamespace("admin");
        //是否存在某个节点   /admin/bgm
        try {
            if (client.checkExists().forPath("/bgm") == null) {
                //withMode:zk有两种类型的节点。一种是持久节点，一种是临时节点
                //withACL:ACL默认权限，匿名用户也能访问
                client.create().
                        creatingParentsIfNeeded().
                        withMode(CreateMode.PERSISTENT).
                        withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).
                        forPath("/bgm");
                log.info("zookeeper初始化成功");
            }
        } catch (Exception e) {
            log.error("zookeeper客户端连接、初始化错误");
            e.printStackTrace();
        }
    }

    //添加或者删除bgm，向zk-server创建子节点，供小程序后端监听
    public void sendBgmOperator(String bgmId, String optObj) {

        try {
            client.create().creatingParentsIfNeeded().
                    withMode(CreateMode.PERSISTENT).
                    withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).
                    forPath("/bgm/" + bgmId,optObj.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
