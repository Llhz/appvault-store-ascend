package com.appvault.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "app_listings")
public class AppListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String name;

    @Size(max = 200)
    private String subtitle;

    @NotBlank
    @Size(max = 5000)
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank
    private String developer;

    private String version;

    private String size;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "header_image_url")
    private String headerImageUrl;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(nullable = false)
    private Double rating = 0.0;

    @Column(name = "review_count", nullable = false)
    private Integer reviewCount = 0;

    @Column(name = "download_count", nullable = false)
    private Long downloadCount = 0L;

    @Column(nullable = false)
    private boolean featured = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Category category;

    @OneToMany(mappedBy = "appListing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "appListing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Screenshot> screenshots = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private String compatibility;

    @Column(name = "age_rating")
    private String ageRating;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
