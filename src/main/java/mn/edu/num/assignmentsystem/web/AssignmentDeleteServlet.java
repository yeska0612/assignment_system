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

@WebServlet("/delete-assignment")
public class AssignmentDeleteServlet extends HttpServlet {

    private AssignmentService assignmentService;

    @Override
    public void init() throws ServletException {
        IAssignmentRepository repo = RepositoryFactory.createRepository();
        this.assignmentService = new AssignmentService(repo);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("assignmentId");

        try {
            Long id = Long.parseLong(idStr);

            assignmentService.deleteAssignment(id);

            /*
             * PRG pattern
             */
            response.sendRedirect("assignments");

        } catch (Exception e) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Delete failed"
            );
        }
    }
}