<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/WEB-INF/views/common/header.jsp"%>

<html>
<head>
<title>가계부 목록</title>
<link rel="stylesheet" href="<c:url value='/resources/css/common.css'/>" />
<script src="<c:url value='/resources/js/common.js'/>" defer></script>
</head>
<body>
<main>
	<h1>가계부 목록</h1>

	<p><a href="<c:url value='/ledgers/new'/>">+ 새 가계부</a></p>

	<table border="1" cellpadding="6">
		<thead>
			<tr>
				<th>ID</th>
				<th>이름</th>
				<th>보기</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="l" items="${ledgers}">
				<tr>
					<td>${l.id}</td>
					<td>${l.name}</td>
					<td><a href="<c:url value='/ledgers/${l.id}'/>">열기</a></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</main>
</body>
</html>
