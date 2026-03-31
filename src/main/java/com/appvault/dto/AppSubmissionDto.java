package com.appvault.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class AppSubmissionDto {

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 200)
    private String subtitle;

    @NotBlank
    @Size(max = 5000)
    private String description;

    private String developer;

    private String iconUrl;

    @DecimalMin("0.00")
    private BigDecimal price = BigDecimal.ZERO;

    private Long categoryId;
}
