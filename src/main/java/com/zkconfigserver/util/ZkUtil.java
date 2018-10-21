package com.zkconfigserver.util;

import com.zkconfigserver.config.Configuration;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by madali on 2017/4/27.
 */
public class ZkUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkUtil.class);

    private static final String NAMESPACE = "zk_vertx";
    public static final String ZOOKEEPER_HOST;
    protected static final CuratorFramework client;

    static {
        ZOOKEEPER_HOST = Configuration.ZOOKEEPER_HOST;

        client = CuratorFrameworkFactory.builder()
                .connectString(ZOOKEEPER_HOST)
                .namespace(NAMESPACE)
                .retryPolicy(new RetryNTimes(3, 1000))
                .build();
    }

    public static Map.Entry<String, String> getNode(String path) {
        Map.Entry<String, String> entry;

        try {
            byte[] data = client.getData().forPath(path);
            entry = new AbstractMap.SimpleEntry<>(path, new String(data, "UTF-8"));
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }

        return entry;
    }

    public static List<Map.Entry<String, String>> listNode(String path) {
        List<Map.Entry<String, String>> entryList = new ArrayList<>();

        try {
            List<String> list = client.getChildren().forPath(path);
            for (int i = 0; i < list.size(); i++) {
                String childPath = path + "/" + list.get(i);
                Map.Entry<String, String> entry = getNode(childPath);
                entryList.add(entry);
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }

        return entryList;
    }

    public static boolean checkNode(String path) throws Exception {

        return client.checkExists().forPath(path) != null;
    }

    public static void connect() {
        client.start();
        LOGGER.info("Connect to zookeeper (host: {}).", ZOOKEEPER_HOST);
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
        client.delete().forPath(path);
    }

    public static List<String> getChildren(String path) throws Exception {
        return client.getChildren().forPath(path);
    }

}
