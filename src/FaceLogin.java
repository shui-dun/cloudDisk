import com.baidu.ai.aip.utils.GsonUtils;
import com.baidu.ai.aip.utils.HttpUtil;
import com.google.gson.Gson;
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
        java.util.List<FileItem> items = Upload.readForm(req);
        if (items == null) {
            resp.getWriter().write(new RespBean(ErrorCode.UNRESOLVED_REQUEST).toJson());
            return;
        }
        if (items.size() != 2) {
            resp.getWriter().write(new RespBean(ErrorCode.N_PARAMETER_ERROR).toJson());
            return;
        }
        List<ImgBean> list = new ArrayList<>();
        list.add(new ImgBean(Upload.base64RmHead(items.get(1).getString()), "BASE64", "LIVE", "NONE", "NONE"));
        String origin = Dao.query("select img from user where name='" + items.get(0).getString() + "';");
        if (origin == null) {
            resp.getWriter().write(new RespBean(ErrorCode.USER_NAME_NOT_EXIST).toJson());
            return;
        }
        String base64 = Upload.base64RmHead(origin);
        list.add(new ImgBean(base64, "BASE64", "LIVE", "NONE", "NONE"));
        String ans = FaceMatch.faceMatch(list);
        if (ans == null) {
            resp.getWriter().write(new RespBean(ErrorCode.BAIDU_ERROR).toJson());
            return;
        }
        JSONObject json = new JSONObject(ans);
        if (json.getInt("error_code") != 0) {
            resp.getWriter().write(new RespBean(ErrorCode.BAIDU_ERROR_RET.getCode(), ErrorCode.BAIDU_ERROR_RET.getMsg() + "：" + json.getString("error_msg")).toJson());
            return;
        }
        if (!isMatch(ans)) {
            resp.getWriter().write(new RespBean(ErrorCode.FACE_NOT_MATCH).toJson());
        } else {
            resp.getWriter().write(new RespBean(ErrorCode.SUCCESS).toJson());
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
            String url = "https://aip.baidubce.com/rest/2.0/face/v3/match";
            try {
                String param = GsonUtils.toJson(map);
                String accessToken = AuthService.getAuth();
                return HttpUtil.post(url, accessToken, "application/json", param);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
