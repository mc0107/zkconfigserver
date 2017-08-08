package com.banclogix.dm2.zkconfigserver.main;

import com.banclogix.dm2.zkconfigserver.factory.ZkNodeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by madl on 2016/5/25.
 */
public class MainServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainServer.class);

    public MainServer() {
    }

    public static MainServer getServer() {
        return new MainServer();
    }

    public void start() {
        LOGGER.info("Initialization config and build zookeeper nodes...");
        ZkNodeFactory.buildNode();

        LOGGER.info("Initialization Vertx Web...");
        new MainVerticle().start();
    }
}
