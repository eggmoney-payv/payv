<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>거래 내역 목록</title>
<link rel="stylesheet" href="<c:url value='/resources/css/common.css'/>">
</head>

<body>
	<jsp:include page="/WEB-INF/views/common/header.jsp" />
	<jsp:include page="/WEB-INF/views/common/aside.jsp" />

	<main id="main" data-ledger-id="${ledgerId}">
	<div class="container">
		<h2>거래 내역</h2>

		<c:if test="${not empty message}">
			<div class="alert success">${message}</div>
		</c:if>
		<c:if test="${not empty error}">
			<div class="alert error">${error}</div>
		</c:if>

		<a class="btn" href="<c:url value='/ledgers/${ledgerId}/transaction/new'/>" style="margin-left: 8px;">+ 거래 내역 추가</a>
		<a class="btn" href="<c:url value='/ledgers/${ledgerId}'/>" style="margin-left: 8px;">← 가계부 홈</a>
			
		<form method="get" action="<c:url value='/ledgers/${ledgerId}/transaction'/>" style="margin-bottom: 12px;">
			<label>월 선택: 
				<input type="month" name="month" value="${month}" />
			</label>
			<button class="btn" type="submit">조회</button>
		</form>

		<table class="table" style="width: 100%; border-collapse: collapse;">
			<thead>
				<tr>
					<th style="text-align: left; padding: 8px; border-bottom: 1px solid #ccc;">일자</th>
					<th style="text-align: left; padding: 8px; border-bottom: 1px solid #ccc;">계좌</th>
					<th style="text-align: left; padding: 8px; border-bottom: 1px solid #ccc;">카테고리</th>
					<th style="text-align: center; padding: 8px; border-bottom: 1px solid #ccc;">유형</th>
					<th style="text-align: right; padding: 8px; border-bottom: 1px solid #ccc;">금액</th>
					<th style="text-align: left; padding: 8px; border-bottom: 1px solid #ccc;">메모</th>
					<th style="text-align: center; padding: 8px; border-bottom: 1px solid #ccc;">작업</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="t" items="${transaction}">
					<tr>
						<td style="padding: 8px;">${t.date}</td>
						<td style="padding: 8px;">${t.accountName}</td>
						<td style="padding: 8px;">${t.categoryName}</td>
						<td style="padding: 8px; text-align: center;">${t.type}</td>
						<td style="padding: 8px; text-align: right;">${t.amount}</td>
						<td style="padding: 8px;">${t.memo}</td>
						<td style="padding: 8px; text-align: center;">
							<a class="btn" href="<c:url value='/ledgers/${ledgerId}/transaction/${t.id}/edit'/>">수정</a>
							<button class="btn danger js-del" data-id="${t.id}" style="margin-left: 8px;">삭제</button>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	</main>

	<script src="<c:url value='/resources/js/common.js'/>"></script>
	<script>
		// 삭제만 비동기(JSON)
		document.addEventListener('click', async (e)=>{
			const btn = e.target.closest('.js-del');
			if(!btn) return;
		
			const id = btn.getAttribute('data-id');
			if(!id) return;
		
			if(!confirm('정말 삭제하시겠습니까?')) return;
		
			const url = '<c:url value="/ledgers/${ledgerId}/transaction/"/>' + id;
		
			try {
			  	const res = await fetch(url, { method: 'DELETE' });
			    const json = await res.json();
			    if(!json.ok){
			      alert(json.message || '삭제 실패');
			      return;
			    }
			    location.reload();
		  	} catch (err) {
			    alert('네트워크 오류로 삭제하지 못했습니다.');
		  	}
		});
	</script>

</body>
</html>
