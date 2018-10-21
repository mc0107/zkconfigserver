package com.zkconfigserver.service;

import com.alibaba.fastjson.JSONObject;
import com.zkconfigserver.config.Configuration;
import com.zkconfigserver.entity.configuration.ConfigurationEntity;
import com.zkconfigserver.entity.connection.ConnectionEntity;
import com.zkconfigserver.entity.connection.ConnectionNodeEntity;
import com.zkconfigserver.factory.ZkNodeFactory;
import com.zkconfigserver.util.ZkUtil;
import com.zkconfigserver.entity.ZkConfigurationNodeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madali on 2016/5/25.
 */
public class ZkNodeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkNodeService.class);

    public ZkNodeService() {
    }

    /**
     * Show all services and amount when serviceName is null.
     *
     * @param type
     * @return
     */
    public List<ConfigurationEntity> selectConfiguration(String type) {
        List<ConfigurationEntity> configurationEntityList = new ArrayList<>();
        ConfigurationEntity configurationEntity;

        try {
            List<String> serviceNames = ZkUtil.getChildren(type);
            for (String serviceName : serviceNames) {
                configurationEntity = new ConfigurationEntity(serviceName, 0);
                List<String> childrenList = ZkUtil.getChildren(type + "/" + serviceName);
                if (childrenList != null && childrenList.size() > 0)
                    configurationEntity = new ConfigurationEntity(serviceName, childrenList.size());
                configurationEntityList.add(configurationEntity);
            }
            return configurationEntityList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Show all key and value when serviceName is not null.
     *
     * @param type
     * @param serviceName
     * @return
     */
    public List<ZkConfigurationNodeEntity> selectConfiguration(String type, String serviceName) {
        List<ZkConfigurationNodeEntity> zkConfigurationNodeEntityList = new ArrayList<>();
        ZkConfigurationNodeEntity zkConfigurationNodeEntity;

        try {
            List<String> keys = ZkUtil.getChildren(type + "/" + serviceName);
            for (String key : keys) {
                String value = new String(ZkUtil.getData(type + "/" + serviceName + "/" + key), "utf-8");
                zkConfigurationNodeEntity = new ZkConfigurationNodeEntity(key, value);
                zkConfigurationNodeEntityList.add(zkConfigurationNodeEntity);
            }
            return zkConfigurationNodeEntityList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Show all connections.
     *
     * @return
     */
    public List<ConnectionEntity> selectConnection() {
        List<ConnectionEntity> connectionEntityList = new ArrayList<>();
        String connectionPath = ZkNodeFactory.getAllNodeMap().get("zookeeper.root.connected");
        ConnectionEntity connectionEntity;

        //serviceNames: zookeeper-node.properties中配置的所有connection
        List<String> serviceNames;

        try {
            serviceNames = ZkUtil.getChildren(connectionPath);
            //没有connection  return
            if (serviceNames == null || serviceNames.size() == 0) {
                return connectionEntityList;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //有connection   遍历
        for (String serviceName : serviceNames) {
            try {
                List<String> nodePaths = ZkUtil.getChildren(connectionPath + "/" + serviceName);

                if (nodePaths == null || nodePaths.size() == 0) {
                    //节点没有数据：将serviceName赋给connectionEntity，添加connectionEntity到connectionEntityList中
                    connectionEntity = new ConnectionEntity(serviceName);
                    connectionEntityList.add(connectionEntity);
                } else {
                    //节点有数据（Ip，Port，State）：遍历，将serviceName和节点数据赋给connectionEntity，添加connectionEntity到connectionEntityList中
                    List<ConnectionNodeEntity> connections = new ArrayList<>();
                    ConnectionNodeEntity connectionNodeEntity;
                    for (String nodePath : nodePaths) {
                        String path = connectionPath + "/" + serviceName + "/" + nodePath;

                        String ipPortState = new String(ZkUtil.getData(path), "UTF-8");
                        JSONObject jsonObject = JSONObject.parseObject(ipPortState);
                        String ip = jsonObject.getString("Ip");
                        int port = jsonObject.getIntValue("Port");
                        String state = Configuration.getConnectionStateMap().get(jsonObject.getIntValue("State"));

                        connectionNodeEntity = new ConnectionNodeEntity(ip, port, state);
                        connections.add(connectionNodeEntity);
                    }
                    connectionEntity = new ConnectionEntity(serviceName, connections);
                    connectionEntityList.add(connectionEntity);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return connectionEntityList;
    }

    /**
     * Update service's value.
     *
     * @param type
     * @param serviceName
     * @param key
     * @param value
     */
    public void updateConfiguration(String type, String serviceName, String key, String value) {
        if (serviceName == null || serviceName.length() == 0 || key == null || key.length() == 0) {
            LOGGER.error("Update configuration fail: type: [{}], serviceName: [{}], key: [{}]", type, serviceName, key);
            throw new RuntimeException();
        }

        String path = type + "/" + serviceName + "/" + key;
        try {
            if (ZkUtil.checkNode(path)) {
                ZkUtil.setData(path, value);
            } else {
                //获取节点：节点不存在时，返回
                LOGGER.error("There is no zookeeper node named: type: [{}], serviceName: [{}], key: [{}]", type, serviceName, key);
                throw new RuntimeException();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
