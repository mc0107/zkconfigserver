package com.banclogix.dm2.zkconfigserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.banclogix.dm2.zkconfigserver.config.Configuration;
import com.banclogix.dm2.zkconfigserver.entity.OperationResult;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by madl on 2016/5/24.
 */
public class AuthenController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenController.class);

    public AuthenController() {
    }

    // {"username":"admin","password":"123456"}
    public void login(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.putHeader(HttpHeaders.CONTENT_TYPE, Configuration.CONTENT_JSON);
        JsonObject jsonObject = routingContext.getBodyAsJson();
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");

        if ("admin".equals(username) && "123456".equals(password)) {
            LOGGER.info("Login success: received correct login request username: [{}], password: [{}]", username, password);
            response.end(JSONObject.toJSONString(new OperationResult(new Date(), Configuration.getToken())));
        } else {
            LOGGER.error("Login fail: received incorrect login request username [{}], password:[{}]", username, password);
            routingContext.fail(Configuration.E_422);
            return;
        }
    }

    public void logout(RoutingContext routingContext) {
        if (!this.isAuthenticated(routingContext)) {
            return;
        }
        LOGGER.info("Logout ...");
        routingContext.response().end(JSONObject.toJSONString(new OperationResult(new Date())));
    }
}
