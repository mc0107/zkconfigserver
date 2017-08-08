package com.banclogix.dm2.zkconfigserver.entity.configuration;

/**
 * Created by madl on 2016/6/1.
 */
public class ConfigurationEntity {

    private final String serviceName;
    private final int amount;

    public ConfigurationEntity(String serviceName, int amount) {
        this.serviceName = serviceName;
        this.amount = amount;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getAmount() {
        return amount;
    }
}
