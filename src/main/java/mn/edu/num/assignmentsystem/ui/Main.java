package mn.edu.num.assignmentsystem.ui;

import javax.swing.SwingUtilities;

import mn.edu.num.assignmentsystem.core.application.AssignmentService;
import mn.edu.num.assignmentsystem.core.ports.IAssignmentRepository;
import mn.edu.num.assignmentsystem.infrastructure.persistence.RepositoryFactory;

/**
 * Системийн эхлэх цэг.
 */
public class Main {

    /**
     * Програмын эхлэх method.
     */
    public static void main(String[] args) {

        // Repository үүсгэнэ
        IAssignmentRepository repository = RepositoryFactory.createRepository();

        // Service үүсгэнэ
        AssignmentService assignmentService = new AssignmentService(repository);

        // UI эхлүүлнэ
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(assignmentService);
            frame.setVisible(true);
        });
    }
}