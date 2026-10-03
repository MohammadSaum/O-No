package com.prepForge.prepForge_backend.service;

import com.prepForge.prepForge_backend.entity.Progress;
import com.prepForge.prepForge_backend.repository.ProgressRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class RevisionScheduler {

    private final ProgressRepository progressRepository;

    public RevisionScheduler(ProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void checkDueRevisions() {

        List<Progress> dueRevisions =
                progressRepository.findByNextRevisionLessThanEqual(LocalDate.now());

        System.out.println(
                "Scheduled revision check completed. "
                        + dueRevisions.size()
                        + " revision(s) due."
        );

        dueRevisions.forEach(progress ->
                System.out.println(
                        "Revision due for question ID: "
                                + progress.getQuestion().getId()
                )
        );
    }
}