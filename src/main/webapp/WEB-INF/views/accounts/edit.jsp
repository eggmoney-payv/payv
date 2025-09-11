<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>자산 수정</title>
<link rel="stylesheet" href="<c:url value='/resources/css/common.css'/>">
</head>

<body>
	<jsp:include page="/WEB-INF/views/common/header.jsp" />
	<jsp:include page="/WEB-INF/views/common/aside.jsp" />

	<main id="main" data-ledger-id="${ledgerId}">
	<div class="container">
		<h2>자산 수정</h2>

		<c:if test="${not empty error}">
			<div class="alert error">${error}</div>
		</c:if>

		<form method="post" action="<c:url value='/ledgers/${ledgerId}/accounts/${account.id}'/>" class="card" style="padding: 16px;">
			<div style="margin-bottom: 10px;">
				<label>이름 
					<input type="text" name="name" value="${form.name}" required />
				</label>
			</div>
			
			<%-- 
			<div style="margin-bottom: 10px;">
				<label>유형 
					<select name="type" required>
						<c:forEach var="t" items="${accountTypes}">
							<option value="${t.name()}" <c:if test="${t.name() == form.type}">selected</c:if>>
								${t.name()}
							</option>
						</c:forEach>
					</select>
				</label>
			</div>
			--%>

			<div class="muted" style="margin-bottom: 10px;">
				* 현재 잔액은 거래 기록으로 관리됩니다. 초기 잔액 변경이 필요하면 별도 기능으로 확장하세요.
			</div>

			<div style="display: flex; gap: 8px;">
				<button type="submit" class="btn">저장</button>
				<a class="btn" href="<c:url value='/ledgers/${ledgerId}/accounts'/>">취소</a>
			</div>
		</form>
	</div>
	</main>

	<script src="<c:url value='/resources/js/common.js'/>"></script>

</body>
</html>
