package mn.edu.num.assignmentsystem.web;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import mn.edu.num.assignmentsystem.core.application.AssignmentService;
import mn.edu.num.assignmentsystem.core.domain.UserRole;
import mn.edu.num.assignmentsystem.core.ports.IAssignmentRepository;
import mn.edu.num.assignmentsystem.infrastructure.persistence.RepositoryFactory;

/**
 * StudentSubmitServlet нь student хэрэглэгч өөрийн assignment-ийг submit хийх
 * POST request-ийг хариуцна.
 *
 * Энэ servlet нь зөвхөн STUDENT role-той хэрэглэгчид нээлттэй.
 */
@WebServlet("/submit-assignment")
public class StudentSubmitServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private AssignmentService assignmentService;

    @Override
    public void init() throws ServletException {
        IAssignmentRepository repository = RepositoryFactory.createRepository();
        this.assignmentService = new AssignmentService(repository);
    }

    /**
     * POST /submit-assignment
     *
     * Student role-той хэрэглэгч assignment submit хийж,
     * дараа нь dashboard руу буцна.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        /*
         * Session байхгүй эсвэл role нь STUDENT биш бол login руу буцаана.
         */
        if (session == null
                || session.getAttribute("loggedInUser") == null
                || !UserRole.STUDENT.name().equals(session.getAttribute("role"))) {

            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String assignmentIdText = request.getParameter("assignmentId");

        try {
            Long assignmentId = Long.parseLong(assignmentIdText);
            assignmentService.submitAssignment(assignmentId);

            /*
             * Submit хийсний дараа PRG pattern баримталж dashboard руу буцна.
             */
            response.sendRedirect(request.getContextPath() + "/dashboard");

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Assignment ID буруу форматтай байна."
            );
        } catch (Exception e) {
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Assignment submit хийх үед алдаа гарлаа."
            );
        }
    }
}