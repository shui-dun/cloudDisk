package com.shuidun;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 下载网盘中的文件
 * <p>
 * 请求：
 * name: 欲下载的文件名
 * <p>
 * 响应:
 * 文件的二进制流
 */
@WebServlet("/download")
public class Download extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        resp.addHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(name, StandardCharsets.UTF_8) + "\"");
        File file = new File(Property.getFolderPath() + name);
        try (FileInputStream stream = new FileInputStream(file);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            int i;
            while ((i = stream.read()) != -1) {
                byteArrayOutputStream.write(i);
            }
            byte[] bytes = byteArrayOutputStream.toByteArray();
            resp.getOutputStream().write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
