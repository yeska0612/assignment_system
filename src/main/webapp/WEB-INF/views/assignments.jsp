<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="mn.edu.num.assignmentsystem.core.domain.Assignment" %>

<html>
<head>
    <title>Assignment Web Portal</title>
</head>
<body>

<h2>All Assignments</h2>

<table border="1">
    <tr>
        <th>ID</th>
        <th>Title</th>
        <th>Student ID</th>
        <th>Course</th>
        <th>Status</th>
    </tr>

<%
    /*
     * Servlet-аас ирсэн өгөгдлийг авна
     */
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
    </tr>

<%
        }
    }
%>

</table>

</body>
</html>