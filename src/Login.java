import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class Login extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean success = Dao.exists("select name from user where name='" + req.getParameter("name") + "' and passwd='" + req.getParameter("passwd") + "';");
        if (!success) {
            resp.getWriter().write(new RespBean(ErrorCode.USERNAME_PASSWD_NOT_MATCH).toJson());
        } else {
            resp.getWriter().write(new RespBean(ErrorCode.SUCCESS).toJson());
            HttpSession session = req.getSession(true);
            session.setAttribute("name", req.getParameter("name"));
        }
    }
}
