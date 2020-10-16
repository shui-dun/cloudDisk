package com.shuidun;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * 获取百度智能云API的token类
 */
public class AuthService {
    /**
     * 缓存的token
     */
    private static String auth;

    /**
     * toker过期的时间戳，即1970年1月1日0分0秒至token过期时刻经过的秒数
     */
    private static Long expireTimestamp;

    /**
     * 判断是否token过期
     */
    private static boolean expire() {
        long cur = System.currentTimeMillis() / 1000;
        return (cur - 100) >= expireTimestamp;
    }

    /**
     * 得到token，如果缓存的token没有过期，则直接从缓存中取，否则，从百度服务器获取token
     */
    public static String getAuth() {
        if (auth == null || expire()) {
            String clientId = Property.getClientId();
            String clientSecret = Property.getClientSecret();
            JSONObject s = getAuth(clientId, clientSecret);
            auth = s.getString("access_token");
            expireTimestamp = System.currentTimeMillis() / 1000 + s.getLong("expires_in");
        }
        return auth;
    }

    /**
     * 从百度服务器获取token
     */
    public static JSONObject getAuth(String ak, String sk) {
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                + "grant_type=client_credentials"
                + "&client_id=" + ak
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            Map<String, List<String>> map = connection.getHeaderFields();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            return new JSONObject(result.toString());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return null;
    }
}