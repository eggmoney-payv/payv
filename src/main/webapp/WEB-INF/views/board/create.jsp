<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>게시글 작성</title></head>
<body>
    <h2>새 글 작성</h2>
    <form action="${pageContext.request.contextPath}/boards" method="post">
        <p>작성자ID: <input type="text" name="userId" required></p>
        <p>제목: <input type="text" name="title" required></p>
        <p>내용: <textarea name="content" rows="5" cols="40"></textarea></p>
        <button type="submit">작성</button>
    </form>
    <a href="${pageContext.request.contextPath}/boards">목록으로</a>
</body>
</html>
