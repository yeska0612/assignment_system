package mn.edu.num.assignmentsystem.web;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * LoginServlet нь authentication flow-ийн controller юм.
 *
 * Энэ sprint дээр authentication-ийг энгийн hardcoded байдлаар хийж байна:
 * username = admin
 * password = password123
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * GET /login
     *
     * Хэрэглэгч login page нээхэд form-ыг JSP view рүү forward хийнэ.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/views/login.jsp")
               .forward(request, response);
    }

    /**
     * POST /login
     *
     * Browser-оос ирсэн username, password-ийг шалгана.
     * Хэрэв зөв бол session дээр loggedInUser хадгалж dashboard руу redirect хийнэ.
     * Хэрэв буруу бол login page руу error=true query parameter-тай буцаана.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String user = request.getParameter("username");
        String pass = request.getParameter("password");

        /*
         * Энэ sprint дээр hardcoded check ашиглаж байгаа.
         * Core-ийг өөрчлөхгүйгээр web authentication flow-оо эхлээд туршиж байна.
         */
        if ("admin".equals(user) && "password123".equals(pass)) {

            /*
             * request.getSession() дуудахад:
             * - session байхгүй бол шинээр үүсгэнэ
             * - байвал одоо байгаа session-ийг өгнө
            */
            HttpSession session = request.getSession();
            session.setAttribute("loggedInUser", user);

            /*
             * Амжилттай login хийсний дараа dashboard руу redirect хийнэ.
             */
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            /*
             * Буруу credential үед session үүсгэхгүй.
             * Зөвхөн login page руу error flag-тай буцаана.
             */
            response.sendRedirect(request.getContextPath() + "/login?error=true");
        }
    }
}