package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {

    private TemplateEngine templateEngine;

    @Override
    public void init() {
        ServletContextTemplateResolver resolver =
                new ServletContextTemplateResolver(getServletContext());

        resolver.setPrefix("/WEB-INF/templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String timezone = req.getParameter("timezone");

        if (timezone == null && req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                if ("lastTimezone".equals(cookie.getName())) {
                    timezone = cookie.getValue();
                }
            }
        }

        if (timezone == null) {
            timezone = "UTC";
        }

        Cookie cookie = new Cookie("lastTimezone", timezone);
        cookie.setMaxAge(60 * 60);
        resp.addCookie(cookie);

        ZoneOffset offset = timezone.equals("UTC")
                ? ZoneOffset.UTC
                : ZoneOffset.of(timezone.replace("UTC", ""));

        LocalDateTime now = LocalDateTime.now(offset);

        String time = now.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );

        WebContext context = new WebContext(req, resp, getServletContext());
        context.setVariable("time", time);
        context.setVariable("timezone", timezone);

        resp.setContentType("text/html;charset=UTF-8");

        templateEngine.process("time", context, resp.getWriter());
    }
}