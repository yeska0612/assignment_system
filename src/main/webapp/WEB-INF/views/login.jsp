<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/login.css">
</head>
<body>

<div class="login-shell">
    <div class="login-card">
        <h2 class="login-title">Assignment System</h2>

        <%
            String error = request.getParameter("error");
            if ("true".equals(error)) {
        %>
            <div class="alert alert-error">
                Invalid username or password.
            </div>
        <%
            }
        %>

        <form action="<%= request.getContextPath() %>/login" method="POST">
            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" required>
            </div>

            <div class="form-group" style="margin-top:14px;">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required>
            </div>

            <div style="margin-top:18px;">
                <button type="submit" class="btn btn-primary" style="width:100%;">Login</button>
            </div>
        </form>

        <div class="credentials-box">
            <strong>Accounts</strong><br><br>
            Teacher: <strong>teacher</strong> / <strong>password12345</strong><br>
            Student: <strong>student</strong> / <strong>password1234</strong>
        </div>
    </div>
</div>

</body>
</html>