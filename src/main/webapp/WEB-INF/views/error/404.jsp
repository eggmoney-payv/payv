<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>404 - 페이지를 찾을 수 없습니다 | PayV</title>
<link rel="stylesheet"
	href="<c:url value='/resources/css/error-common.css' />">
</head>
<body class="bg-gradient-404">
	<div class="error-container">
		<div class="error-icon float icon-info">🔍</div>
		<div class="error-code code-404">404</div>
		<h1 class="error-title">페이지를 찾을 수 없습니다</h1>
		<p class="error-description">
			죄송합니다. 요청하신 페이지가 존재하지 않거나<br> 이동되었을 수 있습니다.
		</p>

		<div class="search-box">
			<input type="text" class="search-input"
				placeholder="찾고 계신 내용을 검색해보세요...">
			<button class="search-btn">🔍</button>
		</div>

		<div class="action-buttons">
			<a href="javascript:history.back()" class="btn btn-secondary">이전
				페이지</a> <a href="<c:url value='/' />" class="btn btn-primary">홈으로 가기</a>
		</div>

		<div class="footer-text">문제가 계속 발생하면 고객센터에 문의해주세요.</div>
	</div>

	<script src="<c:url value='/resources/js/error-common.js' />"></script>
	<script>
		// 404 에러 로깅
		ErrorPageUtils.logErrorInfo('404', 'Page Not Found');
	</script>
</body>
</html>