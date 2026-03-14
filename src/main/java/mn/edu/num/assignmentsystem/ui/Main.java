package mn.edu.num.assignmentsystem.ui;

import javax.swing.SwingUtilities;

import mn.edu.num.assignmentsystem.core.application.AssignmentService;
import mn.edu.num.assignmentsystem.core.ports.IAssignmentRepository;
import mn.edu.num.assignmentsystem.infrastructure.persistence.RepositoryFactory;

/**
 * Системийн эхлэх цэг.
 */
public class Main {

    public static void main(String[] args) {

        // Repository factory-аас repository үүсгэнэ
        IAssignmentRepository repository = RepositoryFactory.createRepository();

        // Service layer үүсгэнэ
        AssignmentService service = new AssignmentService(repository);

        // UI эхлүүлнэ
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(service);
            frame.setVisible(true);
        });
    }
}