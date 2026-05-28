<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>System Error</title>
</head>
<body>
    <h2>Something went wrong</h2>
    <p>The system encountered an unexpected error.</p>

    <hr>

    <h3>Debug Message</h3>
    <p>
        <%= request.getAttribute("jakarta.servlet.error.message") %>
    </p>

    <h3>Exception</h3>
    <pre>
<%= request.getAttribute("jakarta.servlet.error.exception") %>
    </pre>

    <p><a href="<%= request.getContextPath() %>/login">Go to Login</a></p>
</body>
</html>