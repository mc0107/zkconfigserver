package com.banclogix.dm2.zkconfigserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.banclogix.dm2.common.entity.ZkConfigurationNodeEntity;
import com.banclogix.dm2.zkconfigserver.config.Configuration;
import com.banclogix.dm2.zkconfigserver.entity.OperationResult;
import com.banclogix.dm2.zkconfigserver.entity.configuration.ConfigurationEntity;
import com.banclogix.dm2.zkconfigserver.entity.connection.ConnectionEntity;
import com.banclogix.dm2.zkconfigserver.service.ZkNodeService;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Created by madl on 2016/5/25.
 */
public class ZkNodeController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkNodeController.class);

    private ZkNodeService zkNodeService = new ZkNodeService();

    public ZkNodeController() {
    }

    /**
     * Show all services in type(/serviceConfig or /infrastructure).
     *
     * @param routingContext
     */
    public void selectConfiguration(RoutingContext routingContext) {
        if (!this.isAuthenticated(routingContext)) {
            return;
        }

        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response();

        String type = request.getParam("type");
        String serviceName = request.getParam("serviceName");

        if (type.equals("/serviceConfig") || type.equals("/infrastructure")) {
            if (serviceName == null || serviceName.length() == 0) {
                List<ConfigurationEntity> configurationEntityList = zkNodeService.selectConfiguration(type);
                LOGGER.info("Select configuration success: type: [{}]", type);
                response.end(JSONObject.toJSONString(configurationEntityList));
            } else {
                List<ZkConfigurationNodeEntity> zkConfigurationNodeEntityList;
                try {
                    zkConfigurationNodeEntityList = zkNodeService.selectConfiguration(type, serviceName);
                } catch (Exception e) {
                    LOGGER.error("Select configuration fail: type: [{}], serviceName: [{}]", type, serviceName);
                    routingContext.fail(Configuration.E_422);
                    return;
                }

                LOGGER.info("Select configuration success: type: [{}], serviceName: [{}]", type, serviceName);
                response.end(JSONObject.toJSONString(zkConfigurationNodeEntityList));
            }
        } else {
            LOGGER.error("Select configuration fail: type: [{}], serviceName: [{}]", type, serviceName);
            routingContext.fail(Configuration.E_422);
            return;
        }
    }

    /**
     * Show all connections.
     *
     * @param routingContext
     */
    public void selectConnection(RoutingContext routingContext) {
        if (!this.isAuthenticated(routingContext)) {
            return;
        }

        HttpServerResponse response = routingContext.response();
        List<ConnectionEntity> connectionEntityList;
        try {
            connectionEntityList = zkNodeService.selectConnection();
            LOGGER.info("Select connections success.");
            response.end(JSONObject.toJSONString(connectionEntityList));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return;
        }
    }

    /**
     * Update service's value in type(/serviceConfig or /infrastructure), service must exist.
     *
     * @param routingContext
     */
    public void updateConfiguration(RoutingContext routingContext) {
        if (!this.isAuthenticated(routingContext)) {
            return;
        }

        HttpServerResponse response = routingContext.response();
        JsonObject jsonObject = routingContext.getBodyAsJson();
        String type = jsonObject.getString("type");
        String serviceName = jsonObject.getString("serviceName");
        String key = jsonObject.getString("key");
        String value = jsonObject.getString("value");

        if ("/serviceConfig".equals(type) || "/infrastructure".equals(type)) {
            try {
                zkNodeService.updateConfiguration(type, serviceName, key, value);
            } catch (Exception e) {
                routingContext.fail(Configuration.E_422);
                return;
            }
            LOGGER.info("Update configuration success: type: [{}], serviceName: [{}], key: [{}], value: [{}]", type, serviceName, key, value);
            response.end(JSONObject.toJSONString(new OperationResult(new Date())));
        } else {
            LOGGER.error("Update configuration fail: type: [{}]", type);
            routingContext.fail(Configuration.E_422);
            return;
        }
    }
}