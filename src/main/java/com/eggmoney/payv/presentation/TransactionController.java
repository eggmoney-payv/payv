package com.eggmoney.payv.presentation;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eggmoney.payv.application.service.AccountAppService;
import com.eggmoney.payv.application.service.CategoryAppService;
import com.eggmoney.payv.application.service.TransactionAppService;
import com.eggmoney.payv.domain.model.entity.Account;
import com.eggmoney.payv.domain.model.entity.Category;
import com.eggmoney.payv.domain.model.entity.Transaction;
import com.eggmoney.payv.domain.model.entity.TransactionType;
import com.eggmoney.payv.domain.model.vo.AccountId;
import com.eggmoney.payv.domain.model.vo.CategoryId;
import com.eggmoney.payv.domain.model.vo.LedgerId;
import com.eggmoney.payv.domain.model.vo.Money;
import com.eggmoney.payv.domain.model.vo.TransactionId;
import com.eggmoney.payv.domain.shared.error.DomainException;
import com.eggmoney.payv.presentation.dto.CategoryOptionDto;
import com.eggmoney.payv.presentation.dto.TransactionCreateDto;
import com.eggmoney.payv.presentation.dto.TransactionListItemDto;
import com.eggmoney.payv.presentation.dto.TransactionUpdateDto;

import lombok.RequiredArgsConstructor;

