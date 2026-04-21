<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="mn.edu.num.assignmentsystem.core.domain.Assignment" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<html>
<head>
    <title>Dashboard</title>
</head>
<body>

    <h2>Dashboard</h2>

    <p><c:out value="${welcomeMessage}" /></p>

    <p>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </p>

    <hr>

    <h3>All Assignments</h3>

    <form action="${pageContext.request.contextPath}/assignments" method="POST">
        <label for="title">Title:</label>
        <input type="text" id="title" name="title" required>

        <label for="studentId">Student ID:</label>
        <input type="text" id="studentId" name="studentId" required>

        <label for="courseCode">Course Code:</label>
        <input type="text" id="courseCode" name="courseCode" required>

        <label for="description">Description:</label>
        <input type="text" id="description" name="description">

        <button type="submit">Save</button>
    </form>

    <hr>

    <table border="1">
        <tr>
            <th>ID</th>
            <th>Title</th>
            <th>Student ID</th>
            <th>Course</th>
            <th>Status</th>
            <th>Action</th>
        </tr>

        <c:forEach var="a" items="${assignmentList}">
            <tr>
                <td><c:out value="${a.id}" /></td>
                <td><c:out value="${a.title}" /></td>
                <td><c:out value="${a.studentId}" /></td>
                <td><c:out value="${a.courseCode}" /></td>
                <td><c:out value="${a.status}" /></td>
                <td>
                    <form action="${pageContext.request.contextPath}/delete-assignment"
                          method="POST"
                          style="display:inline;">
                        <input type="hidden" name="assignmentId" value="${a.id}">
                        <button type="submit"
                                onclick="return confirm('Delete this assignment?');">
                            Delete
                        </button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>

</body>
</html>