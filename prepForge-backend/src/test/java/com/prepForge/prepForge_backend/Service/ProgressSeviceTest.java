package com.prepForge.prepForge_backend.Service;

import com.prepForge.prepForge_backend.dto.UpdateConfidenceRequest;
import com.prepForge.prepForge_backend.entity.Progress;
import com.prepForge.prepForge_backend.entity.Question;
import com.prepForge.prepForge_backend.entity.User;
import com.prepForge.prepForge_backend.repository.ProgressRepository;
import com.prepForge.prepForge_backend.repository.QuestionRepository;
import com.prepForge.prepForge_backend.service.AuthenticationService;
import com.prepForge.prepForge_backend.service.ProgressService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock
    private ProgressRepository progressRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private ProgressService progressService;


    @Test
    void toggleFavorite_shouldToggleFavorite() {
        User user = new User();

        Question question = new Question();
        question.setId(1L);

        Progress progress = new Progress();
        progress.setFavorite(false);
        progress.setQuestion(question);

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(questionRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(question));
        when(progressRepository.findByQuestion(question))
                .thenReturn(Optional.of(progress));
        when(progressRepository.save(progress))
                .thenReturn(progress);

        Progress result = progressService.toggleFavorite(1L);

        assertTrue(result.isFavorite());

        verify(progressRepository).save(progress);
    }


    @Test
    void toggleFavorite_shouldThrowExceptionWhenQuestionNotFound() {
        User user = new User();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(questionRepository.findByIdAndUser(99L, user))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> progressService.toggleFavorite(99L)
        );

        assertEquals("Question not found", exception.getMessage());

        verify(progressRepository, never()).findByQuestion(any());
        verify(progressRepository, never()).save(any());
    }


    @Test
    void toggleFavorite_shouldThrowExceptionWhenProgressNotFound() {
        User user = new User();

        Question question = new Question();
        question.setId(1L);

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(questionRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(question));
        when(progressRepository.findByQuestion(question))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> progressService.toggleFavorite(1L)
        );

        assertEquals("Progress not found", exception.getMessage());

        verify(progressRepository, never()).save(any());
    }


    @Test
    void updateConfidence_shouldUpdateConfidence() {
        User user = new User();

        Question question = new Question();
        question.setId(1L);

        Progress progress = new Progress();
        progress.setConfidence(2);
        progress.setQuestion(question);

        UpdateConfidenceRequest request = new UpdateConfidenceRequest();
        request.setConfidence(5);

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(questionRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(question));
        when(progressRepository.findByQuestion(question))
                .thenReturn(Optional.of(progress));
        when(progressRepository.save(progress))
                .thenReturn(progress);

        Progress result = progressService.updateConfidence(1L, request);

        assertEquals(5, result.getConfidence());

        verify(progressRepository).save(progress);
    }


    @Test
    void reviseQuestion_shouldUpdateRevisionData() {
        User user = new User();

        Question question = new Question();
        question.setId(1L);

        Progress progress = new Progress();
        progress.setRevisionCount(2);
        progress.setQuestion(question);

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(questionRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(question));
        when(progressRepository.findByQuestion(question))
                .thenReturn(Optional.of(progress));
        when(progressRepository.save(progress))
                .thenReturn(progress);

        LocalDate before = LocalDate.now();

        Progress result = progressService.reviseQuestion(1L);

        assertEquals(3, result.getRevisionCount());
        assertEquals(LocalDate.now(), result.getLastSolved());
        assertEquals(before.plusDays(7), result.getNextRevision());

        verify(progressRepository).save(progress);
    }


    @Test
    void getProgress_shouldReturnProgressForQuestion() {
        User user = new User();

        Question question = new Question();
        question.setId(1L);

        Progress progress = new Progress();
        progress.setQuestion(question);

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(questionRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(question));
        when(progressRepository.findByQuestion(question))
                .thenReturn(Optional.of(progress));

        Progress result = progressService.getProgress(1L);

        assertNotNull(result);
        assertSame(progress, result);

        verify(progressRepository).findByQuestion(question);
    }


    @Test
    void getProgress_shouldThrowExceptionWhenQuestionNotFound() {
        User user = new User();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(questionRepository.findByIdAndUser(99L, user))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> progressService.getProgress(99L)
        );

        assertEquals("Question not found", exception.getMessage());
    }


    @Test
    void getProgress_shouldThrowExceptionWhenProgressNotFound() {
        User user = new User();

        Question question = new Question();
        question.setId(1L);

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(questionRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(question));
        when(progressRepository.findByQuestion(question))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> progressService.getProgress(1L)
        );

        assertEquals("Progress not found", exception.getMessage());
    }
}