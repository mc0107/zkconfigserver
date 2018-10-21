package com.zkconfigserver.entity.connection;

/**
 * Created by madali on 2016/6/3.
 */
public class ConnectionNodeEntity {

    private final String ip;
    private final int port;
    private final String state;

    public ConnectionNodeEntity(String ip, int port, String state) {
        this.ip = ip;
        this.port = port;
        this.state = state;
    }

    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    public String getState() {
        return state;
    }
}
