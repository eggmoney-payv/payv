<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>거래 내역 수정</title>
<link rel="stylesheet" href="<c:url value='/resources/css/common.css'/>">
</head>
<body>
	<jsp:include page="/WEB-INF/views/common/header.jsp" />
	<jsp:include page="/WEB-INF/views/common/aside.jsp" />

	<main id="main" data-ledger-id="${ledgerId}"
		data-selected-root-id="${selectedRootId}"
		data-selected-child-id="${selectedChildId}">
	
	<div class="container" style="padding: 16px;">
		<h2>거래 내역 수정</h2>

		<c:if test="${not empty error}">
			<div class="alert error">${error}</div>
		</c:if>

		<form id="transaction-form" method="post"
			action="<c:url value='/ledgers/${ledgerId}/transaction/${transaction.id}'/>"
			class="card" style="padding: 16px;">

			<!-- 일자 -->
			<div style="margin-bottom: 10px;">
				<label>일자 <input type="date" name="date"
					value="${form.date}" required />
				</label>
			</div>

			<!-- 유형 -->
			<div style="margin-bottom: 10px;">
				<label>유형 <select name="type" required>
						<c:forEach var="t" items="${transactionTypes}">
							<option value="${t.name()}"
								<c:if test="${t.name()==form.type}">selected</c:if>>${t.name()}</option>
						</c:forEach>
				</select>
				</label>
			</div>

			<!-- 금액 -->
			<div style="margin-bottom: 10px;">
				<label>금액(원) <input type="number" name="amount" min="0"
					step="100" value="${form.amount}" required />
				</label>
			</div>

			<!-- 자산 -->
			<div style="margin-bottom: 10px;">
				<label>자산 <select name="accountId" required>
						<c:forEach var="a" items="${accounts}">
							<option value="${a.id}"
								<c:if test="${a.id == form.accountId}">selected</c:if>>${a.name}</option>
						</c:forEach>
				</select>
				</label>
			</div>

			<!-- 카테고리: 상위/하위(항상 노출) -->
			<div style="margin-bottom: 10px;">
				<label>카테고리(상위) <select id="rootCategoryId" required>
						<option value="">(선택)</option>
						<c:forEach var="r" items="${rootCategories}">
							<option value="${r.id}"
								<c:if test="${r.id == selectedRootId}">selected</c:if>>${r.name}</option>
						</c:forEach>
				</select>
				</label>
			</div>

			<div style="margin-bottom: 10px;">
				<label>카테고리(하위) <select id="childCategoryId">
						<!-- 최초엔 아래 스크립트가 채움 -->
						<option value="">(상위를 먼저 선택하세요)</option>
				</select>
				</label>
				<div class="muted">* 최종 카테고리는 하위 선택값이 우선이며, 하위를 선택하지 않으면 상위가
					저장됩니다.</div>
			</div>

			<!-- 최종 전송용 hidden -->
			<input type="hidden" name="categoryId" id="categoryIdHidden"
				value="${form.categoryId}" />

			<!-- 메모 -->
			<div style="margin-bottom: 10px;">
				<label>메모 <input type="text" name="memo"
					value="${form.memo}" />
				</label>
			</div>

			<div style="display: flex; gap: 8px;">
				<button type="submit" class="btn">저장</button>
				<a class="btn"
					href="<c:url value='/ledgers/${ledgerId}/transaction?month=${form.date.substring(0,7)}'/>">취소</a>
			</div>
		</form>
	</div>
	</main>

	<script src="<c:url value='/resources/js/common.js'/>"></script>
	<script>
    // API 베이스 (컨텍스트 포함)
    var childApiBase = '<c:url value="/api/ledgers/${ledgerId}/categories/"/>'; // + {rootId}/children

    // 요소
    var mainEl   = document.getElementById('main');
    var rootSel  = document.getElementById('rootCategoryId');
    var childSel = document.getElementById('childCategoryId');
    var hiddenId = document.getElementById('categoryIdHidden');
    var formEl   = document.getElementById('transaction-form');

    // 초기 선택값(서버에서 내려준 현재 거래 상태)
    var selectedRootId  = mainEl.dataset.selectedRootId || '';
    var selectedChildId = mainEl.dataset.selectedChildId || '';

    function clearSelectOptions(sel){
      while(sel.firstChild) sel.removeChild(sel.firstChild);
    }
    function setChildOptions(list, preselect){
      clearSelectOptions(childSel);
      if (Object.prototype.toString.call(list) === '[object Array]' && list.length > 0) {
        var base = document.createElement('option');
        base.value = '';
        base.textContent = '(하위 선택)';
        childSel.appendChild(base);
        for (var i=0; i<list.length; i++){
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
    async function loadChildren(rootId, preselectChild){
      clearSelectOptions(childSel);
      if (!rootId){
        var msg = document.createElement('option');
        msg.value = '';
        msg.textContent = '(상위를 먼저 선택하세요)';
        childSel.appendChild(msg);
        return;
      }
      try{
        var res  = await fetch(childApiBase + encodeURIComponent(rootId) + '/children', { method:'GET' });
        var list = await res.json(); // [{id,name}]
        setChildOptions(list, preselectChild);
      }catch(e){
        clearSelectOptions(childSel);
        var err = document.createElement('option');
        err.value = '';
        err.textContent = '(하위 불러오기 실패)';
        childSel.appendChild(err);
      }
    }

    // 루트 변경 → 하위 새로 로드(선택 초기화)
    rootSel.addEventListener('change', function(){
      loadChildren(rootSel.value, '');
    });

    // 제출: 하위 선택 우선, 없으면 루트. 둘 다 없으면 막기.
    formEl.addEventListener('submit', function(e){
      var rootId  = rootSel.value || '';
      var childId = childSel.value || '';
      if (!rootId && !childId){
        e.preventDefault();
        alert('상위 또는 하위 카테고리를 선택해 주세요.');
        return;
      }
      hiddenId.value = childId || rootId;
    });

    // 초기 진입 처리: 루트가 선택돼 있으면 하위 로드 + 기존 하위 선택 복원
    document.addEventListener('DOMContentLoaded', function(){
      if (rootSel.value){
        loadChildren(rootSel.value, selectedChildId);
      }
    });
  </script>
</body>
</html>
