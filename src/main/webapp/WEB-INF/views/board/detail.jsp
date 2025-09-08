<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/aside.jsp" %>

<html>
<head>
  <title>${board.title}</title>
  <link rel="stylesheet" href="<c:url value='/resources/css/common.css'/>" />
  <link rel="stylesheet" href="<c:url value='/resources/css/board.css'/>" />
  <script src="<c:url value='/resources/js/common.js'/>" defer></script>

</head>
<body>
  <main class="board-detail">
    <h2 class="board-title">커뮤니티</h2>

    <article class="post-card">
      <!-- (1) 제목 -->
      <h1 class="post-title">${board.title}</h1>

      <!-- (2) 메타: 왼쪽 작성자/날짜 · 오른쪽 수정/삭제 -->
      <div class="post-meta-bar">
        <div class="post-meta-left">
          <span class="author-name">${board.userId.value}</span>
          <span class="meta-sep"></span>
          <span class="post-date">${boardCreatedAtText}</span>
        </div>

        <div class="post-actions-right">
          <c:if test="${loginUser != null && loginUser.id.value == board.userId.value}">
            <a class="btn" href="<c:url value='/boards/${board.id.value}/edit'/>">수정</a>
            <!-- 삭제는 POST + _method=DELETE (HiddenHttpMethodFilter 사용 시) -->
            <form action="<c:url value='/boards/${board.id.value}'/>" method="post" style="display:inline;">
              <input type="hidden" name="_method" value="DELETE"/>
              <button type="submit" class="btn btn-danger">삭제</button>
            </form>
          </c:if>
        </div>
      </div>

      <!-- (3) 본문 -->
      <div class="post-content">
        ${fn:escapeXml(board.content)}
      </div>

<%--       <!-- (4) 좋아요 / 댓글수 -->
      <div class="post-stats">
        <form action="<c:url value='/boards/${board.id.value}/like'/>" method="post" style="display:inline;">
          <input type="hidden" name="userId" value="${loginUser.id.value}" />
          <button type="submit" class="like-btn">❤️ 좋아요 ${likeCount}</button>
        </form>
        <span>💬 댓글 ${fn:length(comments)}</span>
      </div> --%>
      
      <!-- (4) 좋아요 / 댓글수 -->
<div class="post-stats" 
     data-board-id="${board.id.value}" 
     data-like-count="${likeCount}">
  <!-- 하트(좋아요)만 버튼 -->
  <button type="button" class="like-toggle" aria-pressed="false" title="좋아요">
    <!-- 하트 SVG 아이콘 -->
    <svg class="icon-heart" viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
      <path d="M12 21s-6.716-4.21-9.193-7.32C1.24 12.07 1 10.94 1 9.75 1 7.13 3.14 5 5.75 5c1.54 0 2.97.73 3.89 1.88A5.02 5.02 0 0 1 13.5 5C16.09 5 18.25 7.13 18.25 9.75c0 1.19-.24 2.32-1.807 3.93C18.716 16.79 12 21 12 21z" />
    </svg>
    <span class="like-count">${likeCount}</span>
  </button>

  <!-- 점 구분자 -->
  <span class="meta-sep"></span>

  <!-- 댓글: 아이콘 + 숫자 (버튼 아님) -->
  <div class="stat">
    <svg class="icon-comment" viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
      <path d="M21 6a3 3 0 0 0-3-3H6A3 3 0 0 0 3 6v8a3 3 0 0 0 3 3h8l4 4v-4a3 3 0 0 0 3-3V6z"/>
    </svg>
    <span class="comment-count">${fn:length(comments)}</span>
  </div>
</div>
      

      <!-- (5) 댓글 목록 -->
      <section class="comments">
        <h3>댓글</h3>
        <ul class="comment-list">
          <c:forEach var="comment" items="${comments}">
            <li class="comment-item">
              <div class="comment-head">
                <strong>${comment.userId.value}</strong>
                <span class="meta-sep"></span>
                <!-- comment.createdAt이 LocalDateTime이면 fmt가 터질 수 있으니 안전하게 처리 -->
                <c:choose>
                  <c:when test="${not empty comment.createdAtText}">
                    <span>${comment.createdAtText}</span>
                  </c:when>
                  <c:otherwise>
                    <fmt:formatDate value="${comment.createdAt}" pattern="yyyy.MM.dd HH:mm"/>
                  </c:otherwise>
                </c:choose>
              </div>
              <p class="comment-content">${fn:escapeXml(comment.content)}</p>
            </li>
          </c:forEach>
        </ul>

        <!-- 댓글 작성 -->
        <form action="<c:url value='/boards/${board.id.value}/comments'/>" method="post" class="comment-form">
          <!-- <input type="hidden" name="userId" value="anonymous" /> -->
          <input type="hidden" name="userId" value="anonymous" />
          <textarea name="content" placeholder="댓글을 입력하세요"></textarea>
          <button type="submit">등록</button>
        </form>
      </section>
    </article>
  </main>
</body>
</html>

