<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<!-- 대시보드 전용 CSS -->
<link rel="stylesheet"
	href="<c:url value='/resources/css/dashboard.css'/>">

<!-- 대시보드 메인 컨텐츠 -->
<div class="dashboard-container">
	<!-- 환영 메시지 -->
	<div class="welcome-section">
		<h1>
			안녕하세요,
			<sec:authentication property="name" />
			님!
		</h1>
		<p>PAYV에서 스마트하게 가계부를 관리해보세요.</p>
	</div>

	<!-- 빠른 통계 카드 -->
	<div class="stats-grid">
		<div class="stat-card">
			<div class="stat-icon income">💰</div>
			<div class="stat-content">
				<h3>이번 달 수입</h3>
				<p class="stat-amount">₩0</p>
				<span class="stat-change">+ ₩0</span>
			</div>
		</div>

		<div class="stat-card">
			<div class="stat-icon expense">💸</div>
			<div class="stat-content">
				<h3>이번 달 지출</h3>
				<p class="stat-amount">₩0</p>
				<span class="stat-change">- ₩0</span>
			</div>
		</div>

		<div class="stat-card">
			<div class="stat-icon balance">💵</div>
			<div class="stat-content">
				<h3>잔액</h3>
				<p class="stat-amount">₩0</p>
				<span class="stat-change neutral">₩0</span>
			</div>
		</div>

		<div class="stat-card">
			<div class="stat-icon budget">🎯</div>
			<div class="stat-content">
				<h3>예산 달성률</h3>
				<p class="stat-amount">0%</p>
				<span class="stat-change">목표까지 ₩0</span>
			</div>
		</div>
	</div>

	<!-- 빠른 액션 버튼 -->
	<div class="quick-actions">
		<h2>빠른 작업</h2>
		<div class="action-grid">
			<a href="<c:url value='/ledger/income/add'/>" class="action-card">
				<div class="action-icon">➕</div>
				<h3>수입 등록</h3>
				<p>새로운 수입 내역을 등록하세요</p>
			</a> <a href="<c:url value='/ledger/expense/add'/>" class="action-card">
				<div class="action-icon">➖</div>
				<h3>지출 등록</h3>
				<p>새로운 지출 내역을 등록하세요</p>
			</a> <a href="<c:url value='/ledger/list'/>" class="action-card">
				<div class="action-icon">📊</div>
				<h3>내역 조회</h3>
				<p>수입/지출 내역을 확인하세요</p>
			</a> <a href="<c:url value='/budget/manage'/>" class="action-card">
				<div class="action-icon">🎯</div>
				<h3>예산 관리</h3>
				<p>월별 예산을 설정하고 관리하세요</p>
			</a>
		</div>
	</div>

	<!-- 최근 거래 내역 -->
	<div class="recent-transactions">
		<div class="section-header">
			<h2>최근 거래 내역</h2>
			<a href="<c:url value='/ledger/list'/>" class="view-all">전체 보기</a>
		</div>

		<div class="transaction-list">
			<!-- 데이터가 없을 때 표시 -->
			<div class="empty-state">
				<div class="empty-icon">📝</div>
				<h3>아직 거래 내역이 없습니다</h3>
				<p>첫 번째 수입 또는 지출을 등록해보세요!</p>
				<a href="<c:url value='/ledger/income/add'/>"
					class="btn btn-primary">수입 등록하기</a>
			</div>

			<!-- 추후 실제 데이터로 대체 예정 -->
			<!-- 
            <div class="transaction-item income">
                <div class="transaction-icon">💰</div>
                <div class="transaction-info">
                    <h4>급여</h4>
                    <span class="transaction-date">2024-09-11</span>
                </div>
                <div class="transaction-amount">+₩3,000,000</div>
            </div>
            -->
		</div>
	</div>

	<!-- 가계부 목록 -->
	<div class="ledger-books">
		<div class="section-header">
			<h2>내 가계부</h2>
			<a href="<c:url value='/ledger/books/create'/>" class="btn btn-small">새
				가계부 만들기</a>
		</div>

		<div class="books-grid">
			<!-- 기본 가계부 -->
			<div class="book-card default">
				<div class="book-icon">📋</div>
				<div class="book-info">
					<h3>개인 가계부</h3>
					<p>기본 가계부</p>
					<span class="book-stats">거래 0건</span>
				</div>
				<a href="<c:url value='/ledger/books/1'/>" class="book-link">열기</a>
			</div>

			<!-- 추후 사용자가 생성한 가계부들 표시 -->
			<div class="book-card add-new">
				<div class="add-icon">➕</div>
				<div class="add-content">
					<h3>새 가계부 만들기</h3>
					<p>
						사업용, 모임용 등<br>용도별 가계부를 만들어보세요
					</p>
				</div>
			</div>
		</div>
	</div>
</div>