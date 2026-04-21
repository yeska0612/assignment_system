package mn.edu.num.assignmentsystem.core.ports;

import java.util.List;

import mn.edu.num.assignmentsystem.core.domain.Assignment;

/**
 * Assignment өгөгдлийг хадгалах persistence boundary.
 *
 * Core layer нь өгөгдлийг яг хаана, яаж хадгалагдаж байгааг мэдэхгүй.
 * Харин энэ interface-ээр дамжуулж repository-той ажиллана.
 *
 * Ингэснээр бид InMemory, JDBC, эсвэл цаашдаа өөр persistence
 * implementation-уудыг core логикт өөрчлөлтгүйгээр сольж ашиглаж чадна.
 */
public interface IAssignmentRepository {

    /**
     * Шинэ assignment хадгална.
     */
    void save(Assignment assignment);

    /**
     * Бүх assignment-уудыг авна.
     */
    List<Assignment> findAll();

    /**
     * ID-аар assignment авна.
     */
    Assignment findById(Long id);

    /**
     * Assignment шинэчилнэ.
     */
    void update(Assignment assignment);

    /**
     * ID-аар assignment устгана.
     */
    void deleteById(Long id);
}