/**
 * 거래 내역 컨트롤러
 * @author 정의탁
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/ledgers/{ledgerId}/transaction")
public class TransactionController {

	private final TransactionAppService transactionAppService;
	private final AccountAppService accountAppService;
	private final CategoryAppService categoryAppService;

	// 폼에서 사용할 enum 목록.
	@ModelAttribute("transactionTypes")
	public TransactionType[] tansactionTypes() {
		return TransactionType.values();
	}

	// ===== 목록 =====
	@GetMapping
	public String list(@PathVariable String ledgerId, 
					   @RequestParam(value = "month", required = false) String month,
					   @ModelAttribute("message") String message, 
					   @ModelAttribute("error") String error, Model model) {

		YearMonth ym = (month == null || month.trim().isEmpty()) ? 
				YearMonth.now() : YearMonth.parse(month.trim()); // "YYYY-MM"

		LedgerId lId = LedgerId.of(ledgerId);

		// TODO: 페이징 적용되도록 변경.
		List<Transaction> transactionList = transactionAppService.listByMonth(lId, ym, Integer.MAX_VALUE, 0);

		// 뷰 표시를 위한 자산/카테고리 이름 맵 구성
		Map<String, String> accountNameMap = accountAppService.listByLedger(lId).stream().collect(
				Collectors.toMap(a -> a.getId().toString(), Account::getName, (a, b) -> a, LinkedHashMap::new));

		Map<String, String> categoryNameMap = categoryAppService.listByLedger(lId).stream().collect(
				Collectors.toMap(c -> c.getId().toString(), Category::getName, (a, b) -> a, LinkedHashMap::new));

		List<TransactionListItemDto> items = transactionList.stream().map(t -> {
			TransactionListItemDto d = new TransactionListItemDto();
			d.setId(t.getId().toString());
			d.setDate(t.getDate().toString());
			d.setAccountName(accountNameMap.getOrDefault(t.getAccountId().toString(), t.getAccountId().toString()));
			d.setCategoryName(categoryNameMap.getOrDefault(t.getCategoryId().toString(), t.getCategoryId().toString()));
			d.setType(t.getType().name());
			d.setAmount(String.valueOf(t.getAmount())); // Money.toString()
			d.setMemo(t.getMemo());
			return d;
		}).collect(Collectors.toList());

		model.addAttribute("ledgerId", ledgerId);
		model.addAttribute("month", ym.toString()); // yyyy-MM
		model.addAttribute("transaction", items);
		return "transactions/list";
	}

	// ===== 신규 폼 =====
	@GetMapping("/new")
	public String newForm(@PathVariable String ledgerId, Model model) {
		LedgerId lId = LedgerId.of(ledgerId);
		List<Account> accounts = accountAppService.listByLedger(lId);
		List<Category> rootCategories = categoryAppService.rootCategoryListByLedger(lId);

		model.addAttribute("ledgerId", ledgerId);
		model.addAttribute("accounts", accounts);
		model.addAttribute("rootCategories", rootCategories);
		model.addAttribute("form", defaultCreateForm());
		return "transactions/new";
	}

	// ===== 신규 처리 =====
	@PostMapping
	public String create(@PathVariable String ledgerId, 
						 @ModelAttribute("form") TransactionCreateDto form,
						 RedirectAttributes ra) {
		try {
			// 검증
			if (isBlank(form.getAccountId()) || isBlank(form.getCategoryId()) || isBlank(form.getDate())
					|| isBlank(form.getType()) || isBlank(form.getAmount())) {
				ra.addFlashAttribute("error", "필수 입력값이 누락되었습니다.");
				return "redirect:/ledgers/" + ledgerId + "/transaction/new";
			}

			LedgerId lId = LedgerId.of(ledgerId);
			AccountId accId = AccountId.of(form.getAccountId());
			CategoryId catId = CategoryId.of(form.getCategoryId());
			LocalDate date = LocalDate.parse(form.getDate());
			TransactionType type = TransactionType.valueOf(form.getType());

			long won = parseWon(form.getAmount());
			if (won < 0) {
				ra.addFlashAttribute("error", "금액은 0 이상의 정수만 가능합니다.");
				return "redirect:/ledgers/" + ledgerId + "/transaction/new";
			}
			
			transactionAppService.oneClickCreate(lId, accId, type, date, Money.won(won), catId, form.getMemo());

			ra.addFlashAttribute("message", "거래를 등록했습니다.");
			// 등록 후: 해당 월 목록으로
			String month = YearMonth.from(date).toString();
			return "redirect:/ledgers/" + ledgerId + "/transaction?month=" + month;

		} catch (DomainException e) {
			ra.addFlashAttribute("error", e.getMessage());
			return "redirect:/ledgers/" + ledgerId + "/transaction/new";
		}
	}

	// ===== 수정 폼 =====
	@GetMapping("/{txnId}/edit")
	public String editForm(@PathVariable String ledgerId, @PathVariable String txnId, Model model) {
		// 거래 상세
	    Transaction t = transactionAppService.getDetails(TransactionId.of(txnId));

	    LedgerId lId = LedgerId.of(ledgerId);
	    List<Account> accounts = accountAppService.listByLedger(lId);
	    List<Category> allCategoryList  = categoryAppService.listByLedger(lId);

	    // 1) 루트 카테고리 목록.
	    Comparator<Category> cmp = Comparator
	        .comparingInt(Category::getSortOrder)
	        .thenComparing(Category::getName, String.CASE_INSENSITIVE_ORDER);

	    List<Category> rootCategories = allCategoryList.stream()
	        .filter(c -> c.getParentId() == null)
	        .sorted(cmp)
	        .collect(Collectors.toList());

	    // 2) 현재 거래의 카테고리로부터 (루트/하위) 결정.
	    Category current = allCategoryList.stream()
	        .filter(c -> c.getId().equals(t.getCategoryId()))
	        .findFirst().orElse(null);

	    String selectedRootId = "";
	    String selectedChildId = "";
	    if (current != null) {
	        if (current.getParentId() == null) {
	            selectedRootId = current.getId().toString();
	        } else {
	            selectedRootId = current.getParentId().toString();
	            selectedChildId = current.getId().toString();
	        }
	    }

	    // 3) 폼 DTO (hidden categoryId로 최종 전송)
	    TransactionUpdateDto form = new TransactionUpdateDto(
	        t.getAccountId().toString(),
	        t.getCategoryId().toString(),
	        t.getDate().toString(),
	        t.getType().name(),
	        String.valueOf(t.getAmount().toLong()), // Money → long (네 도메인에 맞춤)
	        t.getMemo()
	    );

	    model.addAttribute("ledgerId", ledgerId);
	    model.addAttribute("transaction", t);
	    model.addAttribute("accounts", accounts);
	    model.addAttribute("rootCategories", rootCategories);
	    model.addAttribute("selectedRootId", selectedRootId);
	    model.addAttribute("selectedChildId", selectedChildId);
	    model.addAttribute("form", form);

	    return "transactions/edit";
	}

	// ===== 수정 처리 =====
	@PostMapping("/{txnId}")
	public String update(@PathVariable String ledgerId, @PathVariable String txnId,
			@ModelAttribute("form") TransactionUpdateDto form, RedirectAttributes ra) {
		try {
			if (isBlank(form.getAccountId()) || isBlank(form.getCategoryId()) || isBlank(form.getDate())
					|| isBlank(form.getType()) || isBlank(form.getAmount())) {
				ra.addFlashAttribute("error", "필수 입력값이 누락되었습니다.");
				return "redirect:/ledgers/" + ledgerId + "/transaction/" + txnId + "/edit";
			}

			// LedgerId lId = LedgerId.of(ledgerId);
			AccountId accId = AccountId.of(form.getAccountId());
			CategoryId catId = CategoryId.of(form.getCategoryId());
			LocalDate date = LocalDate.parse(form.getDate());
			TransactionType type = TransactionType.valueOf(form.getType());

			long won = parseWon(form.getAmount());
			if (won < 0) {
				ra.addFlashAttribute("error", "금액은 0 이상의 정수만 가능합니다.");
				return "redirect:/ledgers/" + ledgerId + "/transaction/" + txnId + "/edit";
			}

			transactionAppService.oneClickUpdate(TransactionId.of(txnId), accId, type, date, Money.won(won), catId, form.getMemo());

			ra.addFlashAttribute("message", "거래를 수정했습니다.");
			String month = YearMonth.from(date).toString();
			return "redirect:/ledgers/" + ledgerId + "/transaction?month=" + month;

		} catch (DomainException e) {
			ra.addFlashAttribute("error", e.getMessage());
			return "redirect:/ledgers/" + ledgerId + "/transaction/" + txnId + "/edit";
		}
	}

	// ===== 삭제 (AJAX, JSON) =====
	@DeleteMapping(value = "/{txnId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> deleteAjax(@PathVariable String ledgerId, @PathVariable String txnId) {
		Map<String, Object> res = new HashMap<>();
		try {
			transactionAppService.oneClickDelete(TransactionId.of(txnId));
			res.put("ok", true);
		} catch (DomainException e) {
			res.put("ok", false);
			res.put("message", e.getMessage());
		}
		return res;
	}

	// ===== helpers =====
	private TransactionCreateDto defaultCreateForm() {
		TransactionCreateDto f = new TransactionCreateDto();
		f.setDate(LocalDate.now().toString());
		f.setType(TransactionType.EXPENSE.name()); // 기본값(원하면 변경)
		f.setAmount("0");
		return f;
	}

	private List<CategoryOptionDto> toCategoryOptions(List<Category> list) {
		// 루트/자식 트리를 평탄화해서 select 옵션 라벨 구성
		Map<String, Category> map = new LinkedHashMap<>();
		for (Category c : list)
			map.put(c.getId().toString(), c);
		List<CategoryOptionDto> opts = new ArrayList<>();

		Comparator<Category> cmp = Comparator.comparingInt(Category::getSortOrder).thenComparing(Category::getName,
				String.CASE_INSENSITIVE_ORDER);

		// 루트
		List<Category> roots = list.stream().filter(c -> c.getParentId() == null).sorted(cmp)
				.collect(Collectors.toList());
		for (Category r : roots) {
			opts.add(new CategoryOptionDto(r.getId().toString(), r.getName()));
			// 자식
			List<Category> children = list.stream().filter(c -> r.getId().equals(c.getParentId())).sorted(cmp)
					.collect(Collectors.toList());
			for (Category ch : children) {
				opts.add(new CategoryOptionDto(ch.getId().toString(), "— " + ch.getName()));
			}
		}
		return opts;
	}

	private long parseWon(String s) {
		try {
			return Long.parseLong(s.trim());
		} catch (Exception e) {
			return -1L;
		}
	}

	private boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}
}
