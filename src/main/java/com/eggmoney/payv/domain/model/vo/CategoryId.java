package com.eggmoney.payv.domain.model.vo;

import java.util.Objects;

import com.eggmoney.payv.domain.shared.id.LongId;
import com.eggmoney.payv.domain.shared.util.EntityIdentifier;

public class CategoryId implements LongId {

	private final long value;
	
	private CategoryId(long value) {
		this.value = EntityIdentifier.positive(value, "categoryId");
    }

	public static CategoryId of(long value) {
		return new CategoryId(value);
	}

	public long value() {
		return value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public String toString() {
		return Long.toString(value);
	}
	
	@Override
	public boolean equals(Object o) {
		return (this == o) || (o instanceof CategoryId && value == ((CategoryId) o).value);
	}
}
