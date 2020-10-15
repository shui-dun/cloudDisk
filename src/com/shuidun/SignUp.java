package com.shuidun;

import com.baidu.ai.aip.utils.GsonUtils;
import com.baidu.ai.aip.utils.HttpUtil;
import org.apache.commons.fileupload.FileItem;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 注册账号
 * <p>
 * 请求：
 * name:用户名
 * passwd:密码明文
 * content:人脸照片的base64码
 * <p>
 * 响应：
 * code:状态码
 * msg:状态信息
 */
@WebServlet("/signup")
public class SignUp extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        java.util.List<FileItem> items = Upload.readForm(req);
        if (items == null) {
            resp.getWriter().write(new RespBean(ErrorCode.UNRESOLVED_REQUEST).toJson());
            return;
        }
        if (items.size() != 3) {
            resp.getWriter().write(new RespBean(ErrorCode.N_PARAMETER_ERROR).toJson());
            return;
        }
        String name = items.get(0).getString();
        String passwd = items.get(1).getString();
        String base64 = items.get(2).getString();
        if (name.length() <= 1) {
            resp.getWriter().write(new RespBean(ErrorCode.WEAK_USER_NAME).toJson());
            return;
        }
        if (passwd.length() <= 3) {
            resp.getWriter().write(new RespBean(ErrorCode.WEAK_PASSWD).toJson());
            return;
        }
        if (Dao.existsUser(name)) {
            resp.getWriter().write(new RespBean(ErrorCode.USER_NAME_OCCUPIED).toJson());
            return;
        }
        String ans = FaceDetect.faceDetect(Upload.base64RmHead(base64));
        if (ans == null) {
            resp.getWriter().write(new RespBean(ErrorCode.BAIDU_ERROR).toJson());
            return;
        }
        JSONObject json = new JSONObject(ans);
        if (json.getInt("error_code") != 0) {
            resp.getWriter().write(new RespBean(ErrorCode.BAIDU_ERROR_RET.getCode(), ErrorCode.BAIDU_ERROR_RET.getMsg() + "：" + json.getString("error_msg")).toJson());
            return;
        }
        boolean success = Dao.addUser(name, passwd, base64);
        if (!success) {
            resp.getWriter().write(new RespBean(ErrorCode.DB_ERROR).toJson());
        } else {
            resp.getWriter().write(new RespBean(ErrorCode.SUCCESS).toJson());
            HttpSession session = req.getSession(true);
            session.setAttribute("name", items.get(0));
        }
    }

    private static class FaceDetect {

        public static String faceDetect(String base64) {
            String url = "https://aip.baidubce.com/rest/2.0/face/v3/detect";
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("image", base64);
                map.put("image_type", "BASE64");
                map.put("face_type", "LIVE");
                String param = GsonUtils.toJson(map);
                String accessToken = AuthService.getAuth();
                String result = HttpUtil.post(url, accessToken, "application/json", param);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
