package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.ZoneOffset;

@WebFilter("/time")
public class TimezoneValidateFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String timezone = req.getParameter("timezone");

        if (timezone != null) {
            try {
                if (!timezone.equals("UTC")) {
                    ZoneOffset.of(timezone.replace("UTC", ""));
                }
            } catch (Exception e) {
                resp.setStatus(400);
                resp.setContentType("text/html;charset=UTF-8");
                resp.getWriter().write("<h1>Invalid timezone</h1>");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}