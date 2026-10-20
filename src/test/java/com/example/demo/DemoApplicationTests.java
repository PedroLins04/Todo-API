
package com.example.demo;

import com.example.demo.entity.Todo;
import com.example.demo.repository.TodoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class DemoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository todoRepository;

    private String token;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        todoRepository.deleteAll();

        String name = "testeUser";
        String email = "teste" + System.nanoTime() + "@gmail.com";
        String password = "SenhaTeste123!";

        // Registra um usuário para o teste.
        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": "%s",
                                    "email": "%s",
                                    "password": "%s"
                                }
                                """.formatted(name, email, password)))
                .andReturn();

        System.out.println("Status do cadastro: "
                + registerResult.getResponse().getStatus());

        System.out.println("Resposta do cadastro: "
                + registerResult.getResponse().getContentAsString());

        // Realiza login e obtém o JWT.
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": "%s",
                                    "email": "%s",
                                    "password": "%s"
                                }
                                """.formatted(name, email, password)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(
                result.getResponse().getContentAsString()
        );

        token = json.get("token").asText();
    }

    private String authorizationHeader() {
        return "Bearer " + token;
    }

    @Test
    void testCreateTodoSuccess() throws Exception {
        var todo = new Todo("todo 1", "description 1", false, 1);

        mockMvc.perform(post("/todos")
                        .header("Authorization", authorizationHeader())
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": "todo 1",
                                    "description": "description 1",
                                    "done": false,
                                    "priority": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value(todo.getName()))
                .andExpect(jsonPath("$[0].description").value(todo.getDescription()))
                .andExpect(jsonPath("$[0].done").value(todo.isDone()))
                .andExpect(jsonPath("$[0].priority").value(todo.getPriority()));
    }

    @Test
    void testCreateTodoFailure() throws Exception {
        mockMvc.perform(post("/todos")
                        .header("Authorization", authorizationHeader())
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": "todo 1",
                                    "description":
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFindTodoByIdSuccess() throws Exception {
        MvcResult result = mockMvc.perform(post("/todos")
                        .header("Authorization", authorizationHeader())
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": "Todo 2",
                                    "description": "Description 2",
                                    "done": false,
                                    "priority": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode response = objectMapper.readTree(
                result.getResponse().getContentAsString()
        );

        long id = response.get(0).get("id").asLong();

        mockMvc.perform(get("/todos/" + id)
                        .header("Authorization", authorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Todo 2"))
                .andExpect(jsonPath("$.description").value("Description 2"));
    }

    @Test
    void testFindTodoByIdFailure() throws Exception {
        mockMvc.perform(get("/todos/99999999")
                        .header("Authorization", authorizationHeader()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateTodoSuccess() throws Exception {
        MvcResult result = mockMvc.perform(post("/todos")
                        .header("Authorization", authorizationHeader())
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": "Todo",
                                    "description": "Description",
                                    "done": false,
                                    "priority": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode response = objectMapper.readTree(
                result.getResponse().getContentAsString()
        );

        long id = response.get(0).get("id").asLong();

        MvcResult updateResult = mockMvc.perform(put("/todos/" + id)
                        .header("Authorization", authorizationHeader())
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": "Todo updated",
                                    "description": "Updated Description",
                                    "done": true,
                                    "priority": 2
                                }
                                """))
                .andReturn();

        System.out.println("Status: "
                + updateResult.getResponse().getStatus());

        System.out.println("Resposta: "
                + updateResult.getResponse().getContentAsString());

        mockMvc.perform(get("/todos/" + id)
                        .header("Authorization", authorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Todo updated"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.done").value(true))
                .andExpect(jsonPath("$.priority").value(2));
    }

    @Test
    void testUpdateTodoNotFound() throws Exception {
        mockMvc.perform(put("/todos/999999")
                        .header("Authorization", authorizationHeader())
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": "Todo updated",
                                    "description": "Updated Description",
                                    "done": true,
                                    "priority": 2
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTodoSuccess() throws Exception {
        MvcResult result = mockMvc.perform(post("/todos")
                        .header("Authorization", authorizationHeader())
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": "Todo",
                                    "description": "Description",
                                    "done": false,
                                    "priority": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(
                result.getResponse().getContentAsString()
        );

        long id = json.get(0).get("id").asLong();

        mockMvc.perform(delete("/todos/" + id)
                        .header("Authorization", authorizationHeader()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/todos/" + id)
                        .header("Authorization", authorizationHeader()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTodoNotFound() throws Exception {
        mockMvc.perform(delete("/todos/99999999")
                        .header("Authorization", authorizationHeader()))
                .andExpect(status().isNotFound());
    }


    @Test
    void testUserCannotAccessAnotherUsersTodo() throws Exception {

        // User A task
        String todoJson = """
                {
                    "name": "Tarefa privada",
                    "description": "Pertence ao usuário A",
                    "done": false,
                    "priority": 2
                }
                """;

        MvcResult createResult = mockMvc.perform(post("/todos")
                        .header("Authorization", authorizationHeader())
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(todoJson))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode createdTodo = objectMapper.readTree(
                createResult.getResponse().getContentAsString()
        );

        long todoId = createdTodo.get(0).get("id").asLong();

        // User B
        String emailB = "outro" + System.nanoTime() + "@gmail.com";
        String passwordB = "SenhaTeste123!";

        String registerJson = """
                {
                    "name": "Usuario B",
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(emailB, passwordB);

        mockMvc.perform(post("/auth/register")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(registerJson))
                .andExpect(status().is2xxSuccessful());

        // Login User B
        String loginJson = """
                {
                    "name": "Usuario B",
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(emailB, passwordB);

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String tokenB = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
        ).get("token").asText();

        String authB = "Bearer " + tokenB;

        // User B try to get User A data
        mockMvc.perform(get("/todos/{id}", todoId)
                        .header("Authorization", authB))
                .andExpect(status().isNotFound());

        // User B try to update user A tasks
        mockMvc.perform(put("/todos/{id}", todoId)
                        .header("Authorization", authB)
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("""
                                {
                                    "name": "Tentativa de invasão",
                                    "description": "Alteração indevida",
                                    "done": true,
                                    "priority": 4
                                }
                                """))
                .andExpect(status().isNotFound());

        // User B try to delete user A tasks
        mockMvc.perform(delete("/todos/{id}", todoId)
                        .header("Authorization", authB))
                .andExpect(status().isNotFound());

        // user A still can consult his list
        mockMvc.perform(get("/todos/{id}", todoId)
                        .header("Authorization", authorizationHeader()))
                .andExpect(status().isOk());
    }

    @Test
    void AccessWithoutToken() throws Exception {
        mockMvc.perform(get("/todos"))
                .andExpect(status().isForbidden());
    }

}
