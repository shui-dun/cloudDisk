import org.apache.commons.fileupload.FileItem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/signup")
public class SignUp extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
        java.util.List<FileItem> items = Upload.readForm(req);
        if (items == null) {
            resp.getWriter().write(RespCode.resp(RespCode.UNRESOLVED_REQUEST));
            return;
        }
        if (items.size() != 3) {
            resp.getWriter().write(RespCode.resp(RespCode.N_PARAMETER_ERROR));
            return;
        }
        boolean success = Dao.update("insert into user values('" + items.get(0).getString() + "','" + items.get(1).getString() + "','" + items.get(2).getString() + "');");
        if (!success) {
            resp.getWriter().write(RespCode.resp(RespCode.USER_NAME_ERROR));
        } else {
            resp.getWriter().write(RespCode.resp(RespCode.SUCCESS));
            HttpSession session = req.getSession(true);
            session.setAttribute("name", items.get(0));
        }
    }
}
