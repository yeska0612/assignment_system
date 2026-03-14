package mn.edu.num.assignmentsystem.infrastructure.persistence;

import java.util.List;

import mn.edu.num.assignmentsystem.core.domain.Assignment;
import mn.edu.num.assignmentsystem.core.ports.IAssignmentRepository;

/**
 * Assignment repository-ийн JDBC implementation.
 * Энэ класс дараагийн алхам дээр SQL query-уудыг хэрэгжүүлнэ.
 */
public class JdbcAssignmentRepository implements IAssignmentRepository {

    @Override
    public void save(Assignment assignment) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public List<Assignment> findAll() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Assignment findById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void update(Assignment assignment) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void deleteById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

}