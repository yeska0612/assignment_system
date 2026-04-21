package mn.edu.num.assignmentsystem.core.application;

import mn.edu.num.assignmentsystem.core.domain.Assignment;

/**
 * AssignmentDto нь REST API болон UI layer рүү
 * domain object-ийг шууд алдахгүйгээр дамжуулах
 * зориулалттай data transfer object юм.
 *
 * Яагаад DTO хэрэгтэй вэ?
 * - API нь domain object-оос хэт хамааралтай байхгүй
 * - зөвхөн client-д хэрэгтэй талбаруудыг гаргаж өгнө
 * - цаашдаа domain өөрчлөгдсөн ч API contract-оо тогтвортой барихад тусална
 */
public class AssignmentDto {

    private Long id;
    private String title;
    private String studentId;
    private String courseCode;
    private String description;
    private String submissionDate;
    private String status;
    private Double score;
    private String feedback;

    /**
     * Jackson JSON mapping-д хэрэгтэй parameterless constructor.
     */
    public AssignmentDto() {
    }

    /**
     * Domain object-оос DTO үүсгэх convenience constructor.
     */
    public AssignmentDto(Assignment assignment) {
        this.id = assignment.getId();
        this.title = assignment.getTitle();
        this.studentId = assignment.getStudentId();
        this.courseCode = assignment.getCourseCode();
        this.description = assignment.getDescription();
        this.submissionDate = assignment.getSubmissionDate() == null
                ? null
                : assignment.getSubmissionDate().toString();
        this.status = assignment.getStatus() == null
                ? null
                : assignment.getStatus().name();
        this.score = assignment.getScore();
        this.feedback = assignment.getFeedback();
    }

    /**
     * DTO -> Domain хөрвүүлэлт.
     *
     * POST request-ээр ирсэн JSON-ийг domain object болгон
     * service layer рүү дамжуулахад ашиглаж болно.
     */
    public Assignment toDomain() {
        Assignment assignment = new Assignment();
        assignment.setId(id);
        assignment.setTitle(title);
        assignment.setStudentId(studentId);
        assignment.setCourseCode(courseCode);
        assignment.setDescription(description);
        assignment.setScore(score);
        assignment.setFeedback(feedback);
        return assignment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}