<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/aside.jsp" %>
<html>
<head>
  <title>게시판</title>
  <link rel="stylesheet" href="<c:url value='/resources/css/common.css'/>" />
  <script src="<c:url value='/resources/js/common.js'/>" defer></script>
</head>
<body>
  <main>
    <h1>게시판</h1>
    <table class="board-table">
      <thead>
        <tr>
          <th>제목</th>
          <th>작성자</th>
          <th>작성일</th>
          <th>조회수</th>
          <th>좋아요</th>
        </tr>
      </thead>
      <tbody>
        <%-- 게시글 리스트 반복 (예: request attribute에 List<Board> 들어있다고 가정) --%>
        <c:forEach var="board" items="${boards}">
          <tr>
            <td><a href="<c:url value='/boards/${board.id.value}'/>">${board.title}</a></td>
            <td>${board.userId.value}</td>
            <td><fmt:formatDate value="${board.createdAt}" pattern="yyyy.MM.dd"/></td>
            <td>${board.viewCount}</td>
            <td>0</td> <%-- 좋아요 수 있으면 연동 --%>
          </tr>
        </c:forEach>
      </tbody>
    </table>
  </main>
</body>
</html>
