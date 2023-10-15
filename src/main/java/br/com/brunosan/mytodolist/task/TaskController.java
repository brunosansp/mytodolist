package br.com.brunosan.mytodolist.task;

import br.com.brunosan.mytodolist.utils.UtilsCopyProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    
    private final ITaskRepository taskRepository;
    
    public TaskController(ITaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    @PostMapping
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        taskModel.setUserId((UUID) userId);
        
        LocalDateTime currentDate = LocalDateTime.now();
        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("As datas de início e término da task devem ser maiores do que a data atual.");
        }
        if (taskModel.getStartAt().isAfter(taskModel.getEndAt()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("A data de início deve ser menor do que a data de término.");
        
        return ResponseEntity.status(HttpStatus.CREATED).body(this.taskRepository.save(taskModel));
    }
    
    @GetMapping
    public ResponseEntity<List<TaskModel>> listTasksByUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        return ResponseEntity.status(HttpStatus.OK).body(this.taskRepository.findAllByUserId((UUID) userId));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity updateTaskById(@RequestBody TaskModel taskModel,
                               HttpServletRequest request,
                               @PathVariable UUID id) {
        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Usuário não tem permissão para alterar a tarefa.");
        }

        var userId = request.getAttribute("userId");

        if (!task.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Usuário não tem permissão para alterar a tarefa.");
        }

        UtilsCopyProperties.copyNonNullProperties(taskModel, task);
        var taskUpdated = this.taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.OK).body(taskUpdated);
    }
}
