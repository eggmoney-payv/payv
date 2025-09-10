package com.eggmoney.payv.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eggmoney.payv.application.service.LedgerAppService;
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
	
	private final UserId testUserId = UserId.of("27115898-3e6f-4a7e-9baa-87605398b5b9");
	
	@GetMapping
    public String list(Model model) {
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
