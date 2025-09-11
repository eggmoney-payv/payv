<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>에그머니 - 회원가입</title>
<!-- 회원가입 페이지 전용 CSS -->
<link rel="stylesheet" href="<c:url value='/resources/css/signup.css'/>">
</head>
<body>
	<!-- 우상단 로그인 버튼 -->
	<a href="<c:url value='/login'/>" class="login-button-top">로그인</a>

	<div class="signup-container">
		<!-- 로고 영역 -->
		<div class="logo-section">
			<div class="character-icon">
				<img src="<c:url value='/resources/images/에그머니.png'/>" alt="에그머니 로고"
					class="logo-icon">
			</div>
		</div>

		<!-- 메시지 표시 -->
		<c:if test="${not empty errorMessage}">
			<div class="alert alert-error">${errorMessage}</div>
		</c:if>
		<c:if test="${not empty message}">
			<div class="alert alert-success">${message}</div>
		</c:if>

		<!-- 회원가입 폼 -->
		<form action="<c:url value='/signup'/>" method="post">
			<div class="form-section">
				<div class="input-group">
					<label class="input-label" for="email">이메일</label> <input
						type="email" id="email" name="email" class="input-field"
						placeholder="이메일을 입력하세요" required>
				</div>

				<div class="input-group">
					<label class="input-label" for="name">이름</label> <input type="text"
						id="name" name="name" class="input-field" placeholder="이름을 입력하세요"
						required>
				</div>

				<div class="input-group">
					<label class="input-label" for="password">비밀번호</label> <input
						type="password" id="password" name="password" class="input-field"
						placeholder="비밀번호를 입력하세요" required>
					<div class="password-hint">최소 8자 이상 입력해주세요</div>
				</div>

				<div class="input-group">
					<label class="input-label" for="confirmPassword">비밀번호 확인</label> <input
						type="password" id="confirmPassword" name="confirmPassword"
						class="input-field" placeholder="비밀번호를 다시 입력하세요" required>
				</div>

				<button type="submit" class="signup-button">회원가입</button>
			</div>
		</form>

		<div class="login-links">
			<a href="<c:url value='/login'/>">이미 계정이 있으신가요? 로그인</a>
		</div>
	</div>
</body>
</html>