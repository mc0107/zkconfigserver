package com.zkconfigserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.zkconfigserver.config.Configuration;
import com.zkconfigserver.entity.OperationResult;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by madali on 2016/5/25.
 */
public class ExceptionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionController.class);

    public ExceptionController() {
    }

    public void error(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.putHeader(HttpHeaders.CONTENT_TYPE, Configuration.CONTENT_JSON);

        OperationResult operationResult = new OperationResult();
        operationResult.setSuccess(false);
        operationResult.setTimestamp(new Date());

        int statusCode = routingContext.statusCode() == -1 ? 422 : routingContext.statusCode();
        //设置错误Code
        operationResult.setStatusCode(statusCode);
        response.setStatusCode(statusCode);
        //设置错误信息
        operationResult.setMessage(Configuration.getErrorCodeMap().get(statusCode));
        LOGGER.error("Operate request fail: {}", JSONObject.toJSONString(operationResult));
        response.end(JSONObject.toJSONString(operationResult));
    }
}
