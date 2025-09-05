<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/aside.jsp" %>

<html>
<head>
  <title>${board.title}</title>
  <link rel="stylesheet" href="<c:url value='/resources/css/common.css'/>" />
  <script src="<c:url value='/resources/js/common.js'/>" defer></script>
</head>
<body>
  <main class="board-detail">
    <h2 class="breadcrumb">커뮤니티</h2>

    <!-- 게시글 본문 -->
    <article class="post-card">
      <header class="post-header">
        <h1>${board.title}</h1>
        <div class="author-info">
          <span class="author-name">${authorName}</span>
          <span class="post-date">
            <fmt:formatDate value="${board.createdAt}" pattern="yyyy-MM-dd"/>
          </span>
        </div>
      </header>

      <div class="post-content">
        <pre>${board.content}</pre>
      </div>

      <!-- 좋아요 + 댓글 수 -->
      <div class="post-actions">
        <form action="/boards/${board.id.value}/like" method="post" style="display:inline;">
          <input type="hidden" name="userId" value="${loginUser.id.value}" />
          <button type="submit" class="like-btn">❤️ 좋아요 ${likeCount}</button>
        </form>
        <span class="comment-count">💬 댓글 ${fn:length(comments)}</span>
      </div>
    </article>

    <!-- 댓글 목록 -->
    <section class="comments">
      <h3>댓글</h3>
      <ul class="comment-list">
        <c:forEach var="comment" items="${comments}">
          <li class="comment-item">
            <strong>${comment.userId.value}</strong>
            <span class="comment-date">
              <fmt:formatDate value="${comment.createdAt}" pattern="yyyy.MM.dd HH:mm"/>
            </span>
            <p>${comment.content}</p>
          </li>
        </c:forEach>
      </ul>

      <!-- 댓글 작성 폼 -->
      <form action="/boards/${board.id.value}/comments" method="post" class="comment-form">
        <input type="hidden" name="userId" value="${loginUser.id.value}" />
        <textarea name="content" placeholder="댓글을 입력하세요"></textarea>
        <button type="submit">등록</button>
      </form>
    </section>
  </main>
</body>
</html>
