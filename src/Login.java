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
            resp.getWriter().write(RespCode.resp(RespCode.NOT_MATCH));
        } else {
            resp.getWriter().write(RespCode.resp(RespCode.SUCCESS));
            HttpSession session = req.getSession(true);
            session.setAttribute("name", req.getParameter("name"));
        }
    }
}
