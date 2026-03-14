package mn.edu.num.assignmentsystem.ui;

import mn.edu.num.assignmentsystem.core.application.AssignmentService;
import mn.edu.num.assignmentsystem.core.ports.IAssignmentRepository;
import mn.edu.num.assignmentsystem.infrastructure.persistence.RepositoryFactory;

/**
 * Системийн эхлэх цэг.
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("Student Assignment Submission System");
        System.out.println("System started successfully...");
        System.out.println("=====================================");

        // Repository factory ашиглан repository үүсгэнэ
        IAssignmentRepository repository = RepositoryFactory.createRepository();

        // Service үүсгэнэ
        AssignmentService service = new AssignmentService(repository);

        System.out.println("Repository initialized successfully.");
        System.out.println("Service layer ready.");

    }
}