<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<!-- 마이페이지 페이지 전용 CSS -->
<link rel="stylesheet" href="<c:url value='/resources/css/profile.css'/>">
<!-- 마이페이지 메인 컨텐츠 -->
<div class="profile-container">
	<div class="profile-header">
		<h2>마이페이지</h2>
		<p>개인정보 관리 및 설정</p>
	</div>

	<!-- 성공 메시지 -->
	<c:if test="${not empty message}">
		<div class="alert alert-success">${message}</div>
	</c:if>

	<!-- 사용자 정보 카드 -->
	<div class="user-info-card">
		<div class="user-avatar">
			<div class="avatar-circle">
				<span>${user.name.substring(0,1).toUpperCase()}</span>
			</div>
		</div>
		<div class="user-details">
			<h3>${user.name}</h3>
			<p class="user-email">${user.email}</p>
			<p class="user-role">
				<sec:authorize access="hasRole('ADMIN')">
					<span class="badge badge-admin">관리자</span>
				</sec:authorize>
				<sec:authorize access="hasRole('PREMIUM')">
					<span class="badge badge-premium">프리미엄</span>
				</sec:authorize>
				<sec:authorize access="hasRole('STANDARD')">
					<span class="badge badge-standard">일반 회원</span>
				</sec:authorize>
			</p>
		</div>
	</div>

	<!-- 개인정보 수정 폼 -->
	<div class="form-section">
		<h3>개인정보 수정</h3>
		<form:form action="/user/profile" method="post"
			modelAttribute="profileForm" cssClass="profile-form">

			<div class="form-row">
				<div class="form-group">
					<label for="email">이메일</label>
					<form:input path="email" type="email" id="email"
						cssClass="form-control ${not empty profileForm.getFieldError('email') ? 'error' : ''}" />
					<form:errors path="email" cssClass="error-message" />
				</div>

				<div class="form-group">
					<label for="name">이름</label>
					<form:input path="name" type="text" id="name"
						cssClass="form-control ${not empty profileForm.getFieldError('name') ? 'error' : ''}" />
					<form:errors path="name" cssClass="error-message" />
				</div>
			</div>

			<div class="form-actions">
				<button type="submit" class="btn btn-primary">정보 수정</button>
			</div>
		</form:form>
	</div>

	<!-- 비밀번호 변경 폼 -->
	<div class="form-section">
		<h3>비밀번호 변경</h3>
		<form:form action="/user/password" method="post"
			modelAttribute="passwordForm" cssClass="password-form">

			<div class="form-group">
				<label for="currentPassword">현재 비밀번호</label>
				<form:input path="currentPassword" type="password"
					id="currentPassword"
					cssClass="form-control ${not empty passwordForm.getFieldError('currentPassword') ? 'error' : ''}" />
				<form:errors path="currentPassword" cssClass="error-message" />
			</div>

			<div class="form-row">
				<div class="form-group">
					<label for="newPassword">새 비밀번호</label>
					<form:input path="newPassword" type="password" id="newPassword"
						cssClass="form-control ${not empty passwordForm.getFieldError('newPassword') ? 'error' : ''}" />
					<form:errors path="newPassword" cssClass="error-message" />
				</div>

				<div class="form-group">
					<label for="confirmNewPassword">새 비밀번호 확인</label>
					<form:input path="confirmNewPassword" type="password"
						id="confirmNewPassword"
						cssClass="form-control ${not empty passwordForm.getFieldError('confirmNewPassword') ? 'error' : ''}" />
					<form:errors path="confirmNewPassword" cssClass="error-message" />
				</div>
			</div>

			<div class="form-actions">
				<button type="submit" class="btn btn-secondary">비밀번호 변경</button>
			</div>
		</form:form>
	</div>

	<!-- 계정 관리 -->
	<div class="account-section">
		<h3>계정 관리</h3>
		<div class="account-actions">
			<a href="<c:url value='/logout'/>" class="btn btn-outline">로그아웃</a>
			<!-- 추후 회원탈퇴 기능 추가 -->
		</div>
	</div>
</div>