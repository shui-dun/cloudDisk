import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

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
                ((HttpServletResponse) servletResponse).setStatus(403);
                servletResponse.getWriter().write(new RespBean(ErrorCode.NOT_LOGIN).toJson());
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
    }
}
