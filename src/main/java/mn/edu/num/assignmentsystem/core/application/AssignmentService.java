package mn.edu.num.assignmentsystem.core.application;

import java.time.LocalDate;
import java.util.List;

import mn.edu.num.assignmentsystem.core.domain.Assignment;
import mn.edu.num.assignmentsystem.core.domain.AssignmentStatus;
import mn.edu.num.assignmentsystem.core.ports.IAssignmentRepository;

/**
 * Assignment-тэй холбоотой бизнес логикыг хариуцах service класс.
 */
public class AssignmentService {

    /** Assignment хадгалах repository port */
    private final IAssignmentRepository assignmentRepository;

    /**
     * Constructor injection ашиглаж repository-г дамжуулан авна.
     */
    public AssignmentService(IAssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    /**
     * Шинэ assignment үүсгэж хадгална.
     */
    public void createAssignment(Assignment assignment) {
        validateBasicFields(assignment);

        // Шинэ assignment үргэлж DRAFT төлөвтэй эхэлнэ
        assignment.setStatus(AssignmentStatus.DRAFT);

        assignmentRepository.save(assignment);
    }

    /**
     * Assignment шинэчилнэ.
     */
    public void updateAssignment(Assignment assignment) {
        if (assignment.getId() == null) {
            throw new IllegalArgumentException("Шинэчлэх assignment-ийн ID хоосон байна.");
        }

        validateBasicFields(assignment);
        assignmentRepository.update(assignment);
    }

    /**
     * ID-аар assignment устгана.
     */
    public void deleteAssignment(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Устгах assignment-ийн ID хоосон байна.");
        }

        assignmentRepository.deleteById(id);
    }

    /**
     * Бүх assignment жагсаалтыг буцаана.
     */
    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    /**
     * ID-аар assignment олно.
     */
    public Assignment getAssignmentById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Хайх assignment-ийн ID хоосон байна.");
        }

        return assignmentRepository.findById(id);
    }

    /**
     * Assignment-ийг submit хийнэ.
     * 
     * Зөвхөн DRAFT -> SUBMITTED төлөв рүү шилжинэ.
     * Submission date-г тухайн өдрийн огноогоор бөглөнө.
     */
    public void submitAssignment(Long id) {
        Assignment assignment = getExistingAssignment(id);

        if (assignment.getStatus() != AssignmentStatus.DRAFT) {
            throw new IllegalStateException("Зөвхөн DRAFT төлөвтэй assignment-ийг submit хийж болно.");
        }

        assignment.setStatus(AssignmentStatus.SUBMITTED);
        assignment.setSubmissionDate(LocalDate.now());

        assignmentRepository.update(assignment);
    }

    /**
     * Assignment-ийг grade хийнэ.
     * 
     * Зөвхөн SUBMITTED -> GRADED төлөв рүү шилжинэ.
     * Оноо заавал өгөгдсөн байна.
     */
    public void gradeAssignment(Long id, Double score, String feedback) {
        Assignment assignment = getExistingAssignment(id);

        if (assignment.getStatus() != AssignmentStatus.SUBMITTED) {
            throw new IllegalStateException("Зөвхөн SUBMITTED төлөвтэй assignment-ийг grade хийж болно.");
        }

        if (score == null) {
            throw new IllegalArgumentException("Grade хийх үед оноо заавал оруулна.");
        }

        assignment.setScore(score);
        assignment.setFeedback(feedback);
        assignment.setStatus(AssignmentStatus.GRADED);

        assignmentRepository.update(assignment);
    }

    /**
     * Assignment-ийг reject хийнэ.
     * 
     * Зөвхөн SUBMITTED -> REJECTED төлөв рүү шилжинэ.
     * Feedback хоосон байж болохгүй.
     */
    public void rejectAssignment(Long id, String feedback) {
        Assignment assignment = getExistingAssignment(id);

        if (assignment.getStatus() != AssignmentStatus.SUBMITTED) {
            throw new IllegalStateException("Зөвхөн SUBMITTED төлөвтэй assignment-ийг reject хийж болно.");
        }

        if (feedback == null || feedback.trim().isEmpty()) {
            throw new IllegalArgumentException("Reject хийх үед feedback заавал оруулна.");
        }

        assignment.setFeedback(feedback);
        assignment.setStatus(AssignmentStatus.REJECTED);

        assignmentRepository.update(assignment);
    }

    /**
     * Assignment-ийн үндсэн талбаруудыг шалгана.
     */
    private void validateBasicFields(Assignment assignment) {
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment объект хоосон байна.");
        }

        if (assignment.getTitle() == null || assignment.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Даалгаврын гарчиг хоосон байж болохгүй.");
        }

        if (assignment.getStudentId() == null || assignment.getStudentId().trim().isEmpty()) {
            throw new IllegalArgumentException("Оюутны ID хоосон байж болохгүй.");
        }

        if (assignment.getCourseCode() == null || assignment.getCourseCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Хичээлийн код хоосон байж болохгүй.");
        }
    }

    /**
     * ID-аар assignment авч, байхгүй бол алдаа шиднэ.
     */
    private Assignment getExistingAssignment(Long id) {
        Assignment assignment = getAssignmentById(id);

        if (assignment == null) {
            throw new IllegalArgumentException("Өгөгдсөн ID-тай assignment олдсонгүй.");
        }

        return assignment;
    }
}