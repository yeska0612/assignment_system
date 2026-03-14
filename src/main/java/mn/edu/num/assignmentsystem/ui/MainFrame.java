package mn.edu.num.assignmentsystem.ui;

import java.awt.BorderLayout;
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

/**
 * Системийн үндсэн цонх.
 */
public class MainFrame extends JFrame {

    /** Business logic service */
    private final AssignmentService assignmentService;

    /** JTable model */
    private final AssignmentTableModel tableModel;

    /** Assignment жагсаалт харах хүснэгт */
    private final JTable assignmentTable;

    /** Refresh товч */
    private final JButton refreshButton;

    /** Submit товч */
    private JButton submitButton;

    /** Grade товч */
    private JButton gradeButton;

    /** Delete товч */
    private JButton deleteButton;

    /** Title input */
    private JTextField titleField;

    /** Student ID input */
    private JTextField studentIdField;

    /** Course input */
    private JTextField courseCodeField;

    /** Description input */
    private JTextField descriptionField;

    /** Add button */
    private JButton addButton;

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
        initializeActions();
        loadAssignments();
    }

    /**
     * Frame-ийн үндсэн тохиргоо.
     */
    private void initializeFrame() {
        setTitle("Student Assignment Submission System");
        setSize(1300, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    /**
     * UI component-уудыг үүсгэж байрлуулна.
     */
    private void initializeComponents() {
        // JTable-ийн үндсэн тохиргоо
        assignmentTable.setRowSelectionAllowed(true); // Мөр сонгохыг зөвшөөрнө
        assignmentTable.setColumnSelectionAllowed(false); // Багана сонгохгүй
        assignmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Нэг мөр л сонгоно
        assignmentTable.setFillsViewportHeight(true); // Хоосон үед ч хүснэгтийн өндөр дүүргэнэ

        JScrollPane scrollPane = new JScrollPane(assignmentTable);
        add(scrollPane, BorderLayout.CENTER);

        // Top panel
        JPanel topPanel = new JPanel();
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);

        // Bottom panel
        JPanel formPanel = new JPanel();

        submitButton = new JButton("Submit");
        gradeButton = new JButton("Grade");
        deleteButton = new JButton("Delete");

        titleField = new JTextField(10);
        studentIdField = new JTextField(10);
        courseCodeField = new JTextField(10);
        descriptionField = new JTextField(18);

        addButton = new JButton("Add Assignment");

        formPanel.add(submitButton);
        formPanel.add(gradeButton);
        formPanel.add(deleteButton);

        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);

        formPanel.add(new JLabel("Student ID:"));
        formPanel.add(studentIdField);

        formPanel.add(new JLabel("Course:"));
        formPanel.add(courseCodeField);

        formPanel.add(new JLabel("Description:"));
        formPanel.add(descriptionField);

        formPanel.add(addButton);

        add(formPanel, BorderLayout.SOUTH);
    }

    /**
     * Button action-уудыг холбоно.
     */
    private void initializeActions() {
        refreshButton.addActionListener(e -> loadAssignments());
        addButton.addActionListener(e -> createAssignment());
        submitButton.addActionListener(e -> submitAssignment());
        gradeButton.addActionListener(e -> gradeAssignment());
        deleteButton.addActionListener(e -> deleteAssignment());
    }

    /**
     * Assignment жагсаалтыг дахин ачаална.
     */
    private void loadAssignments() {
        try {
            List<Assignment> assignments = assignmentService.getAllAssignments();
            tableModel.setAssignments(assignments);

            // Refresh хийсний дараа өмнөх сонголтыг цэвэрлэнэ
            assignmentTable.clearSelection();

            // Хүснэгтийг дахин зурна
            assignmentTable.revalidate();
            assignmentTable.repaint();

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
     * Form дээрх мэдээллээр шинэ assignment үүсгэнэ.
     */
    private void createAssignment() {
        String title = titleField.getText();
        String studentId = studentIdField.getText();
        String courseCode = courseCodeField.getText();
        String description = descriptionField.getText();

        if (title.isBlank() || studentId.isBlank() || courseCode.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Title, Student ID, Course Code заавал бөглөнө.",
                    "Алдаа",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            Assignment assignment = new Assignment(title, studentId, courseCode, description);
            assignmentService.createAssignment(assignment);
            loadAssignments();

            // Form цэвэрлэх
            titleField.setText("");
            studentIdField.setText("");
            courseCodeField.setText("");
            descriptionField.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Алдаа", JOptionPane.ERROR_MESSAGE);
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
            loadAssignments();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Алдаа", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Сонгосон assignment-д оноо өгнө.
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
            Assignment assignment = tableModel.getAssignmentAt(row);
            assignmentService.gradeAssignment(assignment.getId(), score);
            loadAssignments();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Оноог зөв тоогоор оруулна уу.", "Алдаа", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Алдаа", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Сонгосон assignment-ийг устгана.
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
                loadAssignments();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Алдаа", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}