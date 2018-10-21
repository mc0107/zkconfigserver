package com.zkconfigserver.config;

import com.zkconfigserver.util.PropertiesUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by madali on 2016/5/25.
 */
public class Configuration {

    public static final String CONTENT_JSON = "application/json;charset=UTF-8";
    private static final Map<Integer, String> errorCodeMap = new HashMap<>();
    private static final Map<Integer, String> connectionStateMap = new HashMap<>();
    private static final String token = UUID.randomUUID().toString();
    public static final Integer SERVER_PORT = Integer.parseInt(PropertiesUtil.getValue("SERVER_PORT"));
    public static final String ZOOKEEPER_HOST = PropertiesUtil.getValue("ZOOKEEPER_HOST");

    //没有登录
    public static final int E_401 = 401;
    //操作失败：参数有误
    public static final int E_422 = 422;

    static {
        errorCodeMap.put(E_401, "Error：Not login.");
        errorCodeMap.put(E_422, "Operate fail, params is error.");
        connectionStateMap.put(0, "Preparing");
        connectionStateMap.put(1, "WaitLoadingEnsure");
        connectionStateMap.put(2, "Loading");
        connectionStateMap.put(3, "Runing");
        connectionStateMap.put(4, "InsertLoading");
        connectionStateMap.put(5, "DeleteLoading");
        connectionStateMap.put(6, "ClearPreExit");
    }

    public static String getToken() {
        return token;
    }

    public static Map<Integer, String> getErrorCodeMap() {
        return errorCodeMap;
    }

    public static Map<Integer, String> getConnectionStateMap() {
        return connectionStateMap;
    }

}
