package com.appvault.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class AppListingDto {

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 200)
    private String subtitle;

    @NotBlank
    @Size(max = 5000)
    private String description;

    @NotBlank
    private String developer;

    private String version;
    private String size;
    private String iconUrl;
    private String headerImageUrl;

    @DecimalMin("0.00")
    private BigDecimal price = BigDecimal.ZERO;

    private Long categoryId;

    private String ageRating;
    private String compatibility;

    private boolean featured;

    private List<String> screenshotUrls = new ArrayList<>();
    private List<String> screenshotCaptions = new ArrayList<>();
}
