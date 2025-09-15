package com.eggmoney.payv.presentation.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardItemDto {

	private String id;
	private String userId;
	private String title;
	private String content;
	private String owner;
	private long viewCount;
	private LocalDateTime createdAt;
    private LocalDateTime updatedAt;	
}
