<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>자산 생성</title>
<link rel="stylesheet" href="<c:url value='/resources/css/common.css'/>">
</head>

<body>
	<jsp:include page="/WEB-INF/views/common/header.jsp" />
	<jsp:include page="/WEB-INF/views/common/aside.jsp" />

	<main id="main" data-ledger-id="${ledgerId}">	
	<div class="container" style="padding: 16px;">
		<h2>새 계좌</h2>

		<c:if test="${not empty error}">
			<div class="alert error">${error}</div>
		</c:if>

		<form method="post" action="<c:url value='/ledgers/${ledgerId}/accounts'/>" class="card" style="padding: 16px;">
			<div style="margin-bottom: 10px;">
				<label>이름 
					<input type="text" name="name" value="${form.name}" required />
				</label>
			</div>

			<div style="margin-bottom: 10px;">
				<label>유형
					<select name="type" required> 
						<c:forEach var="t" items="${accountTypes}">
							<option value="${t.name()}">${t.name()}</option>
						</c:forEach>
					</select>
				</label>
			</div>

			<div style="margin-bottom: 10px;">
				<label>초기 잔액(원)
					<input type="number" name="openingBalanceWon" min="0" step="100" value="${form.openingBalanceWon}" />
				</label>
				<div class="muted">* 비워두면 0원으로 생성됩니다.</div>
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
