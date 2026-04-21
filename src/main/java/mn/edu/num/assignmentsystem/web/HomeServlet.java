package mn.edu.num.assignmentsystem.web;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * HomeServlet нь application-ийн root URL-ийг хариуцна.
 *
 * Хэрэглэгч root path руу орж ирэхэд:
 * - login хийсэн бол dashboard руу
 * - хийгээгүй бол login page руу
 * redirect хийнэ.
 */
@WebServlet("/")
public class HomeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * GET /
     *
     * Session-ийн төлөвөөс хамаарч зөв page руу redirect хийнэ.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("loggedInUser") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
}