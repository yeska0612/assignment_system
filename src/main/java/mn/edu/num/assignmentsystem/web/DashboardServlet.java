package mn.edu.num.assignmentsystem.web;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import mn.edu.num.assignmentsystem.core.application.AssignmentService;
import mn.edu.num.assignmentsystem.core.domain.Assignment;
import mn.edu.num.assignmentsystem.core.domain.UserRole;
import mn.edu.num.assignmentsystem.core.ports.IAssignmentRepository;
import mn.edu.num.assignmentsystem.infrastructure.persistence.RepositoryFactory;

/**
 * DashboardServlet нь login хийсэн хэрэглэгчийн хамгаалагдсан dashboard-ийг хариуцна.
 *
 * Энэ өргөтгөсөн хувилбар дээр dashboard нь role-aware ажиллана:
 * - TEACHER -> бүх assignment харна
 * - STUDENT -> зөвхөн өөрийн assignment-уудыг харна
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private AssignmentService assignmentService;

    @Override
    public void init() throws ServletException {
        try {
            IAssignmentRepository repository =
                    RepositoryFactory.createRepository();

            this.assignmentService =
                    new AssignmentService(repository);

        } catch (Exception e) {
            e.printStackTrace();

            throw new ServletException(
                    "DashboardServlet init failed", e);
        }
    }
    /**
     * GET /dashboard
     *
     * Session байхгүй бол login руу буцаана.
     * Session байвал role-аас хамааран тохирох өгөгдлийг dashboard view рүү дамжуулна.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        /*
         * --- THE BOUNCER ---
         * Login хийгээгүй хэрэглэгч protected dashboard үзэх эрхгүй.
         */
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String currentUser = (String) session.getAttribute("loggedInUser");
        String role = (String) session.getAttribute("role");

        request.setAttribute("welcomeMessage", "Welcome back, " + currentUser);
        request.setAttribute("role", role);

        try {
            List<Assignment> assignments;

            /*
             * Teacher dashboard:
             * Бүх assignment-уудыг харуулна.
             */
            if (UserRole.TEACHER.name().equals(role)) {
                assignments = assignmentService.getAllAssignments();
                request.setAttribute("dashboardTitle", "Teacher Dashboard");
            }
            /*
             * Student dashboard:
             * Зөвхөн тухайн studentId-д хамаарах assignment-уудыг харуулна.
             */
            else if (UserRole.STUDENT.name().equals(role)) {
                String studentId = (String) session.getAttribute("studentId");
                assignments = assignmentService.getAssignmentsByStudentId(studentId);
                request.setAttribute("dashboardTitle", "Student Dashboard");
                request.setAttribute("studentId", studentId);
            }
            /*
             * Role байхгүй эсвэл танигдахгүй бол login руу буцаана.
             */
            else {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            request.setAttribute("assignmentList", assignments);

            request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            /*
             * Docker container дотор runtime exception гарахад
             * яг ямар repository/schema/SQL дээр унасныг docker logs дээр харахын тулд
             * түр хугацаанд stacktrace хэвлэж байна.
             */
            e.printStackTrace();

            request.setAttribute("errorMessage", e.getMessage());

            request.getRequestDispatcher("/error.jsp")
                   .forward(request, response);
        }
    }
}