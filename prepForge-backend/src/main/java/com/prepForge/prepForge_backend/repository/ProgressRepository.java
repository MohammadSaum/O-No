package com.prepForge.prepForge_backend.repository;

import com.prepForge.prepForge_backend.entity.Progress;
import com.prepForge.prepForge_backend.entity.Question;
import com.prepForge.prepForge_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress, Long> {

    Optional<Progress> findByQuestion(Question question);

    long countByFavoriteTrueAndQuestion_User(User user);

    long countByNextRevisionLessThanEqualAndQuestion_User(
            LocalDate date,
            User user
    );

    List<Progress> findByNextRevisionLessThanEqual(LocalDate date);

}
