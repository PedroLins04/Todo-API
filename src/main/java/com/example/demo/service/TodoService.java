package com.example.demo.service;

import com.example.demo.DTO.todo.TodoRequestDTO;
import com.example.demo.DTO.todo.TodoResponseDTO;
import com.example.demo.entity.Todo;
import com.example.demo.entity.User;
import com.example.demo.repository.TodoRepository;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoService(TodoRepository todoRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    private String getAuthenticatedEmail() {
        return Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
    }

    TodoResponseDTO responseDTO(Todo todo) {
        return new TodoResponseDTO(todo.getId(), todo.getName(), todo.getDescription(), todo.isDone(), todo.getPriority());
    }

    @Transactional
    public List<TodoResponseDTO> create(TodoRequestDTO requestDTO) {

        String email = getAuthenticatedEmail();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Todo todo = new Todo(requestDTO.name(), requestDTO.description(), requestDTO.done(), requestDTO.priority());

        todo.setUser(user);
        todoRepository.save(todo);

        return list();
    }

    public List<TodoResponseDTO> list() {
        String email = getAuthenticatedEmail();

        return todoRepository
                .findAllByUser_EmailOrderByPriorityDescNameAsc(email)
                .stream()
                .map(this::responseDTO)
                .toList();
    }
    public Todo findById(Long id) {
        return getTodoOrThrow(id);
    }

    @Transactional
    public List<TodoResponseDTO> update(Long id, TodoRequestDTO requestDTO) {
        Todo obj = getTodoOrThrow(id);

        obj.setName(requestDTO.name());
        obj.setDescription(requestDTO.description());
        obj.setDone(requestDTO.done());
        obj.setPriority(requestDTO.priority());

        todoRepository.save(obj);

        return list();
    }

    @Transactional
    public List<TodoResponseDTO> delete(Long id) {
        Todo todo = getTodoOrThrow(id);

        todoRepository.delete(todo);

        return list();
    }

    private Todo getTodoOrThrow(Long id) {
        String email = getAuthenticatedEmail();

        return todoRepository.findByIdAndUser_Email(id, email).orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada"));
    }
}
