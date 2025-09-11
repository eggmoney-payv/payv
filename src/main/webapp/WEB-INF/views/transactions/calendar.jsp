<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>월간 달력</title>
<link rel="stylesheet" href="<c:url value='/resources/css/common.css'/>">
<style>
.cal {
	display: grid;
	grid-template-columns: repeat(7, 1fr);
	gap: 8px;
}

.cal .dow {
	font-weight: bold;
	text-align: center;
	padding: 6px 0;
	border-bottom: 1px solid #ddd;
}

.cal .cell {
	border: 1px solid #e4e4e4;
	border-radius: 8px;
	min-height: 120px;
	padding: 8px;
	position: relative;
	background: #fff;
}

.cal .out {
	background: #fafafa;
	color: #999;
}

.cal .date {
	font-size: 12px;
	position: absolute;
	top: 6px;
	right: 8px;
}

.cal .sum {
	font-size: 12px;
	margin-top: 18px;
	line-height: 1.3;
}

.cal .cell { cursor: pointer; }

.cal .txns {
	margin-top: 6px;
	font-size: 12px;
	list-style: none;
	padding-left: 0;
}

.cal .txns li {
	display: flex;
	justify-content: space-between;
	gap: 6px;
}

.cal .amt-in {
	color: #0a7;
}

.cal .amt-out {
	color: #c33;
}

.toolbar {
	display: flex;
	align-items: center;
	gap: 8px;
	margin: 12px 0;
}
</style>
</head>
<body>
	<jsp:include page="/WEB-INF/views/common/header.jsp" />
	<jsp:include page="/WEB-INF/views/common/aside.jsp" />

	<main id="main" data-ledger-id="${ledgerId}">
	<div class="container" style="padding: 16px;">
		<h2>월간 달력 (${month})</h2>

		<div class="toolbar">
			<c:url var="prevUrl"
				value="/ledgers/${ledgerId}/transaction/calendar">
				<c:param name="month" value="${prevMonth}" />
			</c:url>
			<a class="btn" href="${prevUrl}">◀ 이전달</a>

			<form method="get"
				action="<c:url value='/ledgers/${ledgerId}/transaction/calendar'/>"
				style="display: inline-flex; gap: 6px;">
				<input type="month" name="month" value="${month}" />
				<button type="submit" class="btn">이동</button>
			</form>

			<c:url var="nextUrl"
				value="/ledgers/${ledgerId}/transaction/calendar">
				<c:param name="month" value="${nextMonth}" />
			</c:url>
			<a class="btn" href="${nextUrl}">다음달 ▶</a> <a class="btn"
				href="<c:url value='/ledgers/${ledgerId}/transaction?month=${month}'/>"
				style="margin-left: auto;">표 목록 보기</a>
		</div>

		<div class="card" style="padding: 8px; margin-bottom: 10px;">
			<strong>이 달 합계</strong> — <span class="amt-in">수입:
				+${monthIncome}</span> <span style="margin-left: 12px;" class="amt-out">지출:
				-${monthExpense}</span>
		</div>

		<!-- 요일 헤더 -->
		<div class="cal">
			<div class="dow">일</div>
			<div class="dow">월</div>
			<div class="dow">화</div>
			<div class="dow">수</div>
			<div class="dow">목</div>
			<div class="dow">금</div>
			<div class="dow">토</div>

			<!-- 주/일 렌더링 -->
			<c:forEach var="w" items="${weeks}">
				<c:forEach var="d" items="${w.days}">
					<!-- data-date 속성 추가, +추가/목록 링크는 삭제 -->
					<div class="cell <c:if test='${!d.inMonth}'>out</c:if>"
						data-date="${d.date}">
						<div class="date">${fn:substring(d.date, 8, 10)}</div>

						<div class="sum">
							<c:if test="${not empty d.income}">
								<div class="amt-in">+${d.income.substring(1)}</div>
							</c:if>
							<c:if test="${not empty d.expense}">
								<div class="amt-out">-${d.expense.substring(1)}</div>
							</c:if>
						</div>

						<ul class="txns">
							<c:forEach var="t" items="${d.txns}">
								<li><span>${t.categoryName}</span> <span
									class="<c:out value='${t.type == "INCOME" ? "amt-in" : "amt-out"}'/>">${t.amount}</span>
								</li>
							</c:forEach>
						</ul>
					</div>
				</c:forEach>
			</c:forEach>
		</div>
	</div>
	</main>

	<script src="<c:url value='/resources/js/common.js'/>"></script>
	<script>
	  // 새 거래 등록 페이지 베이스 URL (컨텍스트 경로 포함)
	  var newBaseUrl = '<c:url value="/ledgers/${ledgerId}/transaction/new"/>'; // ?date=YYYY-MM-DD
	
	  // 캘린더 셀 클릭 핸들러 (이벤트 위임)
	  document.addEventListener('click', function(e){
	    var cell = e.target.closest('.cal .cell');
	    if (!cell) return;
	
	    var date = cell.getAttribute('data-date');
	    if (!date) return;
	
	    if (confirm('해당 날짜에 거래 내역을 추가하시겠습니까?')) {
	      location.href = newBaseUrl + '?date=' + encodeURIComponent(date);
	    }
	  });
	</script>
		
</body>
</html>
