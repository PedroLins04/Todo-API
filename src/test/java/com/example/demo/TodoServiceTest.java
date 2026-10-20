package com.example.demo;

import com.example.demo.DTO.todo.TodoRequestDTO;
import com.example.demo.DTO.todo.TodoResponseDTO;
import com.example.demo.entity.Todo;
import com.example.demo.entity.User;
import com.example.demo.repository.TodoRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TodoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TodoService todoService;

    private User user;

    @BeforeEach
    void setUp() {
        user = mock(User.class);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "pete@gmail.com", null
                )
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void CreateTaskForClient() {
        when(userRepository.findByEmail("pete@gmail.com"))
                .thenReturn(Optional.of(user));

        when(todoRepository
                .findAllByUser_Email("pete@gmail.com"))
                .thenReturn(List.of());

        when(todoRepository.save(any(Todo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TodoRequestDTO request = new TodoRequestDTO(
                "Estudar Java", "Estudar Mockito", false, 1
        );

        todoService.create(request);

        ArgumentCaptor<Todo> captor =
                ArgumentCaptor.forClass(Todo.class);

        verify(todoRepository).save(captor.capture());

        assertSame(user, captor.getValue().getUser());
        assertEquals("Estudar Java", captor.getValue().getName());
        verify(userRepository).findByEmail("pete@gmail.com");
    }

    @Test
    void FailWhenUserNotFound() {
        when(userRepository.findByEmail("pete@gmail.com"))
                .thenReturn(Optional.empty());

        TodoRequestDTO request = new TodoRequestDTO(
                "Estudar Java", "Estudar Mockito", false, 1
        );

        assertThrows(
                EntityNotFoundException.class,
                () -> todoService.create(request)
        );

        verify(todoRepository, never()).save(any(Todo.class));
    }

    @Test
    void ListUserTasks() {
        Todo todo = new Todo(
                "Study",
                "mockito",
                false,
                1);

        when(todoRepository.findAllByUser_Email("pete@gmail.com"))
                .thenReturn(List.of(todo));

        List<TodoResponseDTO> result = todoService.list();

        assertEquals(1, result.size());

        verify(todoRepository).findAllByUser_Email("pete@gmail.com");
    }

    @Test
    void EmptyList() {
        when(
                todoRepository.findAllByUser_Email("pete@gmail.com"))
                .thenReturn(List.of());
        List<TodoResponseDTO> result = todoService.list();
        assertTrue(result.isEmpty());
    }

    @Test
    void TaskNotFound() {
        when(todoRepository.findByIdAndUser_Email(10L, "pete@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> todoService.findById(10L));
    }

    @Test
    void DeleteOnlyUserTask() {
        when(todoRepository.findByIdAndUser_Email(10L, "pete@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> todoService.delete(10L)
        );

        verify(todoRepository, never()).delete(any());
    }

    @Test
    void DtoConvert() {
        Todo todo = new Todo(
                "study",
                "mockito",
                false,
                1);

        when(todoRepository.findAllByUser_Email("pete@gmail.com"))
                .thenReturn(List.of(todo));
        List<TodoResponseDTO> result = todoService.list();
        assertEquals(1, result.size());

        assertEquals("study", result.getFirst().name());
        assertEquals("mockito", result.getFirst().description());
        assertFalse(result.getFirst().done());
        assertEquals(1, result.getFirst().priority());
    }
}
