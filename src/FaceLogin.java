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
        java.util.List<FileItem> items = Upload.readForm(req, resp, 2);
        if (items == null) {
            return;
        }
        List<ImgBean> list = new ArrayList<>();
        list.add(new ImgBean(Upload.base64RmHead(items.get(1).getString()), "BASE64", "LIVE", "NONE", "NONE"));
        String base64 = Upload.base64RmHead(Dao.query("select img from user where name='" + items.get(0).getString() + "';"));
        list.add(new ImgBean(base64, "BASE64", "LIVE", "NONE", "NONE"));
        String ans = FaceMatch.faceMatch(list);
        if (!isMatch(ans)) {
            resp.getWriter().write("{\"status\":\"fail\"}");
        } else {
            resp.getWriter().write("{\"status\":\"success\"}");
            HttpSession session = req.getSession(true);
            session.setAttribute("name", items.get(0).getString());
        }
    }

    private boolean isMatch(String ans) {
        return new JSONObject(ans).getJSONObject("result").getDouble("score") >= 80;
    }
}
