package mn.edu.num.assignmentsystem.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import mn.edu.num.assignmentsystem.core.domain.Assignment;

/**
 * Assignment-уудыг JTable дээр харуулах model.
 */
public class AssignmentTableModel extends AbstractTableModel {

    /** Хүснэгтийн баганын нэрс */
    private final String[] columnNames = {
            "ID", "Гарчиг", "Оюутны ID", "Хичээлийн код", "Илгээсэн огноо", "Төлөв", "Оноо"
    };

    /** Хүснэгтэд харагдах assignment жагсаалт */
    private List<Assignment> assignments;

    /**
     * Анх хоосон model үүсгэнэ.
     */
    public AssignmentTableModel() {
        this.assignments = new ArrayList<>();
    }

    /**
     * Хүснэгтийн өгөгдлийг шинэчилнэ.
     */
    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
        fireTableDataChanged();
    }

    /**
     * Сонгосон мөрийн assignment-ийг буцаана.
     */
    public Assignment getAssignmentAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= assignments.size()) {
            return null;
        }
        return assignments.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return assignments.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Assignment assignment = assignments.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return assignment.getId();
            case 1:
                return assignment.getTitle();
            case 2:
                return assignment.getStudentId();
            case 3:
                return assignment.getCourseCode();
            case 4:
                return assignment.getSubmissionDate();
            case 5:
                return assignment.getStatus();
            case 6:
                return assignment.getScore();
            default:
                return null;
        }
    }
}