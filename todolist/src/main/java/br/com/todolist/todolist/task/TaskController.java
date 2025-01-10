package br.com.todolist.todolist.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {

        System.out.println("Criando tarefa- chegou no controller" + request.getAttribute("userId"));
        var userId = request.getAttribute("userId");
        taskModel.setUserId((UUID) userId);

        var currentDate = LocalDateTime.now();
        System.out.println("Data atual: " + currentDate);
        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(400).body("Data de inicio / terminio deve ser maior que a data atual");
        }
        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(400).body("Data de inicio deve ser menor que a data de termino");
        }
        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(201).body(task);
    }

    @GetMapping("/all") // Corrigindo a rota com /
    public ResponseEntity<List<TaskModel>> listAll(HttpServletRequest request) {
        var userId = request.getAttribute("userId");
        System.out.println("DEBUG - UserId recebido: " + userId);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ArrayList<>());
        }

        try {
            var tasks = this.taskRepository.findByUserId((UUID) userId);
            System.out.println("DEBUG - Tasks encontradas: " + tasks);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            System.out.println("DEBUG - Erro: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ArrayList<>());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskModel> update(@RequestBody TaskModel taskModel, @PathVariable UUID id,
            HttpServletRequest request) {
        var userId = request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (taskModel == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        // Verificar se o usuário logado é o dono da tarefa
        // taskModel.getUserId() -> serve para verificar se o usuário que está tentando
        // alterar a tarefa é o dono da tarefa
        if (taskModel.getUserId() != null && !taskModel.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return taskRepository.findById(id)
                .map(existingTask -> {
                    // Atualizar os campos fornecidos, mantendo os antigos para os campos não
                    // fornecidos
                    if (taskModel.getTitle() != null)
                        try {
                            existingTask.setTitle(taskModel.getTitle());
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } // aqui significa que o campo title não é nulo
                    if (taskModel.getDescription() != null)
                        existingTask.setDescription(taskModel.getDescription());
                    if (taskModel.getPriority() != null)
                        existingTask.setPriority(taskModel.getPriority());
                    if (taskModel.getStartAt() != null)
                        existingTask.setStartAt(taskModel.getStartAt());
                    if (taskModel.getEndAt() != null)
                        existingTask.setEndAt(taskModel.getEndAt());
                    existingTask.setUserId((UUID) userId); // aqui significa que o campo userId não é nulo e foi
                                                           // alterado
                    var updatedTask = taskRepository.save(existingTask);
                    return ResponseEntity.ok(updatedTask);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
