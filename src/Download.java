import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@WebServlet("/download")
public class Download extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
        String name = req.getParameter("name");
        resp.addHeader("Content-Disposition", "attachment; filename=" + name);
        File file = new File(List.path + name);
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
