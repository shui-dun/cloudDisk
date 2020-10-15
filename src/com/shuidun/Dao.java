package com.shuidun;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Random;

/**
 * 数据库连接类
 */
public class Dao {

    /**
     * 获取数据库连接Connection对象
     */
    private static Connection getConnection() throws NamingException, SQLException {
        Context initContext = new InitialContext();
        Context envContext = (Context) initContext.lookup("java:/comp/env");
        DataSource ds = (DataSource) envContext.lookup("jdbc/cloudDisk");
        return ds.getConnection();
    }

    /**
     * 获取人脸图片的base64码
     *
     * @param name 用户名
     * @return 该用户的人脸图片的base64码
     */
    public static String imgBase64(String name) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("select img from pic where name=?;")) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("img");
                }
            }
        } catch (SQLException | NamingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 验证密码正误
     *
     * @param name   用户名
     * @param passwd 密码的明文
     * @return 密码是否正确
     */
    public static boolean testPasswd(String name, String passwd) {
        String encrypted, salt;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("select passwd,salt from user where name=?;")) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    encrypted = rs.getString("passwd");
                    salt = rs.getString("salt");
                    String encrypted2 = Base64.getEncoder().encodeToString(encrypt(passwd, Base64.getDecoder().decode(salt)));
                    if (encrypted.equals(encrypted2)) {
                        return true;
                    }
                }
            }
        } catch (SQLException | NamingException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断用户是否存在
     *
     * @param name 用户名
     * @return 该用户名在数据库中是否存在
     */
    public static boolean existsUser(String name) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("select name from user where name=?;")) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException | NamingException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 添加用户
     *
     * @param name   用户名
     * @param passwd 密码的明文
     * @param pic    人脸照片的base64码
     * @return 是否添加成功
     */
    public static boolean addUser(String name, String passwd, String pic) {
        byte[] salt = salt(8);
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String encrypted = Base64.getEncoder().encodeToString(encrypt(passwd, salt));
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("insert into user values(?,?,?);");
             PreparedStatement pstmt2 = conn.prepareStatement("insert into pic values(?,?);")) {
            pstmt.setString(1, name);
            pstmt.setString(2, saltBase64);
            pstmt.setString(3, encrypted);
            pstmt.executeUpdate();
            pstmt2.setString(1, name);
            pstmt2.setString(2, pic);
            pstmt2.executeUpdate();
            return true;
        } catch (SQLException | NamingException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 使用SHA-256对密码和盐进行哈希加密
     *
     * @param plain 密码明文字符串
     * @param salt  盐的字节数组
     * @return 加密结果的字节数组
     */
    private static byte[] encrypt(String plain, byte[] salt) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        digest.update(plain.getBytes());
        digest.update(salt);
        return digest.digest();
    }

    /**
     * 生成随机的盐
     *
     * @param length 盐的长度（字节）
     * @return 生成的盐
     */
    private static byte[] salt(int length) {
        byte[] bytes = new byte[length];
        new Random().nextBytes(bytes);
        return bytes;
    }
}
