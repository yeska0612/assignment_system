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
 * TeacherGradeServlet нь teacher хэрэглэгч submitted assignment-ийг
 * үнэлэх POST request-ийг хариуцна.
 *
 * Энэ servlet нь зөвхөн TEACHER role-той хэрэглэгчид нээлттэй.
 * Teacher score болон feedback өгөөд assignment-ийг GRADED төлөвт оруулна.
 */
@WebServlet("/grade-assignment")
public class TeacherGradeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** Business logic service */
    private AssignmentService assignmentService;

    @Override
    public void init() throws ServletException {
        IAssignmentRepository repository = RepositoryFactory.createRepository();
        this.assignmentService = new AssignmentService(repository);
    }

    /**
     * POST /grade-assignment
     *
     * Teacher submitted assignment дээр:
     * - score оруулна
     * - feedback бичнэ
     * - service layer-ээр дамжуулж grade хийнэ
     *
     * Дараа нь dashboard руу redirect хийнэ.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        /*
         * Grade хийх үйлдэл нь зөвхөн teacher role-той хэрэглэгчид зөвшөөрөгдөнө.
         * Хэрэв session байхгүй эсвэл role буруу бол login руу буцаана.
         */
        if (session == null
                || session.getAttribute("loggedInUser") == null
                || !UserRole.TEACHER.name().equals(session.getAttribute("role"))) {

            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String assignmentIdText = request.getParameter("assignmentId");
        String scoreText = request.getParameter("score");
        String feedback = request.getParameter("feedback");

        try {
            Long assignmentId = Long.parseLong(assignmentIdText);
            Double score = Double.parseDouble(scoreText);

            /*
             * Feedback хоосон ирсэн бол null болгож болно.
             * Гэхдээ service layer business rule-ээ өөрөө шалгана.
             */
            if (feedback != null) {
                feedback = feedback.trim();
                if (feedback.isEmpty()) {
                    feedback = null;
                }
            }

            assignmentService.gradeAssignment(assignmentId, score, feedback);

            /*
             * Grade хийсний дараа PRG pattern баримталж dashboard руу буцна.
             * Ингэснээр browser refresh хийхэд form дахин submit болохгүй.
             */
            response.sendRedirect(request.getContextPath() + "/dashboard");

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Assignment ID эсвэл score буруу форматтай байна."
            );
        } catch (IllegalArgumentException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );
        } catch (IllegalStateException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );
        } catch (Exception e) {
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Assignment grade хийх үед системийн алдаа гарлаа."
            );
        }
    }
}