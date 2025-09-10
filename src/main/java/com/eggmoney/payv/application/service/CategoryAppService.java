package com.eggmoney.payv.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eggmoney.payv.domain.model.entity.Budget;
import com.eggmoney.payv.domain.model.entity.Category;
import com.eggmoney.payv.domain.model.repository.CategoryRepository;
import com.eggmoney.payv.domain.model.vo.BudgetId;
import com.eggmoney.payv.domain.model.vo.CategoryId;
import com.eggmoney.payv.domain.model.vo.LedgerId;
import com.eggmoney.payv.domain.shared.error.DomainException;

import lombok.RequiredArgsConstructor;

/**
 * 카테고리 애플리케이션 서비스
 * @author 정의탁
 */
@Service
@RequiredArgsConstructor
public class CategoryAppService {

	private final CategoryRepository categoryRepository;
	
	// 루트 생성(depth=1)
    public Category createRoot(LedgerId ledgerId, String name, boolean isSystem, int sortOrder) {
        ensureUniqueName(ledgerId, name);
        Category category = Category.createRoot(ledgerId, name, isSystem, sortOrder);
        categoryRepository.save(category);
        return category;
    }
    
    // 자식 생성(depth=2) : 부모는 반드시 루트여야 함.
    public Category createChild(LedgerId ledgerId, CategoryId parentId, String name, boolean system, int sortOrder) {
        if (parentId == null) {
        	throw new IllegalArgumentException("parentId is required");
        }
        
        Category parent = categoryRepository.findById(parentId).orElseThrow(
        		() -> new DomainException("parent not found"));

        // 동일한 가계부에 속한 카테고리인지 확인.
        if (!parent.getLedgerId().equals(ledgerId)) {
            throw new DomainException("parent must be in the same ledger");
        }
        
        // 부모가 루트가 아니면, 3단계 이상임으로 금지.
        if (!parent.isRoot()) {
            throw new DomainException("카테고리 최대 깊이는 '2' 입니다.");
        }
        
        ensureUniqueName(ledgerId, name);
        Category category = Category.createChild(ledgerId, parentId, name, system, sortOrder);
        categoryRepository.save(category);
        return category;
    }
    
    // 이름 변경 (동일 가계부 내 중복 방지)
    public void rename(CategoryId id, LedgerId ledgerId, String newName) {
        Category category = categoryRepository.findById(id)
        		.orElseThrow(() -> new DomainException("category not found"));
        
        if (!category.getLedgerId().equals(ledgerId)) {
        	throw new DomainException("ledger mismatch");
        }
        
        // 이름이 바뀌는 경우에만 중복 확인.
        if (!category.getName().equals(newName)) {
        	ensureUniqueName(ledgerId, newName);
        }
        
        category.rename(newName);
        categoryRepository.save(category);
    }
    
    // 카테고리 소프트 삭제(root 카테고리 삭제 시, 하위 카테고리 모두 삭제).
    @Transactional
    public void delete(LedgerId ledgerId, CategoryId categoryId) {
    	Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DomainException("category not found"));
    	
    	if (!category.getLedgerId().equals(ledgerId)) {
    		throw new DomainException("ledger mismatch");
    	}
    	
    	categoryRepository.delete(ledgerId, categoryId);
    }
    
    // 특정 가계부 내의 카테고리 목록 조회.
    @Transactional(readOnly = true)
    public List<Category> listByLedger(LedgerId ledgerId) {
    	return categoryRepository.findListByLedger(ledgerId);
    }
    
    // 카테고리 상세 조회.
    @Transactional
	public Category getDetails(CategoryId categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DomainException("category not found"));
    }
    
    // ----
    private void ensureUniqueName(LedgerId ledgerId, String name) {
    	categoryRepository.findByLedgerAndName(ledgerId, name.trim()).ifPresent(x -> {
            throw new DomainException("동일한 카테고리 이름이 이미 존재합니다.");
        });
    }
}
