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
import mn.edu.num.assignmentsystem.core.ports.IAssignmentRepository;
import mn.edu.num.assignmentsystem.infrastructure.persistence.RepositoryFactory;

/**
 * DashboardServlet нь login хийсэн хэрэглэгчийн хамгаалагдсан dashboard-ийг хариуцна.
 *
 * Энэ servlet нь хоёр чухал үүрэгтэй:
 * 1. Session дээр хэрэглэгч login хийсэн эсэхийг шалгана
 * 2. Login хийсэн бол assignment жагсаалтыг ачаалж JSP view рүү дамжуулна
 *
 * "Bouncer pattern"
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

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
     * GET /dashboard
     *
     * Хамгаалагдсан route.
     * Session дээр loggedInUser attribute байхгүй бол login page руу шууд буцаана.
     * Байвал dashboard view-г ачаална.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * Session-ийг авна.
         * getSession(false) ашиглаж болох ч starter-д getSession() санааг үзүүлсэн.
         * Энд аль аль нь боломжтой. Бид илүү хамгаалалттай байдлаар false ашиглаж болно.
         */
        HttpSession session = request.getSession(false);

        /*
         * --- THE BOUNCER ---
         * Хэрэв session байхгүй эсвэл loggedInUser байхгүй бол
         * хэрэглэгч authentication хийгээгүй гэсэн үг.
         * Тиймээс protected page үзүүлэхгүй, login page руу буцаана.
         */
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return; // Маш чухал: эндээс цааш logic ажиллах ёсгүй
        }

        /*
         * Хэрэв энэ мөрөнд хүрсэн бол хэрэглэгч login хийсэн байна.
         * Session дээр хадгалсан username-ийг авч welcome message бэлдэнэ.
         */
        String currentUser = (String) session.getAttribute("loggedInUser");
        request.setAttribute("welcomeMessage", "Welcome back, " + currentUser);

        /*
         * Dashboard дээр харуулах assignment өгөгдлийг service-ээс авна.
         */
        List<Assignment> assignments = assignmentService.getAllAssignments();
        request.setAttribute("assignmentList", assignments);

        /*
         * Хамгаалагдсан JSP view рүү forward хийнэ.
         */
        request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp")
               .forward(request, response);
    }
}