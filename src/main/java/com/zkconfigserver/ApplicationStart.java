package com.zkconfigserver;

import com.zkconfigserver.factory.ZkNodeFactory;
import com.zkconfigserver.verticle.MainVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by madali on 2016/5/23.
 */
public class ApplicationStart {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationStart.class);

    public static void main(String[] args) {
        LOGGER.info("初始化配置,并构建zk节点...");
        ZkNodeFactory.buildNode();

        LOGGER.info("初始化Vertx Web...");
        new MainVerticle().start();
    }
}
