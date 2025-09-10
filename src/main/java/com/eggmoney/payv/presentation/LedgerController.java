package com.eggmoney.payv.presentation;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eggmoney.payv.application.service.LedgerAppService;
import com.eggmoney.payv.domain.model.entity.Ledger;
import com.eggmoney.payv.domain.model.vo.UserId;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 가계부 컨트롤러
 * @author 정의탁
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/ledgers")
public class LedgerController {
	
	private final LedgerAppService ledgerAppService;
	
	private final UserId testUserId = UserId.of("550e8400-e29b-41d4-a716-446655440000");
	
	@GetMapping
    public String list(Model model) {
		
		// (jw)
		// 사용자 ID를 사용하여 해당 사용자의 가계부 목록을 가져옴
	    List<Ledger> ledgers = ledgerAppService.listByOwner(testUserId);
	    
	    // 첫 번째 가계부를 현재 선택된 가계부로 설정 (여기서는 예시로 첫 번째 가계부를 사용)
	    if (!ledgers.isEmpty()) {
	        model.addAttribute("currentAccountName", ledgers.get(0).getName());
	    }
	    // 가계부 목록을 모델에 추가
	    model.addAttribute("ledgers", ledgers);
	    // (jw)
	    
        // TODO: 애플리케이션 서비스에 조회 기능이 없다면 Query용 리더(Mapper) 추가 권장
        model.addAttribute("ledgers", ledgerAppService.listByOwner(testUserId));
        return "ledgers/list";
    }

    @GetMapping("/new")
    public String newForm() {
        return "ledgers/new";
    }

    @PostMapping
    public String create(@RequestParam("name") String name) {
        ledgerAppService.createLedger(testUserId, name);
        return "redirect:/ledgers";
    }

    /*
    @GetMapping("/{ledgerId}")
    public String detail(@PathVariable String ledgerId, Model model) {
        // model.addAttribute("ledger", ledgerAppService.get(ledgerId));
        // model.addAttribute("accounts", ...); model.addAttribute("budgets", ...); model.addAttribute("recentTxns", ...);
        return "ledgers/detail";
    }
    */
}
