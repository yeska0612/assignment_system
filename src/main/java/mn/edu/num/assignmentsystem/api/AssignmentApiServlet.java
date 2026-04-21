package mn.edu.num.assignmentsystem.api;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
 * AssignmentApiServlet нь RESTful JSON API-ийн primary adapter юм.
 *
 * Энэ servlet нь HTML view ашиглахгүй.
 * Харин HTTP request авч, JSON response буцаана.
 *
 * Дэмжих endpoint-ууд:
 * - GET  /api/assignments       -> бүх assignment жагсаалт
 * - GET  /api/assignments/{id}  -> нэг assignment
 * - POST /api/assignments       -> шинэ assignment үүсгэх
 *
 * Энэ нь mobile app, Postman, бусад client-ууд Core layer-тай
 * HTML/JSP-гүйгээр харилцах боломж олгоно.
 */
@WebServlet("/api/assignments/*")
public class AssignmentApiServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private AssignmentService assignmentService;

    private ObjectMapper objectMapper;

    /**
     * Servlet анх ачаалагдах үед:
     * - service-ээ factory-аар авна
     * - Jackson ObjectMapper-оо үүсгэнэ
     */
    @Override
    public void init() throws ServletException {
        IAssignmentRepository repository = RepositoryFactory.createRepository();
        this.assignmentService = new AssignmentService(repository);

        this.objectMapper = new ObjectMapper();

        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        try {
            /*
             * Хэрэв pathInfo хоосон бол:
             * GET /api/assignments
             *
             * Бүх assignment-уудыг JSON array болгож буцаана.
             */
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Assignment> assignments = assignmentService.getAllAssignments();
                String jsonResponse = objectMapper.writeValueAsString(assignments);

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(jsonResponse);
                return;
            }

            /*
             * Хэрэв pathInfo дотор /{id} хэлбэрээр ирсэн бол:
             * GET /api/assignments/5
             *
             * Leading slash-ийг авч id болгон parse хийнэ.
             */
            String idText = pathInfo.substring(1);
            Long id = Long.parseLong(idText);

            Assignment assignment = assignmentService.getAssignmentById(id);

            if (assignment != null) {
                String jsonResponse = objectMapper.writeValueAsString(assignment);

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(jsonResponse);
            } else {
                /*
                 * ID байхгүй бол 404 буцаана.
                 */
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }

        } catch (NumberFormatException e) {
            /*
             * Path дотор байгаа ID тоо биш байвал client-ийн хүсэлт буруу байна.
             */
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            /*
             * Service layer "assignment олдсонгүй" гэх мэт validation/error өгч болно.
             */
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            /*
             * Бусад бүх unexpected error-д 500 буцаана.
             */
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * POST /api/assignments
     *
     * Client-ээс ирсэн JSON body-г Assignment object болгон хувиргаж,
     * service-ээр дамжуулан хадгална.
     *
     * Жишээ JSON:
     * {
     *   "title": "Lab 11 API",
     *   "studentId": "22B1NUM001",
     *   "courseCode": "ICSI486",
     *   "description": "REST API test"
     * }
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            /*
             * Request body доторх JSON-ийг Jackson ашиглан Assignment object болгоно.
             *
             * Энэ алхамд JSON field name-ууд нь Assignment class-ийн
             * property нэрүүдтэй таарч байх ёстой.
             */
            Assignment newAssignment =
                    objectMapper.readValue(request.getInputStream(), Assignment.class);

            /*
             * Core/service layer дээр create logic болон validation ажиллана.
             */
            assignmentService.createAssignment(newAssignment);

            /*
             * REST API дээр шинэ объект амжилттай үүссэн бол
             * 201 Created status code буцаана.
             */
            response.setStatus(HttpServletResponse.SC_CREATED);

            /*
             * Шинээр үүссэн object-ийг буцааж JSON-р явуулж болно.
             * Ингэснээр client generated id зэрэг шинэ утгуудыг шууд харж чадна.
             */
            response.getWriter().write(objectMapper.writeValueAsString(newAssignment));

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            /*
             * JSON format буруу бол 400 Bad Request буцаана.
             * Энэ нь Lab 11-ийн чухал error-handling requirement.
             */
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            /*
             * Service validation failed бол мөн client-ийн хүсэлт буруу гэж үзэж 400 буцаана.
             */
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            /*
             * Бусад unexpected error-д 500 буцаана.
             */
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}