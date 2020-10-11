import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/upload")
public class Upload extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
        java.util.List<FileItem> items = readForm(req);
        if (items == null) {
            resp.getWriter().write(RespCode.resp(RespCode.UNRESOLVED_REQUEST));
            return;
        }
        if (items.size() != 2) {
            resp.getWriter().write(RespCode.resp(RespCode.N_PARAMETER_ERROR));
            return;
        }
        String base64 = items.get(1).getString();
        try {
            Files.write(Paths.get(List.path + items.get(0).getString()),
                    Base64.getDecoder().decode(base64RmHead(base64)));
            resp.getWriter().write(RespCode.resp(RespCode.SUCCESS));
        } catch (IOException e) {
            resp.getWriter().write(RespCode.resp(RespCode.IO_EXCEPTION));
        }
    }

    public static java.util.List<FileItem> readForm(HttpServletRequest req) {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("UTF-8");
        java.util.List<FileItem> items = null;
        try {
            items = upload.parseRequest(req);
        } catch (FileUploadException e) {
            return null;
        }
        return items;
    }

    public static String base64RmHead(String base64) {
        return base64.substring(base64.indexOf(",") + 1);
    }
}
