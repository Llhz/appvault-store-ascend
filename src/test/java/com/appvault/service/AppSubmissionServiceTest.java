package com.appvault.service;

import com.appvault.dto.AppSubmissionDto;
import com.appvault.model.AppSubmission;
import com.appvault.model.SubmissionStatus;
import com.appvault.model.User;
import com.appvault.repository.CategoryRepository;
import com.appvault.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AppSubmissionServiceTest {

    @Autowired private AppSubmissionService submissionService;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;

    private User developer;

    @BeforeEach
    void setUp() {
        developer = userRepository.findByEmail("developer@appvault.com").orElseThrow(() -> new IllegalStateException("Seed developer missing"));
    }

    @Test
    void submissionLifecycleCreatesListingOnApproval() {
        AppSubmissionDto dto = new AppSubmissionDto();
        dto.setName("Test App");
        dto.setSubtitle("Test Subtitle");
        dto.setDescription("Test description for approval path.");
        dto.setDeveloper("Test Devs");
        dto.setIconUrl("https://placehold.co/100");
        dto.setPrice(new BigDecimal("1.99"));
        dto.setCategoryId(categoryRepository.findAll().get(0).getId());

        AppSubmission submission = submissionService.createSubmission(dto, developer.getId());
        assertEquals(SubmissionStatus.DRAFT, submission.getStatus());

        submission = submissionService.submitForReview(submission.getId(), developer.getId());
        assertEquals(SubmissionStatus.PENDING_REVIEW, submission.getStatus());

        submission = submissionService.approveSubmission(submission.getId(), "Looks good");
        assertEquals(SubmissionStatus.APPROVED, submission.getStatus());
        assertNotNull(submission.getAppListing());
        assertEquals(dto.getName(), submission.getAppListing().getName());
    }
}
