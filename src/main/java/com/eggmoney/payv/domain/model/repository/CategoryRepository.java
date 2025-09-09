package com.eggmoney.payv.domain.model.repository;

import java.util.List;
import java.util.Optional;

import com.eggmoney.payv.domain.model.entity.Category;
import com.eggmoney.payv.domain.model.vo.CategoryId;
import com.eggmoney.payv.domain.model.vo.LedgerId;

/**
 * 카테고리 레포지토리
 * @author 정의탁
 */
public interface CategoryRepository {

	Optional<Category> findById(CategoryId id);
	
	// 한 가계부에 동일한 이름을 갖는 카테고리가 존재하는지 확인.
	Optional<Category> findByLedgerAndName(LedgerId ledgerId, String name);
    
	List<Category> findListByLedger(LedgerId ledgerId);
	
	// UPSERT = 새 UUID면 insert, 아니면 update
    void save(Category category);
    
    // 소프트 삭제.
    void delete(LedgerId ledgerId, CategoryId id);
    
    // 자식 일괄 소프트 삭제.
    // void deleteChildren(LedgerId ledgerId, CategoryId parentId);
}
