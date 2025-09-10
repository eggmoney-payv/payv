<%-- <%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<aside>
	<div class="account-title">가계부1</div>
	<nav>
		<ul>
			<li><a href="/accounts">내역</a></li>
			<li><a href="/calendar">달력</a></li>
			<li><a href="/reports">보고서</a></li>
			<li><a href="/community">커뮤니티</a></li>
			<li><a href="/settings">설정</a></li>
		</ul>
	</nav>
</aside>
 --%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<aside>
	<!-- <div class="account-title">가계부1</div> -->
	<div class="account-title">
		<a href="/account-select" class="account-link">${currentAccountName}</a>
	</div>
	<hr class="aside-divider">
	<nav>
		<ul>
			<li class="${currentPage == 'accounts' ? 'active' : ''}"><a
				href="/accounts">내역</a></li>
			<li class="${currentPage == 'calendar' ? 'active' : ''}"><a
				href="/calendar">달력</a></li>
			<li class="${currentPage == 'reports' ? 'active' : ''}"><a
				href="/reports">보고서</a></li>
			<li class="${currentPage == 'boards' ? 'active' : ''}"><a
				href="/boards">커뮤니티</a></li>
			<!-- 설정 메뉴: 2단 구조 -->
			<li class="has-sub ${openMenu == 'settings' ? 'open' : ''}"><a
				href="javascript:void(0)">설정</a>
				<ul class="submenu">
					<li class="${currentPage == 'settings-profile' ? 'active' : ''}">
						<a href="/settings/profile">카테고리 설정</a>
					</li>
					<li class="${currentPage == 'settings-account' ? 'active' : ''}">
						<a href="/settings/account">예산 설정</a>
					</li>
					<li class="${currentPage == 'settings-security' ? 'active' : ''}">
						<a href="/settings/security">기타 설정</a>
					</li>
				</ul></li>
		</ul>
	</nav>
</aside>