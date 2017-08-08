package com.banclogix.dm2.zkconfigserver.dao;

import com.banclogix.dm2.common.util.zookeeper.ZkUtil;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by madl on 2016/5/25.
 */
public class ZkNodeDao extends ZkUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkNodeDao.class);

    public ZkNodeDao() {
    }

    public static void connect() {
        client.start();
        LOGGER.info("Connect to zookeeper (host: {}).", ZOOKEEPER_HOST);
    }

    public static boolean checkNode(String path) throws Exception {
        return ZkUtil.checkNode(path);
    }

    public static void addNode(String path) throws Exception {
        client.create().withMode(CreateMode.PERSISTENT).forPath(path, new byte[]{});
    }

    public static void addNode(String path, String data) throws Exception {
        client.create().withMode(CreateMode.PERSISTENT).forPath(path, data.getBytes("UTF-8"));
    }

    public static byte[] getData(String path) throws Exception {
        // 将客户端的znode视图与zookeeper同步（强制刷新数据）
        client.sync();
        return client.getData().forPath(path);
    }

    public static void setData(String path, String data) throws Exception {
        client.setData().forPath(path, data.getBytes("UTF-8"));
    }

    public static void deleteNode(String path) throws Exception {
        ZkUtil.deleteNode(path);
    }

    public static List<Map.Entry<String, String>> listNode(String path) {
        return ZkUtil.listNode(path);
    }

    public static List<String> getChildren(String path) throws Exception {
        return client.getChildren().forPath(path);
    }
}
