package mn.edu.num.assignmentsystem.web;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import mn.edu.num.assignmentsystem.core.domain.UserRole;

/**
 * LoginServlet нь authentication flow-ийн controller юм.
 *
 * Энэ өргөтгөсөн хувилбар дээр бид хоёр role-той demo account ашиглаж байна:
 * - teacher / password123
 * - student / password123
 *
 * Ингэснээр session дээр зөвхөн username биш,
 * role мэдээллийг мөн хадгалж role-based UI болон authorization хийх боломжтой болно.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * GET /login
     *
     * Хэрэглэгч login form харах үед JSP view рүү forward хийнэ.
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
     * Username/password-ийг шалгаж, session дээр:
     * - loggedInUser
     * - role
     * - studentId (хэрэв student бол)
     * мэдээллийг хадгална.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        HttpSession session;

        /*
         * Teacher login:
         * Teacher нь assignment үүсгэх, засах, устгах, үнэлэх эрхтэй.
         */
        if ("teacher".equals(username) && "password12345".equals(password)) {
            session = request.getSession();
            session.setAttribute("loggedInUser", username);
            session.setAttribute("role", UserRole.TEACHER.name());

            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        /*
         * Student login:
         * Student нь зөвхөн өөрийн assignment-уудыг харах ба submit хийх эрхтэй.
         *
         * Demo зорилгоор studentId-г session дээр хадгалж байна.
         * Энэ нь assignment.studentId талбартай таарч filter хийхэд ашиглагдана.
         */
        if ("student".equals(username) && "password1234".equals(password)) {
            session = request.getSession();
            session.setAttribute("loggedInUser", username);
            session.setAttribute("role", UserRole.STUDENT.name());
            session.setAttribute("studentId", "23B1NUM1004");

            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        /*
         * Credential буруу бол login page руу error flag-тай буцаана.
         */
        response.sendRedirect(request.getContextPath() + "/login?error=true");
    }
}