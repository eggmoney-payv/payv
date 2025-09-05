package com.eggmoney.payv.domain.model.vo;

import java.util.Objects;

import com.eggmoney.payv.domain.shared.id.StringId;
import com.eggmoney.payv.domain.shared.util.EntityIdentifier;

/**
 * Value Object: ReactionId
 * - 책임: Reaction 엔티티를 구분하는 식별자.
 * - 내부적으로 String 값(value)을 보관.
 *  * - null/blank 값 불가.
 * 
 * 팩토리 메서드:
 *   - of(String value): 외부에서 식별자 문자열을 받아 ReactionId로 감쌈.
 * 
 * equals/hashCode:
 *   - 값 객체(value)로 동등성 비교
 * 
 * @author 한지원
 *
 */
public final class ReactionId implements StringId {

    private final String value;

    private ReactionId(String value) {
        this.value = EntityIdentifier.nonBlank(value, "ReactionId");
    }

    public static ReactionId of(String value) {
        return new ReactionId(value);
    }

    public String value() {
        return value;
    }
    
    // JSP EL이 프로퍼티로 인식할 수 있게 getValue() 추가
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ReactionId) && ((ReactionId) o).value.equals(this.value);
    }
}
