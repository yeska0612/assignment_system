package mn.edu.num.assignmentsystem.infrastructure.persistence;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; 
import java.util.ArrayList;
import java.util.List;

import mn.edu.num.assignmentsystem.core.domain.Assignment;
import mn.edu.num.assignmentsystem.core.domain.AssignmentStatus;
import mn.edu.num.assignmentsystem.core.ports.IAssignmentRepository;
import mn.edu.num.assignmentsystem.infrastructure.config.DatabaseConnection;

/**
 * Assignment repository-ийн JDBC implementation.
 * 
 * Энэ класс нь өгөгдлийг H2 database дээр хадгална.
 * Бүх SQL логик энэ class дотор байрлана.
 */
public class JdbcAssignmentRepository implements IAssignmentRepository {

    /** Database connection manager */
    private final DatabaseConnection databaseConnection;

    /**
     * Constructor.
     * Repository үүсэх үед хүснэгт байгаа эсэхийг шалгаж,
     * байхгүй бол автоматаар үүсгэнэ.
     */
    public JdbcAssignmentRepository() {
        this.databaseConnection = DatabaseConnection.getInstance();
        createTableIfNotExists();
    }

    /**
     * assignments хүснэгтийг байхгүй бол үүсгэнэ.
     */
    private void createTableIfNotExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS assignments (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    student_id VARCHAR(100) NOT NULL,
                    course_code VARCHAR(100) NOT NULL,
                    description VARCHAR(1000),
                    submission_date DATE,
                    status VARCHAR(50) NOT NULL,
                    score DOUBLE,
                    feedback VARCHAR(1000)
                )
                """;

        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(sql);

        } catch (SQLException e) {
            throw new RuntimeException("assignments хүснэгт үүсгэх үед алдаа гарлаа.", e);
        }
    }

    @Override
    public void save(Assignment assignment) {
        String sql = """
                INSERT INTO assignments
                (title, student_id, course_code, description, submission_date, status, score, feedback)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, assignment.getTitle());
            preparedStatement.setString(2, assignment.getStudentId());
            preparedStatement.setString(3, assignment.getCourseCode());
            preparedStatement.setString(4, assignment.getDescription());

            if (assignment.getSubmissionDate() != null) {
                preparedStatement.setDate(5, Date.valueOf(assignment.getSubmissionDate()));
            } else {
                preparedStatement.setDate(5, null);
            }

            preparedStatement.setString(6, assignment.getStatus().name());

            if (assignment.getScore() != null) {
                preparedStatement.setDouble(7, assignment.getScore());
            } else {
                preparedStatement.setNull(7, java.sql.Types.DOUBLE);
            }

            preparedStatement.setString(8, assignment.getFeedback());

            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    assignment.setId(generatedKeys.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Assignment хадгалах үед алдаа гарлаа.", e);
        }
    }

    @Override
    public List<Assignment> findAll() {
        String sql = "SELECT * FROM assignments ORDER BY id";
        List<Assignment> assignments = new ArrayList<>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                assignments.add(mapRowToAssignment(resultSet));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Assignment жагсаалт авах үед алдаа гарлаа.", e);
        }

        return assignments;
    }

    @Override
    public Assignment findById(Long id) {
        String sql = "SELECT * FROM assignments WHERE id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToAssignment(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("ID-аар assignment хайх үед алдаа гарлаа.", e);
        }

        return null;
    }

    @Override
    public void update(Assignment assignment) {
        String sql = """
                UPDATE assignments
                SET title = ?, student_id = ?, course_code = ?, description = ?,
                    submission_date = ?, status = ?, score = ?, feedback = ?
                WHERE id = ?
                """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, assignment.getTitle());
            preparedStatement.setString(2, assignment.getStudentId());
            preparedStatement.setString(3, assignment.getCourseCode());
            preparedStatement.setString(4, assignment.getDescription());

            if (assignment.getSubmissionDate() != null) {
                preparedStatement.setDate(5, Date.valueOf(assignment.getSubmissionDate()));
            } else {
                preparedStatement.setDate(5, null);
            }

            preparedStatement.setString(6, assignment.getStatus().name());

            if (assignment.getScore() != null) {
                preparedStatement.setDouble(7, assignment.getScore());
            } else {
                preparedStatement.setNull(7, java.sql.Types.DOUBLE);
            }

            preparedStatement.setString(8, assignment.getFeedback());
            preparedStatement.setLong(9, assignment.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Assignment шинэчлэх үед алдаа гарлаа.", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM assignments WHERE id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Assignment устгах үед алдаа гарлаа.", e);
        }
    }

    /**
     * ResultSet-ийн нэг мөрийг Assignment object болгон хөрвүүлнэ.
     */
    private Assignment mapRowToAssignment(ResultSet resultSet) throws SQLException {
        Assignment assignment = new Assignment();

        assignment.setId(resultSet.getLong("id"));
        assignment.setTitle(resultSet.getString("title"));
        assignment.setStudentId(resultSet.getString("student_id"));
        assignment.setCourseCode(resultSet.getString("course_code"));
        assignment.setDescription(resultSet.getString("description"));

        Date submissionDate = resultSet.getDate("submission_date");
        if (submissionDate != null) {
            assignment.setSubmissionDate(submissionDate.toLocalDate());
        }

        assignment.setStatus(AssignmentStatus.valueOf(resultSet.getString("status")));

        double score = resultSet.getDouble("score");
        if (!resultSet.wasNull()) {
            assignment.setScore(score);
        }

        assignment.setFeedback(resultSet.getString("feedback"));

        return assignment;
    }
}