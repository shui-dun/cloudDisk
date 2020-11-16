package com.shuidun;

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

/**
 * 展示网盘中的文件内容
 * <p>
 * 响应：
 * code:状态码
 * msg:状态信息
 * contents:文件列表信息，详见ListRespBean
 */
@WebServlet("/list")
public class List extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        File folder = new File(Property.getFolderPath());
        File[] files = folder.listFiles();
        if (files == null) {
            resp.getWriter().write(new ListRespBean(ErrorCode.IO_EXCEPTION).toJson());
        } else {
            ListRespBean bean = new ListRespBean(ErrorCode.SUCCESS);
            if (files.length != 0) {
                SimpleDateFormat ft = new SimpleDateFormat("yy-MM-dd hh:mm");
                for (File file : files) {
                    if (file.isFile()) {
                        bean.add(new ListRespBean.ContentBean(file.getName(), file.length(), ft.format(new Date(file.lastModified()))));
                    }
                }
            }
            resp.getWriter().write(bean.toJson());
        }
    }

    /**
     * 文件列表信息的JavaBean
     */
    private static class ListRespBean extends RespBean {
        /**
         * 文件信息列表
         */
        private java.util.List<ContentBean> contents = new ArrayList<>();

        public ListRespBean(Integer code, String msg) {
            super(code, msg);
        }

        public ListRespBean(ErrorCode errorCode) {
            super(errorCode);
        }

        /**
         * 向文件列表中添加一个文件
         */
        void add(ContentBean contentBean) {
            contents.add(contentBean);
        }

        /**
         * 文件的JavaBean
         */
        private static class ContentBean {
            /**
             * 文件名称
             */
            private String name;

            /**
             * 文件大小
             */
            private Long size;

            /**
             * 文件上次修改时间
             */
            private String time;

            public ContentBean(String name, Long size, String time) {
                this.name = name;
                this.size = size;
                this.time = time;
            }
        }

    }
}
