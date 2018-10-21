package com.zkconfigserver.entity.connection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madali on 2016/5/25.
 */
public class ConnectionEntity {

    private final String serviceName;
    private List<ConnectionNodeEntity> connections = new ArrayList<>();

    public ConnectionEntity(String serviceName) {
        this.serviceName = serviceName;
    }

    public ConnectionEntity(String serviceName, List<ConnectionNodeEntity> connections) {
        this.serviceName = serviceName;
        this.connections = connections;
    }

    public List<ConnectionNodeEntity> getConnections() {
        return connections;
    }

    public String getServiceName() {
        return serviceName;
    }
}