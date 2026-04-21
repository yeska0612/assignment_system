package mn.edu.num.assignmentsystem.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import mn.edu.num.assignmentsystem.core.domain.Assignment;

/**
 * Assignment-уудыг JTable дээр харуулах table model.
 *
 * Энэ класс нь domain layer-оос ирсэн Assignment объектуудыг
 * Swing-ийн JTable ойлгох мөр, баганын бүтэц рүү хөрвүүлдэг adapter юм.
 *
 * Өөрөөр хэлбэл UI нь List<Assignment>-ийг шууд зурахгүй.
 * Харин AbstractTableModel-оор дамжуулж хэдэн мөртэй, хэдэн баганатай,
 * тухайн нүдэнд ямар утга харагдахыг тодорхойлно.
 */
public class AssignmentTableModel extends AbstractTableModel {

    /** JTable дээр харагдах баганын нэрс */
    private final String[] columnNames = {
            "ID",
            "Title",
            "Student ID",
            "Course Code",
            "Description",
            "Submission Date",
            "Status",
            "Score"
    };

    /** Хүснэгтэд харуулах assignment жагсаалт */
    private List<Assignment> assignments;

    /**
     * Анх хоосон model үүсгэнэ.
     *
     * UI анх ачаалагдах үед хүснэгт хоосон байж болно.
     * Дараа нь service-ээс өгөгдөл ачаалаад setAssignments(...) дуудаж шинэчилнэ.
     */
    public AssignmentTableModel() {
        this.assignments = new ArrayList<>();
    }

    /**
     * JTable дээр харагдах өгөгдлийн эх үүсвэрийг шинэчилнэ.
     *
     * fireTableDataChanged() дуудах нь Swing-д
     * "хүснэгтийн өгөгдөл өөрчлөгдсөн" гэдгийг мэдэгдэж,
     * дэлгэцэн дээрх table-ийг шууд refresh хийхэд хэрэгтэй.
     */
    public void setAssignments(List<Assignment> assignments) {
        if (assignments == null) {
            this.assignments = new ArrayList<>();
        } else {
            this.assignments = assignments;
        }

        fireTableDataChanged();
    }

    /**
     * Тухайн мөрөнд харгалзах Assignment объектыг буцаана.
     *
     * Энэ method нь row selection хийсний дараа update / submit / grade /
     * delete зэрэг үйлдэл хийхэд сонгогдсон мөрийн арын domain объектыг авахад хэрэгтэй.
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
                if (assignment.getFeedback() != null && !assignment.getFeedback().isBlank()) {
                    return assignment.getFeedback();
                }
                return assignment.getDescription() == null ? "" : assignment.getDescription();
            case 5:
                return assignment.getSubmissionDate() == null ? "" : assignment.getSubmissionDate();
            case 6:
                return assignment.getStatus();
            case 7:
                return assignment.getScore() == null ? "" : assignment.getScore();
            default:
                return null;
        }
    }
}