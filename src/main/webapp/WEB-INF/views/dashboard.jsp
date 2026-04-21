<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<html>
<head>
    <title>Dashboard</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/dashboard.css">
</head>
<body>
<div class="page-container">
    <div class="card">
        <div class="top-bar">
            <div>
                <h2 class="page-title"><c:out value="${dashboardTitle}" /></h2>
            </div>

            <div style="display:flex; align-items:center; gap:12px;">
                <c:if test="${role == 'TEACHER'}">
                    <span class="role-badge role-teacher">TEACHER</span>
                </c:if>

                <c:if test="${role == 'STUDENT'}">
                    <span class="role-badge role-student">STUDENT</span>
                </c:if>

                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/logout">Logout</a>
            </div>
        </div>

        <!--
            Teacher dashboard дээр teacher юу хийж болохыг товч тайлбарлаж байна.
        -->
        <c:if test="${role == 'TEACHER'}">
            <div class="info-box">
                You can create assignments, delete them, and grade submitted work.
            </div>

            <hr class="divider">

            <h3 class="section-title">Create New Assignment</h3>

            <!--
                Teacher assignment үүсгэх form.
                Description талбар нь student dashboard дээр харагдах даалгаврын тайлбар болно.
            -->
            <form action="${pageContext.request.contextPath}/assignments" method="POST">
                <div class="form-grid">
                    <div class="form-group">
                        <label for="title">Title</label>
                        <input type="text" id="title" name="title" required>
                    </div>

                    <div class="form-group">
                        <label for="studentId">Student ID</label>
                        <input type="text" id="studentId" name="studentId" required>
                    </div>

                    <div class="form-group">
                        <label for="courseCode">Course Code</label>
                        <input type="text" id="courseCode" name="courseCode" required>
                    </div>

                    <div class="form-group">
                        <label for="description">Description</label>
                        <input type="text" id="description" name="description">
                    </div>
                </div>

                <button type="submit" class="btn btn-primary">Save Assignment</button>
            </form>
        </c:if>

        <!--
            Student dashboard дээр өөрийн student ID-г харуулна.
        -->
        <c:if test="${role == 'STUDENT'}">
            <div class="info-box">
                Viewing assignments for student:
                <strong><c:out value="${studentId}" /></strong>
            </div>
        </c:if>

        <hr class="divider">

        <h3 class="section-title">Assignments</h3>

        <c:if test="${empty assignmentList}">
            <div class="empty-state">
                No assignments found for this account.
            </div>
        </c:if>

        <c:if test="${not empty assignmentList}">
            <div class="table-wrapper">
                <table>
                    <tr>
                        <th>ID</th>
                        <th>Title</th>

                        <!--
                            Teacher dashboard дээр Student ID багана харагдана.
                            Student dashboard дээр Student ID-ийн оронд Description багана харагдана.
                        -->
                        <c:choose>
                            <c:when test="${role == 'TEACHER'}">
                                <th>Student ID</th>
                            </c:when>
                            <c:otherwise>
                                <th>Description</th>
                            </c:otherwise>
                        </c:choose>

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

                            <!--
                                Teacher assignment-ийг ямар student-д оноосныг харна.
                                Student харин тухайн даалгаврын description-ийг харна.
                            -->
                            <c:choose>
                                <c:when test="${role == 'TEACHER'}">
                                    <td><c:out value="${a.studentId}" /></td>
                                </c:when>
                                <c:otherwise>
                                    <td><c:out value="${a.description}" /></td>
                                </c:otherwise>
                            </c:choose>

                            <td><c:out value="${a.courseCode}" /></td>
                            <td><c:out value="${a.status}" /></td>
                            <td><c:out value="${a.score}" /></td>
                            <td><c:out value="${a.feedback}" /></td>
                            <td>
                                <!--
                                    Teacher delete хийх эрхтэй.
                                -->
                                <c:if test="${role == 'TEACHER'}">
                                    <form action="${pageContext.request.contextPath}/delete-assignment"
                                          method="POST"
                                          class="inline-form">
                                        <input type="hidden" name="assignmentId" value="${a.id}">
                                        <button type="submit"
                                                class="btn btn-danger"
                                                onclick="return confirm('Delete this assignment?');">
                                            Delete
                                        </button>
                                    </form>
                                </c:if>

                                <!--
                                    Teacher submitted assignment-ийг grade хийж чадна.
                                -->
                                <c:if test="${role == 'TEACHER' && a.status == 'SUBMITTED'}">
                                    <form action="${pageContext.request.contextPath}/grade-assignment"
                                          method="POST"
                                          class="inline-form"
                                          style="margin-top:8px;">
                                        <input type="hidden" name="assignmentId" value="${a.id}">
                                        <input type="number" step="0.1" name="score" placeholder="Score" required>
                                        <input type="text" name="feedback" placeholder="Feedback">
                                        <button type="submit" class="btn btn-success">Grade</button>
                                    </form>
                                </c:if>

                                <!--
                                    Student зөвхөн DRAFT төлөвтэй assignment-ийг submit хийж чадна.
                                -->
                                <c:if test="${role == 'STUDENT' && a.status == 'DRAFT'}">
                                    <form action="${pageContext.request.contextPath}/submit-assignment"
                                          method="POST"
                                          class="inline-form">
                                        <input type="hidden" name="assignmentId" value="${a.id}">
                                        <button type="submit" class="btn btn-primary">
                                            Submit
                                        </button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
        </c:if>
    </div>
</div>

</body>
</html>