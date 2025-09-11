<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>월별 수입/지출</title>
<link rel="stylesheet" href="<c:url value='/resources/css/common.css'/>">
<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
<style>
.toolbar {
	display: flex;
	gap: 8px;
	align-items: center;
	margin: 12px 0;
}
</style>
</head>
<body>
	<jsp:include page="/WEB-INF/views/common/header.jsp" />
	<jsp:include page="/WEB-INF/views/common/aside.jsp" />

	<main id="main" data-ledger-id="${ledgerId}">
	<div class="container" style="padding: 16px;">
		<h2>${year}년월별 수입/지출</h2>

		<!-- 집계 연도 선택 툴바 -->
		<div class="toolbar">
			<c:url var="prevUrl" value="/ledgers/${ledgerId}/insights/monthly">
				<c:param name="year" value="${prevYear}" />
			</c:url>
			<a class="btn" href="${prevUrl}">◀ 이전해</a>

			<form method="get" action="<c:url value='/ledgers/${ledgerId}/insights/monthly'/>" style="display: inline-flex; gap: 6px;">
				<input type="number" name="year" value="${year}" min="2000" max="2100" />
				<button class="btn" type="submit">이동</button>
			</form>

			<c:url var="nextUrl" value="/ledgers/${ledgerId}/insights/monthly">
				<c:param name="year" value="${nextYear}" />
			</c:url>
			<a class="btn" href="${nextUrl}">다음해 ▶</a> 
			
			<a class="btn" href="<c:url value='/ledgers/${ledgerId}/transaction?month=${year}-01'/>" style="margin-left: auto;">거래 목록</a>
		</div>

		<div class="card" style="padding: 8px; margin-bottom: 10px;">
			<strong>연간 합계</strong> — 
			<fmt:setLocale value="ko_KR"/>
			<span style="margin-left: 8px;">수입: <fmt:formatNumber value="${sumIncome}" type="currency" groupingUsed="true"/></span>
			<span style="margin-left: 12px;">지출: <fmt:formatNumber value="${sumExpense}" type="currency" groupingUsed="true"/></span>
		</div>

		<div id="chart" style="width: 100%; height: 480px;"></div>
	</div>
	</main>

	<script>
		// Google Charts 로드
		google.charts.load('current', { 'packages': ['corechart'] });
		google.charts.setOnLoadCallback(draw);
	
		function draw() {
			var rows = ${ chartDataJson }; // 서버에서 직렬화된 JSON (배열)
			var data = google.visualization.arrayToDataTable(rows);
	
			var options = {
				legend: { position: 'top' },
				height: 480,
				vAxis: { format: 'short' }, // 숫자 포맷(간단)
				bar: { groupWidth: '70%' },
				colors: ['#1a73e8', '#d93025'] // 수입/지출
			};
	
			var chart = new google.visualization.ColumnChart(document.getElementById('chart'));
			chart.draw(data, options);
		}
	
		// 반응형(선택)
		window.addEventListener('resize', function () { if (google && google.visualization) draw(); });
	</script>
</body>
</html>
