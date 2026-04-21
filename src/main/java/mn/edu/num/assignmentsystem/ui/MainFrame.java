package mn.edu.num.assignmentsystem.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import mn.edu.num.assignmentsystem.core.application.AssignmentService;
import mn.edu.num.assignmentsystem.core.domain.Assignment;
import mn.edu.num.assignmentsystem.core.domain.AssignmentStatus;

/**
 * Assignment системийн үндсэн UI цонх.
 *
 * Энэ класс нь:
 * - assignment жагсаалтыг хүснэгтээр харуулах
 * - хүснэгтийн мөр сонгоход form дээр мэдээлэл ачаалах
 * - save button-оор create/update хоёрыг удирдах
 * - submit / grade / reject / delete үйлдэл хийх
 * - button-уудын төлөвийг тухайн assignment-ийн status-аас хамааруулж удирдах
 *
 * Lab 07-ийн master-detail requirement-ийн дагуу:
 * - row click хийхэд selectedAssignmentId хадгалагдана
 * - form автоматаар бөглөгдөнө
 * - Save товчийн текст Update болж өөрчлөгдөнө
 */
public class MainFrame extends JFrame {

    /** Business logic service */
    private final AssignmentService assignmentService;

    /** JTable-ийн model */
    private final AssignmentTableModel tableModel;

    /** Assignment жагсаалт харах хүснэгт */
    private final JTable assignmentTable;

    /** Refresh товч */
    private final JButton refreshButton;

    /**
     * Save / Update товч.
     *
     * selectedAssignmentId == null бол CREATE
     * selectedAssignmentId != null бол UPDATE ажиллана.
     */
    private JButton saveButton;

    /** Submit товч */
    private JButton submitButton;

    /** Grade товч */
    private JButton gradeButton;

    /** Reject товч */
    private JButton rejectButton;

    /** Delete товч */
    private JButton deleteButton;

    /** Title input */
    private JTextField titleField;

    /** Student ID input */
    private JTextField studentIdField;

    /** Course code input */
    private JTextField courseCodeField;

    /** Description input */
    private JTextField descriptionField;

    /**
     * Одоогоор сонгогдсон assignment-ийн ID.
     *
     * Энэ хувьсагч нь UI state management-д маш чухал:
     * - null байвал шинэ assignment үүсгэнэ
     * - утгатай байвал тухайн assignment-ийг update хийнэ
     */
    private Long selectedAssignmentId = null;

    /**
     * Constructor.
     */
    public MainFrame(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;

        this.tableModel = new AssignmentTableModel();
        this.assignmentTable = new JTable(tableModel);
        this.refreshButton = new JButton("Refresh");

        initializeFrame();
        initializeComponents();
        initializeLayout();
        initializeActions();
        loadAssignments();
        updateButtonStates();
    }

    /**
     * Frame-ийн үндсэн тохиргоо.
     */
    private void initializeFrame() {
        setTitle("Student Assignment Submission System");
        setSize(1100, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
    }

    /**
     * UI component-уудыг үүсгэнэ.
     */
    private void initializeComponents() {
        // JTable-ийн үндсэн сонголтын тохиргоо
        assignmentTable.setRowSelectionAllowed(true);
        assignmentTable.setColumnSelectionAllowed(false);
        assignmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        assignmentTable.setFillsViewportHeight(true);

        // Form input-ууд
        titleField = new JTextField(15);
        studentIdField = new JTextField(12);
        courseCodeField = new JTextField(12);
        descriptionField = new JTextField(20);

        /*
         * Description нь create болон update үед хэрэглэгчээс орж ирэх ёстой.
         * Тиймээс editable байх шаардлагатай.
         */
        descriptionField.setEditable(true);

        // Action button-ууд
        saveButton = new JButton("Save");
        submitButton = new JButton("Submit");
        gradeButton = new JButton("Grade");
        rejectButton = new JButton("Reject");
        deleteButton = new JButton("Delete");
    }

    /**
     * UI layout-ийг байрлуулна.
     */
    private void initializeLayout() {
        JPanel topContainer = new JPanel(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 10));

        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);

        formPanel.add(new JLabel("Student ID:"));
        formPanel.add(studentIdField);

        formPanel.add(new JLabel("Course Code:"));
        formPanel.add(courseCodeField);

