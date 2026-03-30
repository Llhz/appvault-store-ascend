package com.appvault.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppSuggestDto {
    private Long id;
    private String name;
    private String iconUrl;
    private String categoryName;
}
