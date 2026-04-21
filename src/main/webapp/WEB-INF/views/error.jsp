<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Application Error</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/error.css">
</head>
<body>

<div class="page-container">
    <div class="card" style="max-width:700px; margin:80px auto;">
        <h2 class="page-title">Something went wrong</h2>
        <p class="subtitle">
            The application encountered an unexpected error.
            Please try again later.
        </p>

        <hr class="divider">

        <a class="btn btn-primary" href="${pageContext.request.contextPath}/login">
            Back to Login
        </a>
    </div>
</div>

</body>
</html>