package com.zkconfigserver.service;

import com.zkconfigserver.util.ZkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by madali on 2016/6/13.
 */
public class NodeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkNodeService.class);

    //默认的环境变量中的初始化标记为false，表示不使用properties配置文件中的配置覆盖现有的服务中的配置。
    private static final boolean DEFAULT_INITIAL_FLAG = false;

    public NodeService() {
    }

    /**
     * 创建节点
     *
     * @param path 节点路径
     * @param data 节点数据
     * @throws Exception
     */
    public void createNode(String path, String data) throws Exception {

        //不管环境变量中的初始化标记是什么，只要properties配置文件中的配置存在而zookeeper中对应节点不存在，就都会在zookeeper中创建节点。
        if (!ZkUtil.checkNode(path)) {
            if (data == null || data.length() == 0) {
                ZkUtil.addNode(path);
                LOGGER.info("create Node: " + path);
            } else {
                ZkUtil.addNode(path, data);
                LOGGER.info("create Node: " + path);
            }
        } else {

            boolean initialFlag;

            if (System.getenv("INITIAL_FLAG") != null) {
                try {
                    initialFlag = Boolean.valueOf(System.getenv("INITIAL_FLAG"));
                } catch (Exception e) {
                    initialFlag = DEFAULT_INITIAL_FLAG;
                }
            } else
                initialFlag = DEFAULT_INITIAL_FLAG;

            //只有当环境变量中的初始化标记为true，并且properties配置文件中的data不为空时，才会使用properties配置文件中的配置覆盖现有的服务中的配置（溯源）；否则不作处理
            if (initialFlag && data != null && data.length() > 0) {
                ZkUtil.setData(path, data);
                LOGGER.info("set data : path: [{}], data: [{}]", path, data);
            }

        }
    }

    /**
     * 删除path路径下的所有节点
     *
     * @param path 节点路径
     * @throws Exception
     */
    public void deleteNode(String path) throws Exception {
        if (path == null || !ZkUtil.checkNode(path)) {
            return;
        }

        List<Map.Entry<String, String>> list = ZkUtil.listNode(path);
        if (list == null || list.size() == 0) {
            LOGGER.info("delete node: " + path);
            ZkUtil.deleteNode(path);
            return;
        }

        for (Map.Entry<String, String> entry : list) {
            deleteNode(entry.getKey());
        }

        LOGGER.info("delete node: " + path);
        //循环删除path路径下的子节点之后，删除path节点。
        ZkUtil.deleteNode(path);
    }

}
