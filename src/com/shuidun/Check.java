package com.shuidun;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 过滤器，检测是否登录，未登录则拦截，否则通过
 */
@WebFilter("/*")
public class Check implements Filter {
    private static ArrayList<String> exclude = new ArrayList<>();

    static {
        Collections.addAll(exclude, "/login", "/signup", "/facelogin");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletResponse.setCharacterEncoding("utf-8");
        if (exclude.contains(((HttpServletRequest) servletRequest).getServletPath())) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            HttpSession session = ((HttpServletRequest) servletRequest).getSession(true);
            if (session.getAttribute("name") == null) {
                servletResponse.getWriter().write(new RespBean(ErrorCode.NOT_LOGIN).toJson());
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
    }
}
