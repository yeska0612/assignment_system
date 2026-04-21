<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>
</head>
<body>

    <h2>Login</h2>

    <%
        String error = request.getParameter("error");
        if ("true".equals(error)) {
    %>
        <p style="color:red;">Invalid username or password.</p>
    <%
        }
    %>

    <form action="<%= request.getContextPath() %>/login" method="POST">
        <div>
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" required>
        </div>

        <div style="margin-top:10px;">
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required>
        </div>

        <div style="margin-top:10px;">
            <button type="submit">Login</button>
        </div>
    </form>

    <p style="margin-top:15px;">
   		Teacher account: <strong>teacher</strong> / <strong>password123</strong><br>
    	Student account: <strong>student</strong> / <strong>password123</strong>
	</p>

</body>
</html>