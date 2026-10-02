package com.prepForge.prepForge_backend.Service;

import com.prepForge.prepForge_backend.dto.AddQuestionRequest;
import com.prepForge.prepForge_backend.dto.UpdateQuestionRequest;
import com.prepForge.prepForge_backend.entity.Question;
import com.prepForge.prepForge_backend.entity.User;
import com.prepForge.prepForge_backend.repository.ProgressRepository;
import com.prepForge.prepForge_backend.repository.QuestionRepository;
import com.prepForge.prepForge_backend.repository.UserRepository;
import com.prepForge.prepForge_backend.service.AuthenticationService;
import com.prepForge.prepForge_backend.service.QuestionService;
import enums.Difficulty;
import enums.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private ProgressRepository progressRepository;

    @InjectMocks
    private QuestionService questionService;

    @Test
    void getQuestion_shouldReturnQuestionForCurrentUser() {
        User user = new User();
        Question question = new Question();
        question.setId(1L);

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(questionRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(question));

        Question result = questionService.getQuestion(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(questionRepository).findByIdAndUser(1L, user);
    }

    @Test
    void getQuestion_shouldThrowExceptionWhenQuestionNotFound() {
        User user = new User();

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(questionRepository.findByIdAndUser(99L, user))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> questionService.getQuestion(99L)
        );

        assertEquals("Question not found", exception.getMessage());
    }

    @Test
    void getQuestions_shouldReturnUserQuestions() {
        User user = new User();
        Question question = new Question();

        PageRequest pageable = PageRequest.of(0, 10);
        Page<Question> expectedPage =
                new PageImpl<>(List.of(question));

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(questionRepository.findByUser(user, pageable))
                .thenReturn(expectedPage);

        Page<Question> result =
                questionService.getQuestions(
                        pageable,
                        null,
                        null,
                        null
                );

        assertEquals(1, result.getTotalElements());
        assertSame(question, result.getContent().get(0));

        verify(questionRepository).findByUser(user, pageable);
    }

    @Test
    void addQuestion_shouldSaveQuestionAndProgress() {
        User user = new User();

        AddQuestionRequest request = new AddQuestionRequest();
        request.setTitle("Two Sum");
        request.setDifficulty(Difficulty.EASY);
        request.setTopic("Arrays");
        request.setLink("https://leetcode.com/problems/two-sum/");
        request.setStatus(Status.SOLVED);

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(questionRepository.findByLinkAndUser(request.getLink(), user))
                .thenReturn(Optional.empty());

        Question savedQuestion = new Question();
        savedQuestion.setId(1L);

        when(questionRepository.save(any(Question.class)))
                .thenReturn(savedQuestion);

        Question result = questionService.addQuestion(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(questionRepository).save(any(Question.class));
        verify(progressRepository).save(any());
    }

    @Test
    void addQuestion_shouldRejectDuplicateQuestion() {
        User user = new User();
        AddQuestionRequest request = new AddQuestionRequest();

        request.setLink("https://leetcode.com/problems/two-sum/");

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(questionRepository.findByLinkAndUser(request.getLink(), user))
                .thenReturn(Optional.of(new Question()));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> questionService.addQuestion(request)
        );

        assertEquals("Question already exists.", exception.getMessage());

        verify(questionRepository, never()).save(any());
        verify(progressRepository, never()).save(any());
    }

    @Test
    void updateQuestion_shouldUpdateAndSaveQuestion() {
        User user = new User();
        Question question = new Question();
        question.setId(1L);

        UpdateQuestionRequest request = new UpdateQuestionRequest();
        request.setTitle("Updated Two Sum");
        request.setDifficulty(Difficulty.MEDIUM);
        request.setTopic("Arrays");
        request.setLink("https://leetcode.com/problems/two-sum/");
        request.setStatus(Status.SOLVED);

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(questionRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(question));
        when(questionRepository.save(question))
                .thenReturn(question);

        Question result =
                questionService.updateQuestion(1L, request);

        assertEquals("Updated Two Sum", result.getTitle());
        assertEquals(Difficulty.MEDIUM, result.getDifficulty());
        assertEquals("Arrays", result.getTopic());

        verify(questionRepository).save(question);
    }

    @Test
    void deleteQuestion_shouldDeleteExistingQuestion() {
        User user = new User();
        Question question = new Question();
        question.setId(1L);

        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(questionRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(question));

        questionService.deleteQuestion(1L);

        verify(questionRepository).delete(question);
    }
}