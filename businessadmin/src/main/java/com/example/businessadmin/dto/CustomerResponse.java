package com.example.businessadmin.dto;

import java.time.LocalDateTime;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CustomerResponse {
	private Long id;
	private String name;
	private String email;
	private String phone;
    private String address;
	private Boolean active;
	private LocalDateTime createdAt;
}
