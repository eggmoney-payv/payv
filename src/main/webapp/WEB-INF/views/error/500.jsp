<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>500 - 서버 오류 | PayV</title>
<link rel="stylesheet"
	href="<c:url value='/resources/css/error-common.css' />">
</head>
<body class="bg-gradient-500">
	<div class="error-container">
		<div class="error-icon pulse icon-error">⚡</div>
		<div class="error-code code-500">500</div>
		<h1 class="error-title">서버 오류가 발생했습니다</h1>
		<p class="error-description">
			죄송합니다. 일시적인 서버 문제가 발생했습니다.<br> 잠시 후 다시 시도해주세요.
		</p>

		<div class="refresh-timer">
			<div class="timer-text">
				자동 새로고침: <span id="countdown">30</span>초 후
			</div>
			<div class="timer-bar">
				<div class="timer-progress" id="progress"></div>
			</div>
		</div>

		<div class="action-buttons">
			<button onclick="location.reload()" class="btn btn-secondary-500">새로고침</button>
			<a href="<c:url value='/' />" class="btn btn-primary-500">홈으로 가기</a>
		</div>

		<div class="footer-text">
			문제가 계속 발생하면 시스템 관리자에게 문의해주세요.<br> <small>오류 시간: <span
				id="errorTime"></span></small>
		</div>
	</div>

	<script src="<c:url value='/resources/js/error-common.js' />"></script>
	<script>
		// 500 에러 로깅
		ErrorPageUtils.logErrorInfo('500', 'Internal Server Error');
	</script>
</body>
</html>