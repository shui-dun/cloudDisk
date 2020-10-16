package com.shuidun;

import java.io.IOException;
import java.util.Properties;

/**
 * 读取配置文件，放入内存
 */
public class Property {
    /**
     * 百度智能云的公钥
     */
    private static String clientId;

    /**
     * 百度智能云的私钥
     */
    private static String clientSecret;

    /**
     * 网盘所在的绝对路径
     */
    private static String folderPath;

    static {
        Properties prop = new Properties();
        try {
            prop.load(Property.class.getResourceAsStream("../../../config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientId = prop.getProperty("clientId");
        clientSecret = prop.getProperty("clientSecret");
        folderPath = prop.getProperty("folderPath");
    }

    public static String getClientId() {
        return clientId;
    }

    public static String getClientSecret() {
        return clientSecret;
    }

    public static String getFolderPath() {
        return folderPath;
    }
}
