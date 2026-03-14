package mn.edu.num.assignmentsystem.infrastructure.persistence;

import mn.edu.num.assignmentsystem.core.ports.IAssignmentRepository;
import mn.edu.num.assignmentsystem.infrastructure.config.DatabaseConnection;

/**
 * RepositoryFactory нь runtime дээр ямар repository ашиглахыг шийднэ.
 */
public class RepositoryFactory {

    /**
     * Repository instance үүсгэнэ.
     */
    public static IAssignmentRepository createRepository() {

        String mode = DatabaseConnection.getInstance().getPersistenceMode();

        if ("MEM".equalsIgnoreCase(mode)) {
            return new InMemoryAssignmentRepository();
        }

        if ("DB".equalsIgnoreCase(mode)) {
            return new JdbcAssignmentRepository();
        }

        throw new IllegalArgumentException("Unknown persistence mode: " + mode);
    }

}