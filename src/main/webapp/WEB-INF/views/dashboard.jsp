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

    <p><%= request.getAttribute("welcomeMessage") %></p>

    <!--
        Logout link нь зөвхөн login хийсэн хэрэглэгч dashboard дээр орж ирсэн үед харагдана.
        /logout руу орсноор session invalidate хийгдэнэ.
    -->
    <p>
        <a href="<%= request.getContextPath() %>/logout">Logout</a>
    </p>

    <hr>

    <h3>All Assignments</h3>

    <!--
        Assignment нэмэх form.
        Dashboard хамгаалагдсан route тул login хийсэн хэрэглэгч л create хийх боломжтой.
    -->
    <form action="<%= request.getContextPath() %>/assignments" method="POST">

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

        <%
            List<Assignment> list =
                    (List<Assignment>) request.getAttribute("assignmentList");

            if (list != null) {
                for (Assignment a : list) {
        %>
        <tr>
            <td><%= a.getId() %></td>
            <td><%= a.getTitle() %></td>
            <td><%= a.getStudentId() %></td>
            <td><%= a.getCourseCode() %></td>
            <td><%= a.getStatus() %></td>
            <td>
                <form action="<%= request.getContextPath() %>/delete-assignment"
                      method="POST"
                      style="display:inline;">
                    <input type="hidden" name="assignmentId" value="<%= a.getId() %>">
                    <button type="submit"
                            onclick="return confirm('Delete this assignment?');">
                        Delete
                    </button>
                </form>
            </td>
        </tr>
        <%
                }
            }
        %>
    </table>

</body>
</html>