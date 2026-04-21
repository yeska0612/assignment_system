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
 * Servlet = Controller layer (Web adapter)
 *
 * Browser → HTTP request → Servlet → Service → DB → буцааж JSP рүү дамжуулна
 */
@WebServlet("/assignments")
public class AssignmentServlet extends HttpServlet {

    private AssignmentService assignmentService;

    /**
     * Servlet анх ачаалагдахад 1 удаа дуудагдана.
     */
    @Override
    public void init() throws ServletException {

        /*
         * Factory ашиглаж repository авна.
         * Core layer-д огт өөрчлөлт хийхгүй.
         */
        IAssignmentRepository repository = RepositoryFactory.createRepository();

        /*
         * Service үүсгэж controller дээр хадгална.
         */
        this.assignmentService = new AssignmentService(repository);
    }

    /**
     * GET /assignments
     *
     * Browser энэ URL рүү ороход:
     * - DB-с бүх assignment авна
     * - request дээр хадгална
     * - JSP рүү forward хийнэ
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * Service-ээс бүх өгөгдөл авна
         */
        List<Assignment> assignments = assignmentService.getAllAssignments();

        /*
         * Request attribute (backpack) дотор хадгална
         */
        request.setAttribute("assignmentList", assignments);

        /*
         * JSP рүү дамжуулна
         */
        request.getRequestDispatcher("/WEB-INF/views/assignments.jsp")
               .forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String title = request.getParameter("title");
        String studentId = request.getParameter("studentId");
        String courseCode = request.getParameter("courseCode");
        String description = request.getParameter("description");

        try {
            Assignment assignment =
                new Assignment(title, studentId, courseCode, description);

            assignmentService.createAssignment(assignment);

            /*
             * PRG Pattern
             * → refresh хийхэд duplicate үүсэхгүй
             */
            response.sendRedirect("assignments");

        } catch (IllegalArgumentException e) {

            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                e.getMessage()
            );

        } catch (Exception e) {

            response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "System error"
            );
        }
    }
}