package com.appvault.service;

import com.appvault.dto.AppSubmissionDto;
import com.appvault.model.AppSubmission;
import com.appvault.model.SubmissionStatus;
import com.appvault.model.User;
import com.appvault.repository.AppListingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AppSubmissionServiceTest {

    @Autowired
    private AppSubmissionService appSubmissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private AppListingRepository appListingRepository;

    @Test
    void createSubmissionSavesAsDraft() {
        User developer = userService.findByEmail("developer@appvault.com").orElseThrow();

        AppSubmissionDto dto = new AppSubmissionDto();
        dto.setName("Test App");
        dto.setDescription("A test application description");
        dto.setDeveloper("Test Developer");
        dto.setPrice(BigDecimal.ZERO);

        AppSubmission submission = appSubmissionService.createSubmission(dto, developer.getId());

        assertNotNull(submission.getId());
        assertEquals("Test App", submission.getName());
        assertEquals(SubmissionStatus.DRAFT, submission.getStatus());
        assertEquals(developer.getId(), submission.getSubmitter().getId());
    }

    @Test
    void submitForReviewChangesStatus() {
        User developer = userService.findByEmail("developer@appvault.com").orElseThrow();

        AppSubmissionDto dto = new AppSubmissionDto();
        dto.setName("Review Test App");
        dto.setDescription("Description for review test");

        AppSubmission submission = appSubmissionService.createSubmission(dto, developer.getId());
        AppSubmission submitted = appSubmissionService.submitForReview(submission.getId(), developer.getId());

        assertEquals(SubmissionStatus.PENDING_REVIEW, submitted.getStatus());
    }

    @Test
    void approveSubmissionCreatesAppListing() {
        User developer = userService.findByEmail("developer@appvault.com").orElseThrow();
        long appCountBefore = appListingRepository.count();

        AppSubmissionDto dto = new AppSubmissionDto();
        dto.setName("Approved App");
        dto.setDescription("This app will be approved");
        dto.setDeveloper("Test Dev");
        dto.setPrice(new BigDecimal("1.99"));

        AppSubmission submission = appSubmissionService.createSubmission(dto, developer.getId());
        appSubmissionService.submitForReview(submission.getId(), developer.getId());
        AppSubmission approved = appSubmissionService.approveSubmission(submission.getId(), "Looks great!");

        assertEquals(SubmissionStatus.APPROVED, approved.getStatus());
        assertEquals("Looks great!", approved.getReviewNotes());
        assertEquals(appCountBefore + 1, appListingRepository.count());
    }

    @Test
    void rejectSubmissionSetsNotesAndStatus() {
        User developer = userService.findByEmail("developer@appvault.com").orElseThrow();

        AppSubmissionDto dto = new AppSubmissionDto();
        dto.setName("Rejected App");
        dto.setDescription("This app will be rejected");

        AppSubmission submission = appSubmissionService.createSubmission(dto, developer.getId());
        appSubmissionService.submitForReview(submission.getId(), developer.getId());
        AppSubmission rejected = appSubmissionService.rejectSubmission(submission.getId(), "Needs improvement");

        assertEquals(SubmissionStatus.REJECTED, rejected.getStatus());
        assertEquals("Needs improvement", rejected.getReviewNotes());
    }

    @Test
    void rejectedSubmissionCanBeResubmitted() {
        User developer = userService.findByEmail("developer@appvault.com").orElseThrow();

        AppSubmissionDto dto = new AppSubmissionDto();
        dto.setName("Resubmit App");
        dto.setDescription("This app will be resubmitted");

        AppSubmission submission = appSubmissionService.createSubmission(dto, developer.getId());
        appSubmissionService.submitForReview(submission.getId(), developer.getId());
        appSubmissionService.rejectSubmission(submission.getId(), "Fix issues");

        AppSubmission resubmitted = appSubmissionService.submitForReview(submission.getId(), developer.getId());
        assertEquals(SubmissionStatus.PENDING_REVIEW, resubmitted.getStatus());
        assertNull(resubmitted.getReviewNotes());
    }

    @Test
    void getSubmissionsByUserReturnsDeveloperSubmissions() {
        User developer = userService.findByEmail("developer@appvault.com").orElseThrow();
        List<AppSubmission> submissions = appSubmissionService.getSubmissionsByUser(developer.getId());
        assertNotNull(submissions);
        assertFalse(submissions.isEmpty());
    }

    @Test
    void getPendingSubmissionsReturnsPendingOnly() {
        List<AppSubmission> pending = appSubmissionService.getPendingSubmissions();
        assertNotNull(pending);
        pending.forEach(s -> assertEquals(SubmissionStatus.PENDING_REVIEW, s.getStatus()));
    }

    @Test
    void approveAlreadyApprovedThrowsException() {
        User developer = userService.findByEmail("developer@appvault.com").orElseThrow();

        AppSubmissionDto dto = new AppSubmissionDto();
        dto.setName("Double Approve");
        dto.setDescription("Cannot approve twice");
        dto.setDeveloper("Test Dev Co");

        AppSubmission submission = appSubmissionService.createSubmission(dto, developer.getId());
        appSubmissionService.submitForReview(submission.getId(), developer.getId());
        appSubmissionService.approveSubmission(submission.getId(), "Approved");

        assertThrows(IllegalStateException.class, () ->
                appSubmissionService.approveSubmission(submission.getId(), "Approve again"));
    }

    @Test
    void submitOtherUsersSubmissionThrowsException() {
        User developer = userService.findByEmail("developer@appvault.com").orElseThrow();
        User otherUser = userService.findByEmail("user@appvault.com").orElseThrow();

        AppSubmissionDto dto = new AppSubmissionDto();
        dto.setName("Other User App");
        dto.setDescription("Submitting someone else's app");

        AppSubmission submission = appSubmissionService.createSubmission(dto, developer.getId());

        assertThrows(IllegalStateException.class, () ->
                appSubmissionService.submitForReview(submission.getId(), otherUser.getId()));
    }
}
