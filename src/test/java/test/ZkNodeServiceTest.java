package test;

import com.alibaba.fastjson.JSONObject;
import com.banclogix.dm2.zkconfigserver.dao.ZkNodeDao;
import com.banclogix.dm2.zkconfigserver.entity.connection.ConnectionEntity;
import com.banclogix.dm2.zkconfigserver.service.ZkNodeService;

import java.util.List;

/**
 * Created by madl on 2016/5/25.
 */
public class ZkNodeServiceTest {

    public static void main(String[] args) {

        ZkNodeService nodeService = new ZkNodeService();

        try {
            ZkNodeDao.connect();

            //  connected
            List<ConnectionEntity> list = nodeService.selectConnection();
            for (ConnectionEntity entity : list) {
                System.out.println(JSONObject.toJSONString(entity));
            }
//            System.out.println(JSONObject.toJSONString(list));

//            List<String> serviceList = ZkNodeDao.getChildren("/connected/customerService");
//            String ipPortState = new String(ZkNodeDao.getData("/connected/customerService/customerservice_1"), "UTF-8");
//            System.out.println(ipPortState);
//            for (String str : serviceList) {
//                System.out.println(str);
//            }

            //  serviceConfig infrastructure
//            List<ConfigurationEntity> list2 = nodeService.selectConfiguration("/serviceConfig");
//            for (ConfigurationEntity entity : list2) {
//                System.out.println(JSONObject.toJSONString(entity));
//            }

//            List<ZkConfigurationNodeEntity> list3 = nodeService.selectConfiguration("/serviceConfig", "masterService");
//            for (ZkConfigurationNodeEntity entity : list3) {
//                System.out.println(JSONObject.toJSONString(entity));
//            }

//            nodeService.updateConfiguration("/infrastructure", "mysql", "username", "admin");
//            nodeService.updateConfiguration("/serviceConfig", "customerService", "ice.port", "49099");


//            "http://10.10.5.20:10011/app/index.html#/infrastructure/redis5000";
            String path = "HOST" + "/dm2/infrastructure/redis5000";
//            ZkNodeDao.deleteNode(path);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
