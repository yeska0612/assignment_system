package mn.edu.num.assignmentsystem.core.domain;

import java.time.LocalDate;

/**
 * Assignment entity class.
 * 
 * Энэ класс нь оюутны даалгаврын үндсэн мэдээллийг хадгална.
 * Domain entity учраас өгөгдөл болон суурь дүрмүүдийг өөртөө агуулж болно.
 */
public class Assignment {

    /** Даалгаврын дахин давтагдашгүй дугаар */
    private Long id;

    /** Даалгаврын гарчиг */
    private String title;

    /** Оюутны код */
    private String studentId;

    /** Хичээлийн код */
    private String courseCode;

    /** Даалгаврын тайлбар */
    private String description;

    /** Илгээсэн огноо */
    private LocalDate submissionDate;

    /** Даалгаврын төлөв */
    private AssignmentStatus status;

    /** Оноо */
    private Double score;

    /** Багшийн сэтгэгдэл / тайлбар */
    private String feedback;

    /**
     * Параметргүй байгуулагч.
     * JDBC, UI, mapping үед хэрэгтэй байж болно.
     */
    public Assignment() {
        this.status = AssignmentStatus.DRAFT;
    }

    /**
     * Шинэ даалгавар үүсгэх байгуулагч.
     * Анхны төлөв автоматаар DRAFT байна.
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