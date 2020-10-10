import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@WebServlet("/signup")
public class SignUp extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
        java.util.List<FileItem> items = Upload.readForm(req, resp, 3);
        if (items == null) {
            return;
        }
        boolean success = Dao.update("insert into user values('" + items.get(0).getString() + "','" + items.get(1).getString() + "','" + items.get(2).getString() + "');");
        if (!success) {
            resp.getWriter().write("{\"status\":\"wrong\"}");
        } else {
            resp.getWriter().write("{\"status\":\"success\"}");
            HttpSession session = req.getSession(true);
            session.setAttribute("name", items.get(0));
        }
    }
}
