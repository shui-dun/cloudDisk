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

@WebServlet("/signup")
public class SignUp extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        java.util.List<FileItem> items = Upload.readForm(req);
        if (items == null) {
            resp.getWriter().write(RespCode.resp(RespCode.UNRESOLVED_REQUEST));
            return;
        }
        if (items.size() != 3) {
            resp.getWriter().write(RespCode.resp(RespCode.N_PARAMETER_ERROR));
            return;
        }
        String name = items.get(0).getString();
        String passwd = items.get(1).getString();
        String base64 = items.get(2).getString();
        if (name.length() <= 1 || passwd.length() <= 3) {
            resp.getWriter().write(RespCode.resp(RespCode.WEAK_PASSWD));
            return;
        }
        if (Dao.exists("select name from user where name=\"" + name + "\";")) {
            resp.getWriter().write(RespCode.resp(RespCode.USER_NAME_ERROR));
            return;
        }
        String ans = FaceDetect.faceDetect(Upload.base64RmHead(base64));
        if (ans == null) {
            resp.getWriter().write(RespCode.resp(RespCode.BAIDU_ERROR));
            return;
        }
        JSONObject json = new JSONObject(ans);
        if (json.getInt("error_code") != 0) {
            String s = String.format("{\"error_code\":%d,\"error_msg\":\"%s\"}", RespCode.BAIDU_ERROR_RET, json.getString("error_msg"));
            resp.getWriter().write(s);
            return;
        }
        boolean success = Dao.update("insert into user values('" + name + "','" + passwd + "','" + base64 + "');");
        if (!success) {
            resp.getWriter().write(RespCode.resp(RespCode.IO_EXCEPTION));
        } else {
            resp.getWriter().write(RespCode.resp(RespCode.SUCCESS));
            HttpSession session = req.getSession(true);
            session.setAttribute("name", items.get(0));
        }
    }

    private static class FaceDetect {

        public static String faceDetect(String base64) {
            // 请求url
            String url = "https://aip.baidubce.com/rest/2.0/face/v3/detect";
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("image", base64);
                map.put("image_type", "BASE64");
                map.put("face_type", "LIVE");
                String param = GsonUtils.toJson(map);
                System.out.println(param);
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
