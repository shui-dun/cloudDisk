import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/list")
public class List extends HttpServlet {

    public static final String path = "D:\\file\\code\\PROJECTS\\cloudDisk\\folder\\";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
        File folder = new File(path);
        File[] files = folder.listFiles();
        if (files == null) {
            throw new RuntimeException("读取目录时出错");
        } else {
            if (files.length == 0) {
                resp.getWriter().write("[]");
                return;
            }
            SimpleDateFormat ft = new SimpleDateFormat("yy-MM-dd hh:mm");
            StringBuilder ans = new StringBuilder("[");
            for (File file : files) {
                if (file.isFile()) {
                    ans.append("{\"name\":\"").append(file.getName()).append("\",");
                    ans.append("\"size\":\"").append(file.length()).append("\",");
                    ans.append("\"time\":\"").append(ft.format(new Date(file.lastModified()))).append("\"},");
                }
            }
            ans.setCharAt(ans.length() - 1, ']');
            resp.getWriter().write(ans.toString());
        }
    }
}
