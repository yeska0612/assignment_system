package mn.edu.num.assignmentsystem.core.ports;

import java.util.List;
import mn.edu.num.assignmentsystem.core.domain.Assignment;

/**
 * Assignment entity-г хадгалах repository interface.
 * 
 * Энэ interface нь core layer-оос persistence layer-ийг салгаж өгнө.
 * Infrastructure layer дээр JDBC implementation бичигдэнэ.
 */
public interface IAssignmentRepository {

    /**
     * Шинэ assignment хадгалах.
     */
    void save(Assignment assignment);

    /**
     * Бүх assignment-уудыг авах.
     */
    List<Assignment> findAll();

    /**
     * ID-аар нэг assignment олох.
     */
    Assignment findById(Long id);

    /**
     * Assignment шинэчлэх.
     */
    void update(Assignment assignment);

    /**
     * ID-аар assignment устгах.
     */
    void deleteById(Long id);
}