package mn.edu.num.assignmentsystem.web;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import mn.edu.num.assignmentsystem.core.application.AssignmentService;
import mn.edu.num.assignmentsystem.core.ports.IAssignmentRepository;
import mn.edu.num.assignmentsystem.infrastructure.persistence.RepositoryFactory;

/**
 * AssignmentDeleteServlet нь assignment устгах POST хүсэлтийг хариуцна.
 *
 * Delete үйлдлийг GET линкээр хийхгүй.
 * Харин hidden input бүхий form-оор POST хүсэлт авч ажиллуулна.
 *
 * Мөн энэ үйлдэл нь data mutation тул зөвхөн login хийсэн
 * хэрэглэгчид зөвшөөрөгдөнө.
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
     * тухайн assignment-ийг устгаад dashboard руу redirect хийнэ.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * Delete хийх нь data mutation үйлдэл тул
         * зөвхөн authenticated хэрэглэгчид зөвшөөрнө.
         */
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String assignmentIdText = request.getParameter("assignmentId");

        try {
            Long assignmentId = Long.parseLong(assignmentIdText);
            assignmentService.deleteAssignment(assignmentId);

            /*
             * Delete хийсний дараа dashboard руу redirect хийнэ.
             * Ингэснээр browser дахин цэвэр GET request хийж page-ээ ачаална.
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
                    "Assignment устгах үед системийн алдаа гарлаа."
            );
        }
    }
}