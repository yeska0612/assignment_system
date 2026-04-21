package mn.edu.num.assignmentsystem.web;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mn.edu.num.assignmentsystem.core.application.AssignmentService;
import mn.edu.num.assignmentsystem.core.domain.Assignment;
import mn.edu.num.assignmentsystem.core.ports.IAssignmentRepository;
import mn.edu.num.assignmentsystem.infrastructure.persistence.RepositoryFactory;

/**
 * AssignmentServlet нь web adapter-ийн controller үүрэгтэй.
 *
 * Энэ servlet:
 * - GET хүсэлтээр бүх assignment-ийг ачаалж JSP рүү дамжуулна
 * - POST хүсэлтээр browser form-оос шинэ assignment үүсгэнэ
 *
 * Core layer нь web-ийн талаар юу ч мэдэхгүй.
 * Servlet зөвхөн request-ийг service рүү хөрвүүлж өгч байна.
 */
@WebServlet("/assignments")
public class AssignmentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** Business logic service */
    private AssignmentService assignmentService;

    /**
     * Servlet анх ачаалагдах үед service-ийг factory-аар үүсгэнэ.
     */
    @Override
    public void init() throws ServletException {
        IAssignmentRepository repository = RepositoryFactory.createRepository();
        this.assignmentService = new AssignmentService(repository);
    }

    /**
     * GET /assignments
     *
     * Browser-оос assignments page нээгдэхэд:
     * - DB-с бүх assignment авна
     * - request attribute-д хадгална
     * - JSP view рүү forward хийнэ
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Assignment> assignments = assignmentService.getAllAssignments();
        request.setAttribute("assignmentList", assignments);

        request.getRequestDispatcher("/WEB-INF/views/assignments.jsp")
               .forward(request, response);
    }

    /**
     * POST /assignments
     *
     * Browser form-оос ирсэн өгөгдлөөр шинэ assignment үүсгэнэ.
     * Амжилттай хадгалсны дараа PRG pattern ашиглан redirect хийнэ.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String title = request.getParameter("title");
        String studentId = request.getParameter("studentId");
        String courseCode = request.getParameter("courseCode");
        String description = request.getParameter("description");

        try {
            Assignment assignment = new Assignment(title, studentId, courseCode, description);
            assignmentService.createAssignment(assignment);

            /*
             * PRG (Post-Redirect-Get) pattern:
             * POST амжилттай болсны дараа шууд JSP рүү forward хийхгүй.
             * Харин browser-т шинэ GET request хийлгэж redirect хийнэ.
             *
             * Ингэснээр хэрэглэгч F5 дарахад form дахин submit болохгүй,
             * duplicate insert-ээс хамгаална.
             */
            response.sendRedirect(request.getContextPath() + "/assignments");

        } catch (IllegalArgumentException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );
        } catch (Exception e) {
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Assignment хадгалах үед системийн алдаа гарлаа."
            );
        }
    }
}