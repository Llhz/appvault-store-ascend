package com.appvault.dto;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class ReviewDto {

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Size(max = 2000)
    private String content;

    @Min(1)
    @Max(5)
    @NotNull
    private Integer rating;
}
