package mn.edu.num.assignmentsystem.api;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import mn.edu.num.assignmentsystem.core.application.AssignmentDto;
import mn.edu.num.assignmentsystem.core.application.AssignmentService;
import mn.edu.num.assignmentsystem.core.domain.Assignment;
import mn.edu.num.assignmentsystem.core.ports.IAssignmentRepository;
import mn.edu.num.assignmentsystem.infrastructure.persistence.RepositoryFactory;

/**
 * AssignmentApiServlet нь RESTful JSON API-ийн primary adapter юм.
 *
 * Энэ servlet нь:
 * - GET /api/assignments
 * - GET /api/assignments/{id}
 * - POST /api/assignments
 *
 * endpoint-уудыг хариуцна.
 *
 * Project 1 requirement-ийн дагуу API layer нь domain object-ийг
 * шууд serialize хийхгүй, харин DTO болгон хөрвүүлж JSON буцаана.
 */
@WebServlet("/api/assignments/*")
public class AssignmentApiServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** Business logic service */
    private AssignmentService assignmentService;

    /** JSON mapper */
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        IAssignmentRepository repository = RepositoryFactory.createRepository();
        this.assignmentService = new AssignmentService(repository);

        this.objectMapper = new ObjectMapper();

        /*
         * Java time төрлүүдийг serialize хийх support нэмэж байна.
         * LocalDate зэрэг талбарууд 500 алдаа үүсгэхээс сэргийлнэ.
         */
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * GET /api/assignments
     * GET /api/assignments/{id}
     *
     * JSON API дээр content type болон encoding-ийг үргэлж зөв тохируулна.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                /*
                 * Бүх assignment-ийг domain -> DTO list болгон хөрвүүлээд
                 * JSON array байдлаар буцаана.
                 */
                List<AssignmentDto> dtos = assignmentService.getAllAssignments()
                        .stream()
                        .map(AssignmentDto::new)
                        .collect(Collectors.toList());

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(objectMapper.writeValueAsString(dtos));
                return;
            }

            String idText = pathInfo.substring(1);
            Long id = Long.parseLong(idText);

            Assignment assignment = assignmentService.getAssignmentById(id);

            if (assignment == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            AssignmentDto dto = new AssignmentDto(assignment);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(objectMapper.writeValueAsString(dto));

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * POST /api/assignments
     *
     * Project 1 requirement-ийн дагуу mutating API route-ууд хамгаалагдсан байх ёстой.
     * Login хийгээгүй хэрэглэгч create хийх гэж оролдвол 401 Unauthorized буцаана.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        /*
         * API create route хамгаалалт:
         * web UI шиг redirect хийхгүй.
         * API client-д machine-readable HTTP status code буцаах ёстой.
         */
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            /*
             * Client-ээс ирсэн JSON payload-ийг эхлээд DTO болгон уншина.
             * Ингэснээр API contract domain class-аас салангид байна.
             */
            AssignmentDto requestDto =
                    objectMapper.readValue(request.getInputStream(), AssignmentDto.class);

            Assignment newAssignment = requestDto.toDomain();
            assignmentService.createAssignment(newAssignment);

            /*
             * Service layer save хийсний дараа domain object дотор id, status зэрэг
             * шинэ утгууд орж ирсэн байж болно.
             * Тиймээс response-д domain -> DTO болгож буцаана.
             */
            AssignmentDto responseDto = new AssignmentDto(newAssignment);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(objectMapper.writeValueAsString(responseDto));

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}