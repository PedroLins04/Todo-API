package com.example.demo.controller;

import com.example.demo.DTO.todo.TodoRequestDTO;
import com.example.demo.DTO.todo.TodoResponseDTO;
import com.example.demo.entity.Todo;
import com.example.demo.service.TodoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping
    List<TodoResponseDTO> create(@RequestBody TodoRequestDTO requestDTO) {
        return todoService.create(requestDTO);
    }

    @GetMapping
    List<TodoResponseDTO> list() {
        return todoService.list();
    }

    @GetMapping("{id}")
    TodoResponseDTO findById(@PathVariable Long id) {
        Todo todo = todoService.findById(id);

        return new TodoResponseDTO(
                todo.getId(),
                todo.getName(),
                todo.getDescription(),
                todo.isDone(),
                todo.getPriority()
        );
    }

    @PutMapping("{id}")
    List<TodoResponseDTO> update(@PathVariable Long id, @RequestBody TodoRequestDTO requestDTO) {
        return todoService.update(id, requestDTO);
    }

    @DeleteMapping("{id}")
    List<TodoResponseDTO> delete(@PathVariable Long id) {
        return todoService.delete(id);
    }

}
