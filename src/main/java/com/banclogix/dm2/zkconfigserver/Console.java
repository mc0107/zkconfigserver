package com.banclogix.dm2.zkconfigserver;

import com.banclogix.dm2.zkconfigserver.main.MainServer;

/**
 * Created by madl on 2016/5/23.
 */
public class Console {

    public static void main(String[] args) {
        MainServer.getServer().start();
    }
}
