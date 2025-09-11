<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>대시보드 - PayV</title>
<style>
body {
	font-family: 'Malgun Gothic', '맑은고딕', sans-serif;
	background-color: #F5F3E7;
	margin: 0;
	padding: 20px;
}

.dashboard-container {
	max-width: 800px;
	margin: 0 auto;
	background: white;
	border-radius: 20px;
	padding: 40px;
	box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.welcome-section {
	text-align: center;
	margin-bottom: 40px;
}

.logo {
	width: 60px;
	height: auto;
	margin-bottom: 20px;
}

h1 {
	color: #FF6B9D;
	margin-bottom: 10px;
}

.username {
	color: #666;
	font-size: 18px;
}

.actions {
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
	gap: 20px;
	margin-top: 30px;
}

.action-card {
	background: #F5F3E7;
	padding: 20px;
	border-radius: 15px;
	text-align: center;
	transition: transform 0.3s ease;
}

.action-card:hover {
	transform: translateY(-5px);
}

.logout-btn {
	background: #FF6B9D;
	color: white;
	padding: 12px 30px;
	border: none;
	border-radius: 25px;
	text-decoration: none;
	display: inline-block;
	margin-top: 20px;
	transition: background 0.3s ease;
}

.logout-btn:hover {
	background: #E55A87;
	color: white;
	text-decoration: none;
}
</style>
</head>
<body>
	<div class="dashboard-container">
		<div class="welcome-section">
			<img src="<c:url value='/resources/images/logo-part1.png'/>"
				alt="PayV 로고" class="logo">
			<h1>환영합니다!</h1>
			<p class="username">${username}님</p>
		</div>

		<div class="actions">
			<div class="action-card">
				<h3>가계부 관리</h3>
				<p>수입과 지출을 기록하고 관리하세요</p>
			</div>
			<div class="action-card">
				<h3>예산 설정</h3>
				<p>월별 예산을 설정하고 모니터링하세요</p>
			</div>
			<div class="action-card">
				<h3>리포트 확인</h3>
				<p>지출 패턴과 통계를 확인하세요</p>
			</div>
			<div class="action-card">
				<h3>커뮤니티</h3>
				<p>다른 사용자들과 정보를 공유하세요</p>
			</div>
		</div>

		<div style="text-align: center;">
			<a href="<c:url value='/logout'/>" class="logout-btn">로그아웃</a>
		</div>
	</div>
</body>
</html>