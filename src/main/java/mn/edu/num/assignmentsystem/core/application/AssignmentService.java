package mn.edu.num.assignmentsystem.core.application;

import java.time.LocalDate;
import java.util.List;

import mn.edu.num.assignmentsystem.core.domain.Assignment;
import mn.edu.num.assignmentsystem.core.domain.AssignmentStatus;
import mn.edu.num.assignmentsystem.core.ports.IAssignmentRepository;

/**
 * Assignment-тэй холбоотой бизнес логикыг хариуцна.
 * - шинэ assignment үүсгэх
 * - assignment шинэчлэх
 * - assignment устгах
 * - assignment submit хийх
 * - assignment grade хийх
 * - бүх assignment жагсаалт авах
 * - ID-аар assignment авах
 */
public class AssignmentService {

    /** Repository interface */
    private final IAssignmentRepository assignmentRepository;

    /**
     * Constructor.
     */
    public AssignmentService(IAssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    /**
     * Шинэ assignment үүсгэж хадгална.
     * Шинэ assignment үргэлж DRAFT төлөвтэй эхэлнэ.
     */
    public void createAssignment(Assignment assignment) {
        validateBasicFields(assignment);

        // Шинэ assignment автоматаар DRAFT байна
        assignment.setStatus(AssignmentStatus.DRAFT);

        assignmentRepository.save(assignment);
    }

    /**
     * Assignment шинэчилнэ.
     * Зөвхөн DRAFT төлөвтэй assignment-ийг шинэчилж болно.
     */
    public void updateAssignment(Assignment assignment) {
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment объект хоосон байна.");
        }

        if (assignment.getId() == null) {
            throw new IllegalArgumentException("Шинэчлэх assignment-ийн ID хоосон байна.");
        }

        // DB дээр байгаа одоогийн assignment-ийг авна
        Assignment existingAssignment = getExistingAssignment(assignment.getId());

        // Зөвхөн DRAFT төлөвтэй assignment-ийг засах боломжтой
        if (existingAssignment.getStatus() != AssignmentStatus.DRAFT) {
            throw new IllegalStateException("Зөвхөн DRAFT төлөвтэй assignment-ийг засаж болно.");
        }

        validateBasicFields(assignment);

        // Хуучин workflow мэдээллийг хадгалж үлдээнэ
        assignment.setStatus(existingAssignment.getStatus());
        assignment.setSubmissionDate(existingAssignment.getSubmissionDate());
        assignment.setScore(existingAssignment.getScore());
        assignment.setFeedback(existingAssignment.getFeedback());

        assignmentRepository.update(assignment);
    }

    /**
     * Assignment устгана.
     */
    public void deleteAssignment(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Устгах assignment-ийн ID хоосон байна.");
        }

        // Байгаа эсэхийг эхлээд шалгана
        getExistingAssignment(id);

        assignmentRepository.deleteById(id);
    }

    /**
     * Бүх assignment-уудыг авна.
     */
    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    /**
     * ID-аар assignment авна.
     */
    public Assignment getAssignmentById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Хайх assignment-ийн ID хоосон байна.");
        }

        return assignmentRepository.findById(id);
    }

    /**
     * Assignment-ийг submit хийнэ.
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
     * UI дээрээс зөвхөн ID ба оноо ирэх хувилбар.
     */
    public void gradeAssignment(Long id, Double score) {
        gradeAssignment(id, score, null);
    }

    /**
     * Assignment-ийг grade хийнэ.
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