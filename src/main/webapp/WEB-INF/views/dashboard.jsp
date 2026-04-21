<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<html>
<head>
    <title>Dashboard</title>
</head>
<body>

    <h2><c:out value="${dashboardTitle}" /></h2>

    <p><c:out value="${welcomeMessage}" /></p>

    <p>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </p>

    <hr>

    <!--
        Teacher хэрэглэгч assignment үүсгэх form харах эрхтэй.
        Student-д энэ form харагдахгүй.
    -->
    <c:if test="${role == 'TEACHER'}">
        <h3>Create New Assignment</h3>

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
    </c:if>

    <!--
        Student dashboard дээр тухайн хэрэглэгчийн studentId-г харуулж болно.
        Ингэснээр өөрийн assignment-ууд filter хийгдэж байгааг ойлгомжтой болгоно.
    -->
    <c:if test="${role == 'STUDENT'}">
        <p>
            Viewing assignments for student:
            <strong><c:out value="${studentId}" /></strong>
        </p>
        <hr>
    </c:if>

    <h3>Assignments</h3>

    <table border="1">
        <tr>
            <th>ID</th>
            <th>Title</th>
            <th>Student ID</th>
            <th>Course</th>
            <th>Status</th>
            <th>Score</th>
            <th>Feedback</th>
            <th>Action</th>
        </tr>

        <c:forEach var="a" items="${assignmentList}">
            <tr>
                <td><c:out value="${a.id}" /></td>
                <td><c:out value="${a.title}" /></td>
                <td><c:out value="${a.studentId}" /></td>
                <td><c:out value="${a.courseCode}" /></td>
                <td><c:out value="${a.status}" /></td>
                <td><c:out value="${a.score}" /></td>
                <td><c:out value="${a.feedback}" /></td>
                <td>
    <!--
        Teacher хэрэглэгч delete хийх эрхтэй.
    -->
    <c:if test="${role == 'TEACHER'}">
        <form action="${pageContext.request.contextPath}/delete-assignment"
              method="POST"
              style="display:inline;">
            <input type="hidden" name="assignmentId" value="${a.id}">
            <button type="submit"
                    onclick="return confirm('Delete this assignment?');">
                Delete
            </button>
        </form>
    </c:if>

    <!--
        Teacher submitted assignment-ийг grade хийж чадна.
        Зөвхөн SUBMITTED төлөвтэй мөр дээр grade form харагдана.
    -->
    <c:if test="${role == 'TEACHER' && a.status == 'SUBMITTED'}">
        <form action="${pageContext.request.contextPath}/grade-assignment"
              method="POST"
              style="display:inline; margin-left:8px;">
            <input type="hidden" name="assignmentId" value="${a.id}">
            <input type="number" step="0.1" name="score" placeholder="Score" required>
            <input type="text" name="feedback" placeholder="Feedback">
            <button type="submit">Grade</button>
        </form>
    </c:if>

    <!--
        Student хэрэглэгч зөвхөн өөрийн DRAFT төлөвтэй assignment-ийг submit хийж чадна.
    -->
    <c:if test="${role == 'STUDENT' && a.status == 'DRAFT'}">
        <form action="${pageContext.request.contextPath}/submit-assignment"
              method="POST"
              style="display:inline;">
            <input type="hidden" name="assignmentId" value="${a.id}">
            <button type="submit">
                Submit
            </button>
        </form>
    </c:if>
</td>
            </tr>
        </c:forEach>
    </table>

</body>
</html>