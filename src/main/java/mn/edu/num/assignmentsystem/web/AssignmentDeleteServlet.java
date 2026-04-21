package mn.edu.num.assignmentsystem.web;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mn.edu.num.assignmentsystem.core.application.AssignmentService;
import mn.edu.num.assignmentsystem.core.ports.IAssignmentRepository;
import mn.edu.num.assignmentsystem.infrastructure.persistence.RepositoryFactory;

/**
 * AssignmentDeleteServlet нь assignment устгах POST хүсэлтийг хариуцна.
 *
 * Delete үйлдлийг GET линкээр хийхгүй.
 * Харин hidden input бүхий form-оор POST хүсэлт авч ажиллуулна.
 * Энэ нь Lab 09-ийн security requirement-тэй нийцнэ.
 */
@WebServlet("/delete-assignment")
public class AssignmentDeleteServlet extends HttpServlet {

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
     * POST /delete-assignment
     *
     * Hidden input-аар дамжиж ирсэн assignmentId-г авч,
     * тухайн assignment-ийг устгаад дахин assignments page рүү redirect хийнэ.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String assignmentIdText = request.getParameter("assignmentId");

        try {
            Long assignmentId = Long.parseLong(assignmentIdText);
            assignmentService.deleteAssignment(assignmentId);

            /*
             * Delete хийсний дараа мөн PRG зарчим баримталж
             * list page рүү redirect хийнэ.
             */
            response.sendRedirect(request.getContextPath() + "/assignments");

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Assignment ID буруу форматтай байна."
            );
        } catch (Exception e) {
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Assignment устгах үед системийн алдаа гарлаа."
            );
        }
    }
}