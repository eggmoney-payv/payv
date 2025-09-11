<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>에그머니 - 로그인</title>
<!-- 로그인 페이지 전용 CSS -->
<link rel="stylesheet" href="<c:url value='/resources/css/login.css'/>">
</head>
<body>
	<!-- 우상단 회원가입 버튼 -->
	<a href="<c:url value='/signup'/>" class="signup-button-top">시작</a>

	<div class="login-container">
		<!-- 로고 영역 -->
		<div class="logo-section">
			<!-- 돼지 저금통 이미지 -->
			<div class="character-icon">
				<img src="<c:url value='/resources/images/logo-part1.png'/>"
					alt="돼지 저금통" class="piggy-icon">
			</div>
			<!-- 에그머니 텍스트 로고 -->
			<div class="logo-text-image">
				<img src="<c:url value='/resources/images/logo-part2.png'/>"
					alt="에그머니" class="text-logo">
			</div>
		</div>

		<!-- 메시지 표시 -->
		<c:if test="${not empty errorMessage}">
			<div class="alert alert-error">${errorMessage}</div>
		</c:if>
		<c:if test="${not empty logoutMessage}">
			<div class="alert alert-success">${logoutMessage}</div>
		</c:if>
		<c:if test="${not empty signupMessage}">
			<div class="alert alert-success">${signupMessage}</div>
		</c:if>
		<c:if test="${not empty expiredMessage}">
			<div class="alert alert-error">${expiredMessage}</div>
		</c:if>

		<!-- ⭐ action을 Spring Security 처리 URL로 변경 -->
		<form action="<c:url value='/perform_login'/>" method="post">
			<div class="input-group">
				<label for="email">이메일</label> <input type="email" id="email"
					name="email" required>
			</div>

			<div class="input-group">
				<label for="password">비밀번호</label> <input type="password"
					id="password" name="password" required>
			</div>

			<button type="submit" class="login-button">로그인</button>
		</form>

		<div class="signup-links">
			<a href="<c:url value='/signup'/>">계정이 없으신가요? 회원가입</a>
		</div>
	</div>
</body>
</html>