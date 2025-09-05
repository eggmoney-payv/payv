<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head><title>게시글 목록</title></head>
<body>
    <h2>게시판 목록</h2>
    <a href="${pageContext.request.contextPath}/boards/new">새 글쓰기</a>
    <table border="1" cellpadding="5" cellspacing="0">
        <thead>
            <tr>
                <th>글번호</th>
                <th>제목</th>
                <th>작성자ID</th>
                <th>생성일</th>
            </tr>
        </thead>
        <tbody>
        <c:forEach var="board" items="${boardList}">
            <tr>
                <td>${board.id.value}</td>
                <td>${board.title}</td>
                <td>${board.userId.value}</td>
                <td>${board.createdAt}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</body>
</html>
