import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@WebServlet("/list")
public class List extends HttpServlet {

    public static final String path = "D:\\file\\code\\PROJECTS\\cloudDisk\\folder\\";
//    public static final String path = "/srv/www/folder/";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
        File folder = new File(path);
        File[] files = folder.listFiles();
        if (files == null) {
            resp.getWriter().write(new Gson().toJson(new RespBean(RespCode.IO_EXCEPTION)));
        } else {
            RespBean bean = new RespBean(RespCode.SUCCESS);
            if (files.length != 0) {
                SimpleDateFormat ft = new SimpleDateFormat("yy-MM-dd hh:mm");
                for (File file : files) {
                    if (file.isFile()) {
                        bean.add(new RespBean.ContentBean(file.getName(), file.length(), ft.format(new Date(file.lastModified()))));
                    }
                }
            }
            resp.getWriter().write(new Gson().toJson(bean));
        }
    }

    private static class RespBean {
        private int error_code;

        private java.util.List<ContentBean> contents = new ArrayList<>();

        private static class ContentBean {
            private String name;
            private Long size;
            private String time;

            public ContentBean(String name, Long size, String time) {
                this.name = name;
                this.size = size;
                this.time = time;
            }
        }

        public RespBean(int error_code) {
            this.error_code = error_code;
        }

        public void add(ContentBean contentBean) {
            contents.add(contentBean);
        }
    }
}
