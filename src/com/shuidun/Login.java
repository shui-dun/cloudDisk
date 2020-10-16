package com.shuidun;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 进行登录验证
 * <p>
 * 请求
 * name:用户名
 * passwd:密码明文
 * <p>
 * 响应：
 * code:状态码
 * msg:状态信息
 */
@WebServlet("/login")
public class Login extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean success = Dao.testPasswd(req.getParameter("name"), req.getParameter("passwd"));
        if (!success) {
            resp.getWriter().write(new RespBean(ErrorCode.USERNAME_PASSWD_NOT_MATCH).toJson());
        } else {
            resp.getWriter().write(new RespBean(ErrorCode.SUCCESS).toJson());
            HttpSession session = req.getSession(true);
            session.setAttribute("name", req.getParameter("name"));
        }
    }
}
