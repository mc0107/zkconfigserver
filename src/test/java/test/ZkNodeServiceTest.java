package test;

import com.alibaba.fastjson.JSONObject;
import com.zkconfigserver.entity.ZkConfigurationNodeEntity;
import com.zkconfigserver.entity.configuration.ConfigurationEntity;
import com.zkconfigserver.entity.connection.ConnectionEntity;
import com.zkconfigserver.service.ZkNodeService;
import com.zkconfigserver.util.ZkUtil;

import java.util.List;

/**
 * Created by madali on 2016/5/25.
 */
public class ZkNodeServiceTest {

    static ZkNodeService nodeService = new ZkNodeService();

    private static void test1() throws Exception {
        //  connected
        List<ConnectionEntity> list = nodeService.selectConnection();
        for (ConnectionEntity entity : list) {
            System.out.println(JSONObject.toJSONString(entity));
        }
        System.out.println(JSONObject.toJSONString(list));
        System.out.println("----------------");

        List<String> serviceList = ZkUtil.getChildren("/connected/customerService");
        for (String str : serviceList) {
            System.out.println(str);
        }

    }

    private static void test2() {
        // serviceConfig infrastructure
        List<ConfigurationEntity> list = nodeService.selectConfiguration("/serviceConfig");
        for (ConfigurationEntity entity : list) {
            System.out.println(JSONObject.toJSONString(entity));
        }
        System.out.println("----------------");

        List<ZkConfigurationNodeEntity> list2 = nodeService.selectConfiguration("/serviceConfig", "masterService");
        for (ZkConfigurationNodeEntity entity : list2) {
            System.out.println(JSONObject.toJSONString(entity));
        }
    }

    private static void test3() throws Exception {
        nodeService.updateConfiguration("/infrastructure", "mysql", "username", "admin");
        nodeService.updateConfiguration("/serviceConfig", "customerService", "ice.port", "49099");

        String path = "HOST" + "/zk_vertx/infrastructure/redis5000";
        ZkUtil.deleteNode(path);
    }
}
