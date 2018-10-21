package com.zkconfigserver.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Auther: madali
 * @Date: 2018/8/28 20:25
 */
public class PropertiesUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtil.class);

    private static final String CONFIG_PATH = "zk.properties";

    private static Properties properties = new Properties();

    static {
        try {
            // 读取resources路径下的文件
            InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(CONFIG_PATH);
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("读取配置文件失败, ErrorMsg:{}", e.getMessage());
        }
    }

    public static String getValue(String propertiesKey) {
        return properties.getProperty(propertiesKey);
    }

}