        formPanel.add(new JLabel("Description:"));
        formPanel.add(descriptionField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.add(saveButton);
        buttonPanel.add(submitButton);
        buttonPanel.add(gradeButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        topContainer.add(formPanel, BorderLayout.CENTER);
        topContainer.add(buttonPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(assignmentTable);

        add(topContainer, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Action listener-үүдийг холбоно.
     */
    private void initializeActions() {
        refreshButton.addActionListener(e -> {
            clearForm();
            loadAssignments();
        });

        /*
         * Save button нь хоёр үүрэгтэй:
         * - selectedAssignmentId байхгүй бол create
         * - selectedAssignmentId байвал update
         *
         * Ингэснээр Lab 07-ийн robust save/update requirement хангагдана.
         */
        saveButton.addActionListener(e -> saveOrUpdateAssignment());

        submitButton.addActionListener(e -> submitAssignment());
        gradeButton.addActionListener(e -> gradeAssignment());
        rejectButton.addActionListener(e -> rejectAssignment());
        deleteButton.addActionListener(e -> deleteAssignment());

        /*
         * Master-detail navigation:
         * row select хийхэд тухайн assignment-ийн мэдээллийг form дээр ачаална.
         */
        assignmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedAssignmentToForm();
            }
        });
    }

    /**
     * Service layer-оос бүх assignment-ийг авч JTable дээр харуулна.
     *
     * Энэ method нь:
     * - application эхлэх үед
     * - refresh дарахад
     * - create / update / submit / grade / reject / delete хийсний дараа
     * хүснэгтийг дахин ачаалахад ашиглагдана.
     */
    private void loadAssignments() {
        try {
            List<Assignment> assignments = assignmentService.getAllAssignments();
            tableModel.setAssignments(assignments);

            assignmentTable.revalidate();
            assignmentTable.repaint();

            updateButtonStates();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Refresh алдаа",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Save button-ийн create/update нэгдсэн логик.
     *
     * - selectedAssignmentId == null бол шинэ assignment үүсгэнэ
     * - selectedAssignmentId != null бол сонгосон assignment-ийг шинэчилнэ
     *
     * Validation төрлийн алдааг WARNING popup-аар,
     * бусад системийн алдааг ERROR popup-аар харуулна.
     */
    private void saveOrUpdateAssignment() {
        String title = titleField.getText().trim();
        String studentId = studentIdField.getText().trim();
        String courseCode = courseCodeField.getText().trim();
        String description = descriptionField.getText().trim();

        try {
            Assignment assignment = new Assignment(title, studentId, courseCode, description);

            if (selectedAssignmentId == null) {
                /*
                 * Ямар ч мөр сонгогдоогүй бол create горим ажиллана.
                 */
                assignmentService.createAssignment(assignment);
                JOptionPane.showMessageDialog(this, "Assignment амжилттай нэмэгдлээ.");
            } else {
                /*
                 * selectedAssignmentId байгаа үед update горим ажиллана.
                 * Зөвхөн DRAFT төлөвтэй assignment update хийгдэхийг
                 * service layer өөрөө шалгана.
                 */
                assignment.setId(selectedAssignmentId);
                assignmentService.updateAssignment(assignment);
                JOptionPane.showMessageDialog(this, "Assignment амжилттай шинэчлэгдлээ.");
            }

            clearForm();
            loadAssignments();

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Validation warning",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Operation error",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "System error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Сонгосон assignment-ийг submit хийнэ.
     */
    private void submitAssignment() {
        int row = assignmentTable.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Assignment сонгоно уу.");
            return;
        }

        Assignment assignment = tableModel.getAssignmentAt(row);

        try {
            assignmentService.submitAssignment(assignment.getId());

            clearForm();
            loadAssignments();

            JOptionPane.showMessageDialog(this, "Assignment SUBMITTED төлөвт орлоо.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Алдаа",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Сонгосон assignment-д оноо өгч GRADED төлөвт оруулна.
     */
    private void gradeAssignment() {
        int row = assignmentTable.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Assignment сонгоно уу.");
            return;
        }

        String scoreText = JOptionPane.showInputDialog(this, "Оноо оруул:");

        if (scoreText == null || scoreText.isBlank()) {
            return;
        }

        try {
            Double score = Double.parseDouble(scoreText);

            String feedback = JOptionPane.showInputDialog(
                    this,
                    "Grade хийх тайлбар оруул (хоосон байж болно):"
            );

            if (feedback != null) {
                feedback = feedback.trim();
                if (feedback.isBlank()) {
                    feedback = null;
                }
            }

            Assignment assignment = tableModel.getAssignmentAt(row);
            assignmentService.gradeAssignment(assignment.getId(), score, feedback);

            clearForm();
            loadAssignments();

            JOptionPane.showMessageDialog(this, "Assignment GRADED төлөвт орлоо.");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Оноог зөв тоогоор оруулна уу.",
                    "Алдаа",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Алдаа",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Сонгосон assignment-ийг reject хийнэ.
     */
    private void rejectAssignment() {
        int row = assignmentTable.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Assignment сонгоно уу.");
            return;
        }

        String feedback = JOptionPane.showInputDialog(
                this,
                "Reject хийх шалтгаан оруул:"
        );

        if (feedback == null) {
            return;
        }

        feedback = feedback.trim();

        if (feedback.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Reject хийх шалтгаан хоосон байж болохгүй.",
                    "Алдаа",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            Assignment assignment = tableModel.getAssignmentAt(row);
            assignmentService.rejectAssignment(assignment.getId(), feedback);

            clearForm();
            loadAssignments();

            JOptionPane.showMessageDialog(this, "Assignment REJECTED төлөвт орлоо.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Алдаа",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Сонгосон assignment-ийг устгана.
     *
     * Устгахаас өмнө хэрэглэгчээс баталгаажуулалт асууж,
     * accidental delete-ээс хамгаална.
     */
    private void deleteAssignment() {
        int row = assignmentTable.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Assignment сонгоно уу.");
            return;
        }

        Assignment assignment = tableModel.getAssignmentAt(row);

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Энэ assignment-ийг устгах уу?",
                "Баталгаажуулалт",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            try {
                assignmentService.deleteAssignment(assignment.getId());

                clearForm();
                loadAssignments();

                JOptionPane.showMessageDialog(this, "Assignment амжилттай устгагдлаа.");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        this,
                        e.getMessage(),
                        "Алдаа",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    /**
     * Хүснэгтээс сонгосон assignment-ийн мэдээллийг form дээр ачаална.
     *
     * Энэ нь master-detail navigation-ийн үндсэн логик:
     * - selectedAssignmentId хадгална
     * - form автоматаар бөглөгдөнө
     * - Save товчийн текст Update болж өөрчлөгдөнө
     */
    private void loadSelectedAssignmentToForm() {
        int row = assignmentTable.getSelectedRow();

        if (row < 0) {
            selectedAssignmentId = null;
            clearFormFieldsOnly();
            updateButtonStates();
            return;
        }

        Assignment assignment = tableModel.getAssignmentAt(row);

        if (assignment == null) {
            selectedAssignmentId = null;
            clearFormFieldsOnly();
            updateButtonStates();
            return;
        }

        selectedAssignmentId = assignment.getId();

        titleField.setText(assignment.getTitle());
        studentIdField.setText(assignment.getStudentId());
        courseCodeField.setText(assignment.getCourseCode());
        descriptionField.setText(
                assignment.getDescription() == null ? "" : assignment.getDescription()
        );

        updateButtonStates();
    }

    /**
     * Сонгосон assignment-ийн төлөвөөс хамаарч button-уудын идэвхийг удирдана.
     */
    private void updateButtonStates() {
        int row = assignmentTable.getSelectedRow();

        /*
         * Default төлөв:
         * - ямар ч мөр сонгогдоогүй үед Save горим байна
         * - delete/submit/grade/reject disabled байна
         */
        saveButton.setText(selectedAssignmentId == null ? "Save" : "Update");
        saveButton.setEnabled(true);

        submitButton.setEnabled(false);
        gradeButton.setEnabled(false);
        rejectButton.setEnabled(false);
        deleteButton.setEnabled(false);

        if (row < 0) {
            return;
        }

        Assignment assignment = tableModel.getAssignmentAt(row);

        if (assignment == null) {
            return;
        }

        /*
         * Сонгосон мөр байгаа үед delete идэвхтэй болно.
         * Save button аль хэдийн Update тексттэй болсон байна.
         */
        deleteButton.setEnabled(true);

        if (assignment.getStatus() == AssignmentStatus.DRAFT) {
            submitButton.setEnabled(true);
        } else if (assignment.getStatus() == AssignmentStatus.SUBMITTED) {
            gradeButton.setEnabled(true);
            rejectButton.setEnabled(true);
        }
    }

    /**
     * Зөвхөн form талбаруудыг цэвэрлэнэ.
     */
    private void clearFormFieldsOnly() {
        titleField.setText("");
        studentIdField.setText("");
        courseCodeField.setText("");
        descriptionField.setText("");
    }

    /**
     * Form болон selection-ийг цэвэрлэнэ.
     *
     * Энэ method дуудагдсаны дараа UI create mode руу буцна:
     * - selectedAssignmentId = null
     * - хүснэгтийн selection арилна
     * - Save товчийн текст дахин Save болно
     */
    private void clearForm() {
        clearFormFieldsOnly();
        selectedAssignmentId = null;
        assignmentTable.clearSelection();
        updateButtonStates();
    }
}