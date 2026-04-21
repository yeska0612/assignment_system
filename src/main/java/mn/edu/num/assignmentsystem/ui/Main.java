package mn.edu.num.assignmentsystem.ui;

import javax.swing.SwingUtilities;

import mn.edu.num.assignmentsystem.core.application.AssignmentService;
import mn.edu.num.assignmentsystem.core.ports.IAssignmentRepository;
import mn.edu.num.assignmentsystem.infrastructure.persistence.RepositoryFactory;

/**
 * Програмын эхлэх цэг.
 *
 * Энэ класс нь application-ийг "угсарч" өгдөг composition root юм.
 * Өөрөөр хэлбэл:
 * - ямар repository implementation ашиглахыг factory-аар шийднэ
 * - service-ийг repository-той холбоно
 * - UI-г service-ээр хангаж ажиллуулна
 *
 * Ингэснээр UI layer нь persistence implementation-ийг шууд мэдэхгүй,
 * зөвхөн service-тэй харилцдаг тул архитектурын dependency direction алдагдахгүй.
 */
public class Main {

    /**
     * Програмын эхлэх method.
     */
    public static void main(String[] args) {

        /*
         * RepositoryFactory нь ямар repository ашиглахыг нэг цэгээс шийддэг.
         * UI дотор JdbcAssignmentRepository зэрэг implementation-ийг
         * шууд new хийхгүй байх нь Hexagonal Architecture-ийн чухал шаардлага.
         */
        IAssignmentRepository repository = RepositoryFactory.createRepository();

        /*
         * Service layer нь бизнес дүрэм болон validation-ийг хариуцна.
         * UI нь өгөгдөл хадгалах логикыг өөрөө мэдэхгүй,
         * зөвхөн service-ийн public method-үүдийг дуудах ёстой.
         */
        AssignmentService assignmentService = new AssignmentService(repository);

        /*
         * Swing UI-г Event Dispatch Thread дээр эхлүүлж байгаа нь
         * Swing application-ийн thread safety-г хангах стандарт арга юм.
         */
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(assignmentService);
            frame.setVisible(true);
        });
    }
}