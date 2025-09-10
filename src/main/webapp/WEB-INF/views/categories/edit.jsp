<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>카테고리 수정</title>
<link rel="stylesheet" href="<c:url value='/resources/css/common.css'/>">
</head>

<body>
	<jsp:include page="/WEB-INF/views/common/header.jsp" />
	<jsp:include page="/WEB-INF/views/common/aside.jsp" />

	<main id="main" data-ledger-id="${ledgerId}">
	<div class="container" style="padding: 16px;">
		<h2>카테고리 수정</h2>

		<c:if test="${not empty error}">
			<div class="alert error">${error}</div>
		</c:if>

		<form method="post" action="<c:url value='/ledgers/${ledgerId}/categories/${category.id}'/>" class="card" style="padding: 16px;">
			<div style="margin-bottom: 8px;">
				<label> 
					이름 <input type="text" name="name" value="${form.name}" required />
				</label>
			</div>
			<div class="muted" style="margin-bottom: 8px;">* 부모 변경은 별도 기능에서 다룹니다.</div>
			<div style="display: flex; gap: 8px;">
				<button type="submit" class="btn">저장</button>
				<a class="btn" href="<c:url value='/ledgers/${ledgerId}/categories'/>">취소</a>
			</div>
		</form>
	</div>
	</main>

	<script src="<c:url value='/resources/js/common.js'/>"></script>

</body>
</html>
