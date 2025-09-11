<%-- <%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>카테고리별 지출</title>
<link rel="stylesheet" href="<c:url value='/resources/css/common.css'/>">
<script type="text/javascript"
	src="https://www.gstatic.com/charts/loader.js"></script>
<style>
.toolbar {
	display: flex;
	gap: 8px;
	align-items: center;
	margin: 12px 0;
}

#no-data {
	padding: 24px;
	text-align: center;
	color: #777;
	border: 1px dashed #ccc;
	border-radius: 8px;
}
</style>
</head>
<body>
	<jsp:include page="/WEB-INF/views/common/header.jsp" />
	<jsp:include page="/WEB-INF/views/common/aside.jsp" />

	<main id="main" data-ledger-id="${ledgerId}">
	<div class="container" style="padding: 16px;">
		<h2>${month}카테고리별 지출</h2>

		<div class="toolbar">
			<form method="get"
				action="<c:url value='/ledgers/${ledgerId}/insights/categories'/>"
				style="display: inline-flex; gap: 6px;">
				<input type="month" name="month" value="${month}" />
				<button class="btn" type="submit">조회</button>
			</form>
			<a class="btn"
				href="<c:url value='/ledgers/${ledgerId}/transaction?month=${month}'/>"
				style="margin-left: auto;">거래 목록</a>
		</div>

		<div class="card" style="padding: 8px; margin-bottom: 10px;">
			<strong>총 지출</strong> — <span>-${totalExpense}</span>
		</div>

		<div id="no-data" style="display: none;">해당 월의 지출 데이터가 없습니다.</div>
		<div id="chart" style="width: 100%; height: 480px;"></div>
	</div>
	</main>

	<script>
    google.charts.load('current', {'packages':['corechart']});
    google.charts.setOnLoadCallback(draw);

    function draw(){
      var rows = ${pieDataJson}; // [ ["카테고리","지출"], ["식비",123], ... ]
      if (!rows || rows.length <= 1) {
        document.getElementById('no-data').style.display = '';
        document.getElementById('chart').style.display = 'none';
        return;
      }
      var data = google.visualization.arrayToDataTable(rows);
      var options = {
        pieHole: 0.5,
        legend: { position: 'right' },
        chartArea: { left: 20, top: 10, width: '90%', height: '90%' },
        height: 480
      };
      var chart = new google.visualization.PieChart(document.getElementById('chart'));
      chart.draw(data, options);
    }
    window.addEventListener('resize', function(){ if (google && google.visualization) draw(); });
  </script>
</body>
</html>
 --%>