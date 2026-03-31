package com.appvault.repository;

import com.appvault.model.AppSubmission;
import com.appvault.model.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppSubmissionRepository extends JpaRepository<AppSubmission, Long> {

    List<AppSubmission> findBySubmitterIdOrderByCreatedAtDesc(Long userId);

    List<AppSubmission> findByStatusOrderByCreatedAtAsc(SubmissionStatus status);

    long countByStatus(SubmissionStatus status);
}
