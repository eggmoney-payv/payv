<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>거래 내역 목록</title>
<link rel="stylesheet" href="<c:url value='/resources/css/common.css'/>">
</head>
<body>
	<jsp:include page="/WEB-INF/views/common/header.jsp" />
	<jsp:include page="/WEB-INF/views/common/aside.jsp" />

	<main id="main" data-ledger-id="${ledgerId}">
	<div class="container">
		<h2>거래 내역</h2>

		<c:if test="${not empty message}">
			<div class="alert success">${message}</div>
		</c:if>
		<c:if test="${not empty error}">
			<div class="alert error">${error}</div>
		</c:if>

		<div style="display: flex; gap: 8px; margin: 8px 0;">
			<a class="btn"
				href="<c:url value='/ledgers/${ledgerId}/transaction/new'/>">+
				거래 내역 추가</a> <a class="btn" href="<c:url value='/ledgers/${ledgerId}'/>">←
				가계부 홈</a>
		</div>

		<!-- 필터 -->
		<form id="filterForm" method="get"
			action="<c:url value='/ledgers/${ledgerId}/transaction'/>"
			style="display: flex; gap: 8px; flex-wrap: wrap; align-items: flex-end; margin-bottom: 12px;">
			<div>
				<label>시작일 <input type="date" name="start"
					value="${cond.start}" />
				</label>
			</div>
			<div>
				<label>종료일 <input type="date" name="end"
					value="${cond.end}" />
				</label>
			</div>
			<div>
				<label>자산 <select name="accountId">
						<option value="">(전체)</option>
						<c:forEach var="a" items="${accounts}">
							<option value="${a.id}"
								<c:if test="${a.id == cond.accountId}">selected</c:if>>${a.name}</option>
						</c:forEach>
				</select>
				</label>
			</div>
			<div>
				<label>카테고리(상위) <select id="rootCategoryId"
					name="rootCategoryId">
						<option value="">(전체)</option>
						<c:forEach var="r" items="${rootCategories}">
							<option value="${r.id}"
								<c:if test="${r.id == cond.rootCategoryId}">selected</c:if>>${r.name}</option>
						</c:forEach>
				</select>
				</label>
			</div>
			<div>
				<label>카테고리(하위) <select id="childCategoryId"
					name="categoryId">
						<option value="">(전체/미선택)</option>
						<!-- JS로 옵션 채움(초기 선택 복원 포함) -->
				</select>
				</label>
			</div>
			<div>
				<label>페이지 크기 <select name="size">
						<option value="10" <c:if test="${size==10}">selected</c:if>>10</option>
						<option value="20" <c:if test="${size==20}">selected</c:if>>20</option>
						<option value="50" <c:if test="${size==50}">selected</c:if>>50</option>
						<option value="100" <c:if test="${size==100}">selected</c:if>>100</option>
				</select>
				</label>
			</div>

			<button class="btn" type="submit">조회</button>
		</form>

		<!-- 목록 -->
		<table class="table" style="width: 100%; border-collapse: collapse;">
			<thead>
				<tr>
					<th
						style="text-align: left; padding: 8px; border-bottom: 1px solid #ccc;">일자</th>
					<th
						style="text-align: left; padding: 8px; border-bottom: 1px solid #ccc;">계좌</th>
					<th
						style="text-align: left; padding: 8px; border-bottom: 1px solid #ccc;">카테고리</th>
					<th
						style="text-align: center; padding: 8px; border-bottom: 1px solid #ccc;">유형</th>
					<th
						style="text-align: right; padding: 8px; border-bottom: 1px solid #ccc;">금액</th>
					<th
						style="text-align: left; padding: 8px; border-bottom: 1px solid #ccc;">메모</th>
					<th
						style="text-align: center; padding: 8px; border-bottom: 1px solid #ccc;">작업</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="t" items="${txns}">
					<tr>
						<td style="padding: 8px;">${t.date}</td>
						<td style="padding: 8px;">${t.accountName}</td>
						<td style="padding: 8px;">${t.categoryName}</td>
						<td style="padding: 8px; text-align: center;">${t.type}</td>
						<td style="padding: 8px; text-align: right;">${t.amount}</td>
						<td style="padding: 8px;">${t.memo}</td>
						<td style="padding: 8px; text-align: center;"><a class="btn"
							href="<c:url value='/ledgers/${ledgerId}/transaction/${t.id}/edit'/>">수정</a>
							<button class="btn danger js-del" data-id="${t.id}"
								style="margin-left: 8px;">삭제</button></td>
					</tr>
				</c:forEach>
				<c:if test="${empty txns}">
					<tr>
						<td colspan="7"
							style="padding: 12px; text-align: center; color: #777;">조회
							결과가 없습니다.</td>
					</tr>
				</c:if>
			</tbody>
		</table>

		<!-- 페이징 -->
		<div class="pagination"
			style="margin-top: 12px; display: flex; gap: 6px; align-items: center; flex-wrap: wrap;">
			<c:if test="${hasPrev}">
				<c:url var="prevUrl" value="/ledgers/${ledgerId}/transaction">
					<c:param name="start" value="${cond.start}" />
					<c:param name="end" value="${cond.end}" />
					<c:param name="accountId" value="${cond.accountId}" />
					<c:param name="rootCategoryId" value="${cond.rootCategoryId}" />
					<c:param name="categoryId" value="${cond.categoryId}" />
					<c:param name="page" value="${page-1}" />
					<c:param name="size" value="${size}" />
				</c:url>
				<a class="btn" href="${prevUrl}">이전</a>
			</c:if>

			<c:forEach var="p" begin="${startPage}" end="${endPage}">
				<c:url var="pageUrl" value="/ledgers/${ledgerId}/transaction">
					<c:param name="start" value="${cond.start}" />
					<c:param name="end" value="${cond.end}" />
					<c:param name="accountId" value="${cond.accountId}" />
					<c:param name="rootCategoryId" value="${cond.rootCategoryId}" />
					<c:param name="categoryId" value="${cond.categoryId}" />
					<c:param name="page" value="${p}" />
					<c:param name="size" value="${size}" />
				</c:url>
				<a class="btn" href="${pageUrl}"
					style="<c:if test='${p==page}'>background:#967E76;color:#fff;</c:if>">${p}</a>
			</c:forEach>

			<c:if test="${hasNext}">
				<c:url var="nextUrl" value="/ledgers/${ledgerId}/transaction">
					<c:param name="start" value="${cond.start}" />
					<c:param name="end" value="${cond.end}" />
					<c:param name="accountId" value="${cond.accountId}" />
					<c:param name="rootCategoryId" value="${cond.rootCategoryId}" />
					<c:param name="categoryId" value="${cond.categoryId}" />
					<c:param name="page" value="${page+1}" />
					<c:param name="size" value="${size}" />
				</c:url>
				<a class="btn" href="${nextUrl}">다음</a>
			</c:if>

			<span class="muted" style="margin-left: 8px;">${page} /
				${totalPages} 페이지</span>
		</div>
	</div>
	</main>

	<script src="<c:url value='/resources/js/common.js'/>"></script>
	<script>
    // 하위 카테고리 로딩
    var childApiBase = '<c:url value="/api/ledgers/${ledgerId}/categories/"/>'; // + {rootId}/children
    var rootSel  = document.getElementById('rootCategoryId');
    var childSel = document.getElementById('childCategoryId');
    var selectedChildId = '${cond.categoryId != null ? cond.categoryId : ""}';

    function clearSelectOptions(sel){ while(sel.firstChild) sel.removeChild(sel.firstChild); }
    function setChildOptions(list, preselect){
      clearSelectOptions(childSel);
      var base = document.createElement('option');
      base.value = '';
      base.textContent = '(전체/미선택)';
      childSel.appendChild(base);
      if (Object.prototype.toString.call(list) === '[object Array]' && list.length > 0){
        for (var i=0;i<list.length;i++){
          var it = list[i];
          var opt = document.createElement('option');
          opt.value = it.id;
          opt.textContent = it.name;
          if (preselect && preselect === it.id) opt.selected = true;
          childSel.appendChild(opt);
        }
      } else {
        var none = document.createElement('option');
        none.value = '';
        none.textContent = '(하위 없음)';
        childSel.appendChild(none);
      }
    }
    async function loadChildren(rootId, preselect){
      clearSelectOptions(childSel);
      if (!rootId){
        var base = document.createElement('option');
        base.value = '';
        base.textContent = '(전체/미선택)';
        childSel.appendChild(base);
        return;
      }
      try{
        var res = await fetch(childApiBase + encodeURIComponent(rootId) + '/children', { method:'GET' });
        var list = await res.json();
        setChildOptions(list, preselect);
      }catch(e){
        clearSelectOptions(childSel);
        var err = document.createElement('option');
        err.value = '';
        err.textContent = '(하위 불러오기 실패)';
        childSel.appendChild(err);
      }
    }
    rootSel.addEventListener('change', function(){ loadChildren(rootSel.value, ''); });

    // 초기 진입: 루트 선택되어 있으면 하위 로드 + 선택 복원
    document.addEventListener('DOMContentLoaded', function(){
      var rootVal = rootSel.value;
      if (rootVal){ loadChildren(rootVal, selectedChildId); }
    });

    // 삭제 비동기
    document.addEventListener('click', async function(e){
      var btn = e.target.closest('.js-del');
      if(!btn) return;
      var id = btn.getAttribute('data-id');
      if(!id) return;
      if(!confirm('정말 삭제하시겠습니까?')) return;

      var url = '<c:url value="/ledgers/${ledgerId}/transaction/"/>' + id;
      try{
        var res = await fetch(url, { method:'DELETE' });
        var json = await res.json();
        if(!json.ok){
          alert(json.message || '삭제 실패');
          return;
        }
        location.reload();
      }catch(err){
        alert('네트워크 오류로 삭제하지 못했습니다.');
      }
    });
  </script>
</body>
</html>
