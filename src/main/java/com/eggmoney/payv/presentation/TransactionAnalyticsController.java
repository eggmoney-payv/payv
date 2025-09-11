package com.eggmoney.payv.presentation;

import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eggmoney.payv.application.service.CategoryAppService;
import com.eggmoney.payv.application.service.TransactionAppService;
import com.eggmoney.payv.domain.model.entity.Category;
import com.eggmoney.payv.domain.model.entity.Transaction;
import com.eggmoney.payv.domain.model.entity.TransactionType;
import com.eggmoney.payv.domain.model.vo.LedgerId;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ledgers/{ledgerId}/insights")
public class TransactionAnalyticsController {

	private final TransactionAppService transactionAppService;
    private final CategoryAppService categoryAppService;
    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson 사용
    
    /** 월별 수입/지출(세로 막대형) */
    @GetMapping("/monthly")
    public String monthly(@PathVariable String ledgerId,
                          @RequestParam(value = "year", required = false) String yearParam,
                          Model model) throws Exception {

    	LedgerId lId = LedgerId.of(ledgerId);
        Year year = (yearParam == null || yearParam.trim().isEmpty())
                ? Year.now()
                : Year.parse(yearParam.trim()); // "YYYY"

        // 1~12월 루프 돌며 합계 산출.
        long[] income = new long[12];
        long[] expense = new long[12];

        for (int m = 1; m <= 12; m++) {
            YearMonth ym = YearMonth.of(year.getValue(), m);
            List<Transaction> txns = transactionAppService.listByMonth(lId, ym, Integer.MAX_VALUE, 0);
            long in = 0L, out = 0L;
            for (Transaction t : txns) {
                long won = t.getAmount().toLong(); // Money -> long
                if (t.getType() == TransactionType.INCOME) in += won;
                else out += won;
            }
            income[m - 1] = in;
            expense[m - 1] = out;
        }

        // Google Charts arrayToDataTable 형식으로 데이터 구성.
        // ex) [ ["월","수입","지출"], ["1월", 1000, 800], ... ]
        List<List<Object>> rows = new ArrayList<>();
        rows.add(Arrays.asList("월", "수입", "지출"));
        for (int m = 1; m <= 12; m++) {
            rows.add(Arrays.asList(m + "월", income[m - 1], expense[m - 1]));
        }
        String chartDataJson = objectMapper.writeValueAsString(rows);
        
        long sumIn = Arrays.stream(income).sum();
        long sumOut = Arrays.stream(expense).sum();
        
        model.addAttribute("ledgerId", ledgerId);
        model.addAttribute("year", String.valueOf(year.getValue()));
        model.addAttribute("chartDataJson", chartDataJson);
        model.addAttribute("sumIncome", sumIn);
        model.addAttribute("sumExpense", sumOut);
        model.addAttribute("prevYear", String.valueOf(year.minusYears(1).getValue()));
        model.addAttribute("nextYear", String.valueOf(year.plusYears(1).getValue()));
        return "insights/monthly";
    }

    /** 카테고리별 지출(도넛 차트) - month=YYYY-MM */
    @GetMapping("/categories")
    public String categories(@PathVariable String ledgerId,
                             @RequestParam(value = "month", required = false) String monthParam,
                             Model model) throws Exception {
    	
        LedgerId lId = LedgerId.of(ledgerId);

        YearMonth ym = (monthParam == null || monthParam.trim().isEmpty())
                ? YearMonth.now()
                : YearMonth.parse(monthParam.trim()); // "YYYY-MM"

        // 1) 해당 (달)월에 포함된 거래 내역 목록.
        List<Transaction> txns = transactionAppService.listByMonth(lId, ym, Integer.MAX_VALUE, 0);
        
        // 2) 카테고리 전체(루트/하위) 목록.
        List<Category> categories = categoryAppService.listByLedger(lId);
        
        // catById = {id, Category}
        Map<String, Category> catById = categories.stream()
                .collect(Collectors.toMap(c -> c.getId().toString(), c -> c));
        
        // 3) root 카테고리만 필터링(상위 카테고리가 NULL인 카테고리).
        Map<String, String> rootNameById = categories.stream()
                .filter(c -> c.getParentId() == null)
                .collect(Collectors.toMap(c -> c.getId().toString(), Category::getName));

        /**
         * 4) 
         * 각 카테고리의 루트(최상위) 카테고리 매핑.(최대 2-Depth 이지만, 방어적으로 다단계도 처리)
         * toRoodId = { 각 카테고리 id, key에 해당하는 카테고리의 최상위 카테고리 id }
         */
        Map<String, String> toRootId = new HashMap<>();
        for (Category c : categories) {
            String id = c.getId().toString();
            if (c.getParentId() == null) {
                toRootId.put(id, id);	// 루트 자신
            } else {
                // 부모를 따라 올라가 루트 찾기.
                String pid = c.getParentId().toString();
                Category p = catById.get(pid);
                while (p != null && p.getParentId() != null) {
                    pid = p.getParentId().toString();
                    p = catById.get(pid);
                }
                toRootId.put(id, (p != null ? p.getId().toString() : id));
            }
        }
        
        // 5) 지출만 루트 기준으로 합산.
        Map<String, Long> expenseByRoot = new HashMap<>();
        for (Transaction t : txns) {
            if (t.getType() == TransactionType.INCOME) continue; // 지출만
            String catId = t.getCategoryId().toString();
            String rootId = toRootId.getOrDefault(catId, catId); // 매핑 없으면 자기 자신.
            long won = t.getAmount().toLong();
            expenseByRoot.merge(rootId, won, Long::sum);
        }
        
        /**
         * 6) Google Charts 도넛 데이터 구성 (루트명만 표기)
         * - ex) [ ["카테고리","지출"], ["식비", 12345], ... ]
         * - 정렬: 금액 내림차순
         */
        List<List<Object>> rows = new ArrayList<>();
        rows.add(Arrays.asList("카테고리", "지출"));
        expenseByRoot.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .forEach(e -> {
                    String rootId = e.getKey();
                    String name = rootNameById.getOrDefault(rootId, rootId);
                    rows.add(Arrays.asList(name, e.getValue()));
                });

        String pieDataJson = objectMapper.writeValueAsString(rows);
        long totalOut = expenseByRoot.values().stream().mapToLong(Long::longValue).sum();

        model.addAttribute("ledgerId", ledgerId);
        model.addAttribute("month", ym.toString());
        model.addAttribute("pieDataJson", pieDataJson);
        model.addAttribute("totalExpense", totalOut);
        return "insights/categories";
    }
}
