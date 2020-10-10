import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class Check implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (((HttpServletRequest) servletRequest).getServletPath().equals("/login")) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            HttpSession session = ((HttpServletRequest) servletRequest).getSession(true);
            if (session.getAttribute("name") == null) {
                ((HttpServletResponse) servletResponse).setStatus(403);
                servletResponse.getWriter().write("not login");
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
    }
}
