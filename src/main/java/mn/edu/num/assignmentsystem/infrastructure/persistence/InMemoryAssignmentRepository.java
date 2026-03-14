package mn.edu.num.assignmentsystem.infrastructure.persistence;

import java.util.ArrayList;
import java.util.List;

import mn.edu.num.assignmentsystem.core.domain.Assignment;
import mn.edu.num.assignmentsystem.core.ports.IAssignmentRepository;

/**
 * Assignment repository-ийн In-Memory implementation.
 * 
 * Энэ implementation нь өгөгдлийг RAM дээр хадгална.
 * Database ашиглахгүй.
 */
public class InMemoryAssignmentRepository implements IAssignmentRepository {

    /** Assignment-уудыг хадгалах жагсаалт */
    private final List<Assignment> assignments = new ArrayList<>();

    /** ID auto increment хийхэд ашиглана */
    private long idCounter = 1;

    @Override
    public void save(Assignment assignment) {

        assignment.setId(idCounter++);
        assignments.add(assignment);

    }

    @Override
    public List<Assignment> findAll() {
        return new ArrayList<>(assignments);
    }

    @Override
    public Assignment findById(Long id) {

        for (Assignment assignment : assignments) {
            if (assignment.getId().equals(id)) {
                return assignment;
            }
        }

        return null;
    }

    @Override
    public void update(Assignment assignment) {

        for (int i = 0; i < assignments.size(); i++) {

            if (assignments.get(i).getId().equals(assignment.getId())) {
                assignments.set(i, assignment);
                return;
            }

        }

    }

    @Override
    public void deleteById(Long id) {

        assignments.removeIf(a -> a.getId().equals(id));

    }

}