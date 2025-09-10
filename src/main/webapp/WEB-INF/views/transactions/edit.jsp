<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>거래 내역 수정</title>
<link rel="stylesheet" href="<c:url value='/resources/css/common.css'/>">
</head>

<body>
	<jsp:include page="/WEB-INF/views/common/header.jsp" />
	<jsp:include page="/WEB-INF/views/common/aside.jsp" />

	<main id="main" data-ledger-id="${ledgerId}">
	<div class="container" style="padding: 16px;">
		<h2>거래 내역 수정</h2>

		<c:if test="${not empty error}">
			<div class="alert error">${error}</div>
		</c:if>

		<form method="post" action="<c:url value='/ledgers/${ledgerId}/transaction/${transaction.id}'/>" class="card" style="padding: 16px;">
			<div style="margin-bottom: 10px;">
				<label>일자 
					<input type="date" name="date" value="${form.date}" required />
				</label>
			</div>

			<div style="margin-bottom: 10px;">
				<label>유형 
					<select name="type" required>
						<c:forEach var="t" items="${transactionTypes}">
							<option value="${t.name()}" <c:if test="${t.name()==form.type}">selected</c:if>>
								${t.name()}
							</option>
						</c:forEach>
					</select>
				</label>
			</div>

			<div style="margin-bottom: 10px;">
				<label>금액(원) 
					<input type="number" name="amount" min="0" step="100" value="${form.amount}" required />
				</label>
			</div>

			<div style="margin-bottom: 10px;">
				<label>계좌 
					<select name="accountId" required>
						<c:forEach var="a" items="${accounts}">
							<option value="${a.id}" <c:if test="${a.id == form.accountId}">selected</c:if>>
								${a.name}
							</option>
						</c:forEach>
					</select>
				</label>
			</div>

			<div style="margin-bottom: 10px;">
				<label>카테고리 
					<select name="categoryId" required>
						<c:forEach var="cOpt" items="${categoryOptions}">
							<option value="${cOpt.id}" <c:if test="${cOpt.id == form.categoryId}">selected</c:if>>
								${cOpt.label}
							</option>
						</c:forEach>
					</select>
				</label>
			</div>

			<div style="margin-bottom: 10px;">
				<label>메모 
					<input type="text" name="memo" value="${form.memo}" />
				</label>
			</div>

			<div style="display: flex; gap: 8px;">
				<button type="submit" class="btn">저장</button>
				<a class="btn" href="<c:url value='/ledgers/${ledgerId}/transaction?month=${form.date.substring(0,7)}'/>">취소</a>
			</div>
		</form>
	</div>
	</main>

	<script src="<c:url value='/resources/js/common.js'/>"></script>

</body>
</html>
