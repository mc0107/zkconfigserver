package com.zkconfigserver.factory;

import com.zkconfigserver.service.NodeService;
import com.zkconfigserver.util.ZkUtil;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by madali on 2016/5/25.
 */
public class ZkNodeFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkNodeFactory.class);

    private static final Map<String, String> allNodeMap = new HashMap<>();

    private static final Map<String, String> allConfigMap = new HashMap<>();

    private static final Set<String> nodeKeySet;

    private static final Set<String> configKeySet;

    private static final List<String> rootNodeList = new ArrayList<>();

    private static final List<String> connectedNodeList = new ArrayList<>();

    private static final List<String> infrastructureNodeList = new ArrayList<>();

    private static final List<String> serviceConfigNodeList = new ArrayList<>();

    private static final List<String> serviceConfigNodeNames = new ArrayList<>();

    private static NodeService nodeService = new NodeService();

    //读取zookeeper-node.properties配置文件
    private static PropertiesConfiguration nodeProp;

    //读取zookeeper-config.properties配置文件
    private static PropertiesConfiguration configProp;

    static {
        // 读取环境变量中的DEFAULT_LIST_DELIMITER  取其第一个字符为apache.commons的分隔符
        String d = System.getenv("DEFAULT_LIST_DELIMITER");

        char defaultListDelimiter = d == null ? '-' : d.charAt(0);

        // 使用apache.commons的读取properties文件方法 需先修改默认的分隔符,为-  这样properties文件中,前就不用添加转义字符\
        AbstractConfiguration.setDefaultListDelimiter(defaultListDelimiter);

        try {
            nodeProp = new PropertiesConfiguration("zookeeper-node.properties");
            configProp = new PropertiesConfiguration("zookeeper-config.properties");

            nodeProp.setAutoSave(true);
            configProp.setAutoSave(true);
        } catch (ConfigurationException e) {
            LOGGER.error("Init zookeeper properties fail: ", e);
            throw new RuntimeException(e);
        }

        nodeProp.getKeys().forEachRemaining(key -> allNodeMap.put(key, nodeProp.getString(key)));
        configProp.getKeys().forEachRemaining(key -> allConfigMap.put(key, configProp.getString(key)));
        nodeKeySet = allNodeMap.keySet();
        configKeySet = allConfigMap.keySet();
    }

    private static void initRootNode() throws Exception {
        for (String key : nodeKeySet) {
            if (key.startsWith("zookeeper.root")) {
                String rootNode = allNodeMap.get(key);
                nodeService.createNode(rootNode, null);
                rootNodeList.add(rootNode);
            }
        }
    }

    private static void initInfrastructureNode() throws Exception {
        String infrastructureNode = allNodeMap.get("zookeeper.root.infrastructure");
        for (String key : nodeKeySet) {
            if (key.startsWith("zookeeper.infrastructure")) {
                String infrastructureNodePath = infrastructureNode + allNodeMap.get(key);
                nodeService.createNode(infrastructureNodePath, null);
                infrastructureNodeList.add(infrastructureNodePath);
            }
        }
    }

    private static void initServiceNode() throws Exception {
        String connectedNode = allNodeMap.get("zookeeper.root.connected");
        String serviceConfigNode = allNodeMap.get("zookeeper.root.serviceConfig");
        for (String key : nodeKeySet) {
            if (key.startsWith("zookeeper.service")) {
                String node = allNodeMap.get(key);
                String connectedNodePath = connectedNode + node;
                String serviceConfigNodePath = serviceConfigNode + node;
                nodeService.createNode(connectedNodePath, null);
                connectedNodeList.add(connectedNodePath);
                nodeService.createNode(serviceConfigNodePath, null);
                serviceConfigNodeList.add(serviceConfigNodePath);
                serviceConfigNodeNames.add(node.substring(node.indexOf("/") + 1));
            }
        }
    }

    private static void initInfrastructureConfigNode() throws Exception {
        String infrastructureNode = allNodeMap.get("zookeeper.root.infrastructure");
        for (String key : nodeKeySet) {
            if (key.startsWith("zookeeper.infrastructure")) {
                for (String s : nodeKeySet) {
                    if (s.startsWith(key.substring(key.lastIndexOf(".") + 1))) {
                        String path = infrastructureNode + allNodeMap.get(key) + allNodeMap.get(s);
                        String data = allConfigMap.get(s);
                        nodeService.createNode(path, data);
                    }
                }
            }
        }
    }

    private static void initServiceConfigNode() throws Exception {
        String serviceConfigNode = allNodeMap.get("zookeeper.root.serviceConfig");
        for (String configName : serviceConfigNodeNames) {
            for (String configKey : configKeySet) {
                if (configKey.startsWith(configName)) {
                    int index = configKey.indexOf(".");
                    String path = serviceConfigNode + "/" + configKey.substring(0, index) + "/" + configKey.substring(index + 1);
                    nodeService.createNode(path, allConfigMap.get(configKey));
                }
            }
        }
    }

    private static void updateStartSign() throws Exception {
        String startSignNode = allNodeMap.get("zookeeper.root.startSign");
        ZkUtil.setData(startSignNode, String.valueOf(true));
        LOGGER.info("The zookeeper node named \"" + startSignNode + "\" have been updated.");
    }

    private static void destroyAllNodes() throws Exception {
        nodeService.deleteNode("/serviceConfig");
        nodeService.deleteNode("/infrastructure");
        nodeService.deleteNode("/connected");
        nodeService.deleteNode("/startSign");
        nodeService.deleteNode("/");
    }

    private static void destroyAllExistNodes() throws Exception {
        nodeService.deleteNode("/serviceConfig");
        nodeService.deleteNode("/infrastructure");
        nodeService.deleteNode("/connected");
        nodeService.deleteNode("/startSign");
    }

    public static Map<String, String> getAllNodeMap() {
        return allNodeMap;
    }

    /**
     * 创建NAMESPACE（zk_vertx）下所有的节点结构与默认数据
     */
    public static void buildNode() {
        try {
            ZkUtil.connect();

            // 强制删除已经存在的所有节点，然后再重新建立节点（不推荐使用）
//            destroyAllExistNodes();
//            LOGGER.info("All existed nodes has been removed.");

            initRootNode();
            initInfrastructureNode();
            initServiceNode();
            initInfrastructureConfigNode();
            initServiceConfigNode();
            updateStartSign();
            LOGGER.info("All default nodes and data have been initialized.");
        } catch (Exception e) {
            try {
                LOGGER.error("error : ", e);

                //如果该服务在启动中出现错误会删除已创建的节点结构,直到下一次服务被正确启动
                destroyAllNodes();
                LOGGER.info("All default nodes and data have been destroyed.");
            } catch (Exception ex) {
                LOGGER.error("error : ", e);
                throw new RuntimeException(e);
            }
        }
    }
}
