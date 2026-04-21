<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="mn.edu.num.assignmentsystem.core.domain.Assignment" %>

<html>
<head>
    <title>Assignment Web Portal</title>
</head>
<body>

    <h2>Add New Assignment</h2>

    <!--
        Энэ form нь шинэ assignment үүсгэхэд ашиглагдана.
        action="assignments" гэдэг нь AssignmentServlet-ийн doPost() руу POST явуулна.
    -->
    <form action="assignments" method="POST">
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
    
    <h2>All Assignments</h2>
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
                <!--
                    Delete-г GET линкээр хийхгүй.
                    Hidden input ашигласан POST form-оор ID дамжуулна.
                -->
                <form action="delete-assignment" method="POST" style="display:inline;">
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