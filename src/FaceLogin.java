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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/facelogin")
public class FaceLogin extends HttpServlet {


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        java.util.List<FileItem> items = Upload.readForm(req);
        if (items == null) {
            resp.getWriter().write(RespCode.resp(RespCode.UNRESOLVED_REQUEST));
            return;
        }
        if (items.size() != 2) {
            resp.getWriter().write(RespCode.resp(RespCode.N_PARAMETER_ERROR));
            return;
        }
        List<ImgBean> list = new ArrayList<>();
        list.add(new ImgBean(Upload.base64RmHead(items.get(1).getString()), "BASE64", "LIVE", "NONE", "NONE"));
        String origin = Dao.query("select img from user where name='" + items.get(0).getString() + "';");
        if (origin == null) {
            resp.getWriter().write(RespCode.resp(RespCode.USER_NAME_ERROR));
            return;
        }
        String base64 = Upload.base64RmHead(origin);
        list.add(new ImgBean(base64, "BASE64", "LIVE", "NONE", "NONE"));
        String ans = FaceMatch.faceMatch(list);
        System.out.println(ans);
        if (ans == null || new JSONObject(ans).getInt("error_code") != 0) {
            resp.getWriter().write(RespCode.resp(RespCode.BAIDU_ERROR));
            return;
        }
        if (!isMatch(ans)) {
            resp.getWriter().write(RespCode.resp(RespCode.NOT_MATCH));
            return;
        } else {
            resp.getWriter().write(RespCode.resp(RespCode.SUCCESS));
            HttpSession session = req.getSession(true);
            session.setAttribute("name", items.get(0).getString());
        }
    }

    private boolean isMatch(String ans) {
        return new JSONObject(ans).getJSONObject("result").getDouble("score") >= 80;
    }

    private static class ImgBean {
        private String image;
        private String image_type;
        private String face_type;
        private String quality_control;
        private String liveness_control;

        public ImgBean(String image, String image_type, String face_type, String quality_control, String liveness_control) {
            this.image = image;
            this.image_type = image_type;
            this.face_type = face_type;
            this.quality_control = quality_control;
            this.liveness_control = liveness_control;
        }
    }

    private static class FaceMatch {
        public static String faceMatch(Object map) {
            // 请求url
            String url = "https://aip.baidubce.com/rest/2.0/face/v3/match";
            try {
                String param = GsonUtils.toJson(map);
                // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
                String accessToken = AuthService.getAuth();
                return HttpUtil.post(url, accessToken, "application/json", param);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
