package mn.edu.num.assignmentsystem.web;

import java.io.IOException;

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
 * AssignmentServlet нь assignment create үйлдлийг хариуцдаг web controller юм.
 *
 * Энэ servlet-ийн үндсэн зорилго нь:
 * - browser form-оос ирсэн POST хүсэлтийг авч шинэ assignment үүсгэх
 * - create үйлдлийг зөвхөн login хийсэн хэрэглэгчид зөвшөөрөх
 * - create амжилттай болсны дараа PRG pattern ашиглан dashboard руу буцаах
 *
 * Assignment жагсаалтыг одоо DashboardServlet хариуцаж байгаа.
 */
@WebServlet("/assignments")
public class AssignmentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** Business logic service */
    private AssignmentService assignmentService;

    /**
     * Servlet анх ачаалагдах үед service-ийг factory-аар үүсгэнэ.
     *
     * Ингэснээр web layer нь repository implementation-ийг шууд new хийхгүй,
     * харин existing factory-гаар дамжуулж service-ээ авна.
     */
    @Override
    public void init() throws ServletException {
        IAssignmentRepository repository = RepositoryFactory.createRepository();
        this.assignmentService = new AssignmentService(repository);
    }

    /**
     * GET /assignments
     *
     * Assignment жагсаалтыг DashboardServlet хариуцаж байгаа тул
     * энэ route-оор dashboard руу redirect хийнэ.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/views/assignments.jsp")
               .forward(request, response);
    }

    /**
     * POST /assignments
     *
     * Browser form-оос ирсэн өгөгдлөөр шинэ assignment үүсгэнэ.
     * Энэ үйлдэл нь data mutation тул зөвхөн authenticated хэрэглэгчид нээлттэй.
     *
     * Амжилттай хадгалсны дараа PRG pattern ашиглан dashboard руу redirect хийнэ.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * Create хийх үйлдэл нь зөвхөн login хийсэн хэрэглэгчид зөвшөөрөгдөнө.
         * Session дээр loggedInUser байхгүй бол authentication хийгээгүй гэж үзээд
         * login page руу буцаана.
         */
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        /*
         * HTML form-оос ирсэн parameter-уудыг уншина.
         * Эндхүү key-үүд нь dashboard.jsp дээрх input name-уудтай
         * яг ижил байх ёстой.
         */
        String title = request.getParameter("title");
        String studentId = request.getParameter("studentId");
        String courseCode = request.getParameter("courseCode");
        String description = request.getParameter("description");

        try {
            /*
             * Web request-ээс ирсэн өгөгдлөөр domain object үүсгэнэ.
             * Бизнес дүрэм болон validation-ийг service layer шалгана.
             */
            Assignment assignment = new Assignment(title, studentId, courseCode, description);
            assignmentService.createAssignment(assignment);

            /*
             * PRG (Post-Redirect-Get) pattern:
             * POST амжилттай болсны дараа JSP рүү forward хийхгүй.
             * Харин browser-оор шинэ GET request хийлгэхийн тулд redirect хийнэ.
             *
             * Ингэснээр хэрэглэгч F5 дарахад form дахин submit болж
             * duplicate өгөгдөл үүсэхээс хамгаална.
             */
            response.sendRedirect(request.getContextPath() + "/dashboard");

        } catch (IllegalArgumentException e) {
            /*
             * Core/service дээрээс validation алдаа ирсэн бол
             * хэрэглэгчийн илгээсэн өгөгдөл буруу байна гэсэн үг.
             */
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );
        } catch (Exception e) {
            /*
             * Бусад бүх алдааг системийн алдаа гэж үзэж 500 буцаана.
             */
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Assignment хадгалах үед системийн алдаа гарлаа."
            );
        }
    }
}