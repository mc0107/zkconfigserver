package com.banclogix.dm2.zkconfigserver.controller;

import com.banclogix.dm2.zkconfigserver.config.Configuration;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by madl on 2016/5/25.
 */
public class BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    /**
     * 判断是否进行了身份验证
     *
     * @param routingContext
     * @return
     */
    protected boolean isAuthenticated(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.putHeader(HttpHeaders.CONTENT_TYPE, Configuration.CONTENT_JSON);
        String token = routingContext.request().getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null && token.equals(Configuration.getToken())) {
            return true;
        }
        LOGGER.error("Not login.");
        routingContext.fail(Configuration.E_401);
        return false;
    }
}
