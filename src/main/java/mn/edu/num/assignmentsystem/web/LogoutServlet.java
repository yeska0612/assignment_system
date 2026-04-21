package mn.edu.num.assignmentsystem.web;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * LogoutServlet нь хэрэглэгчийн session-ийг устгаж logout хийдэг.
 *
 * Session invalidate хийснээр сервер талд хадгалагдсан
 * authentication state бүрэн устна.
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * GET /logout
     *
     * Session байвал invalidate хийнэ.
     * Дараа нь login page руу redirect хийнэ.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * false гэдэг нь:
         * - session байхгүй бол шинээр үүсгэхгүй
         * - зөвхөн байгаа session-ийг л авна
         *
         * Logout үед шинэ session үүсгэх ямар ч шаардлагагүй.
         */
        HttpSession session = request.getSession(false);

        if (session != null) {
            /*
             * Session invalidate() хийхэд:
             * - loggedInUser зэрэг бүх session attribute устна
             * - хэрэглэгчийн login state хүчингүй болно
             */
            session.invalidate();
        }

        response.sendRedirect(request.getContextPath() + "/login");
    }
}