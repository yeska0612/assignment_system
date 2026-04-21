package mn.edu.num.assignmentsystem.core.domain;

import java.time.LocalDate;

/**
 * Оюутны даалгаврыг илэрхийлэх үндсэн domain entity.
 *
 * Энэ класс нь assignment-ийн өгөгдөл болон workflow-д оролцох
 * үндсэн төлөв мэдээллүүдийг хадгална.
 *
 * Жишээлбэл:
 * - эхлээд DRAFT төлөвтэй үүснэ
 * - дараа нь SUBMITTED болж илгээгдэнэ
 * - эцэст нь GRADED эсвэл REJECTED төлөвт шилжинэ
 */
public class Assignment {
    private Long id;

    private String title;

    private String studentId;

    private String courseCode;

    private String description;

    private LocalDate submissionDate;

    private AssignmentStatus status;

    private Double score;

    private String feedback;

    public Assignment() {
        this.status = AssignmentStatus.DRAFT;
    }

    /**
     * Шинэ assignment үүсгэх байгуулагч.
     */
    public Assignment(String title, String studentId, String courseCode, String description) {
        this.title = title;
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.description = description;
        this.status = AssignmentStatus.DRAFT;
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

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
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