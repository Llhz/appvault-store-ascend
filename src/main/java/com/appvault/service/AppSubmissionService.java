package com.appvault.service;

import com.appvault.dto.AppSubmissionDto;
import com.appvault.model.AppSubmission;

import java.util.List;

public interface AppSubmissionService {

    AppSubmission createSubmission(AppSubmissionDto dto, Long userId);

    AppSubmission submitForReview(Long submissionId, Long userId);

    AppSubmission approveSubmission(Long submissionId, String reviewNotes);

    AppSubmission rejectSubmission(Long submissionId, String reviewNotes);

    List<AppSubmission> getSubmissionsByUser(Long userId);

    List<AppSubmission> getPendingSubmissions();

    AppSubmission getSubmissionById(Long id);
}
