package klu.task;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import klu.task.dto.DueDateUpdateRequest;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    private String userId(Authentication auth) { return auth.getName(); }

    @PostMapping
    public ResponseEntity<Task> create(Authentication auth, @Valid @RequestBody Task task) {
        return ResponseEntity.ok(taskService.createTask(userId(auth), task));
    }

    @GetMapping
    public ResponseEntity<Page<Task>> list(Authentication auth,
                                           @RequestParam(required = false) Integer page,
                                           @RequestParam(required = false) Integer size,
                                           @RequestParam(required = false) String priority,
                                           @RequestParam(required = false) String status,
                                           @RequestParam(required = false) LocalDate dueOn,
                                           @RequestParam(required = false) Boolean overdue,
                                           @RequestParam(required = false, name = "taskListId") String taskListId) {
        return ResponseEntity.ok(taskService.listTasks(userId(auth), page, size, priority, status, dueOn, overdue, taskListId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> get(Authentication auth, @PathVariable String id) {
        return taskService.getTask(userId(auth), id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> update(Authentication auth, @PathVariable String id, @RequestBody Task updates) {
        return taskService.updateTask(userId(auth), id, updates)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable String id) {
        boolean deleted = taskService.deleteTask(userId(auth), id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Task> complete(Authentication auth, @PathVariable String id, @RequestParam boolean completed) {
        return taskService.setCompleted(userId(auth), id, completed)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/due-date")
    public ResponseEntity<Task> updateDueDate(Authentication auth, @PathVariable String id, @Valid @RequestBody DueDateUpdateRequest body) {
        if (body.getDueDate() == null) {
            return taskService.setDueDate(userId(auth), id, null)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        return taskService.setDueDate(userId(auth), id, body.getDueDate())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}


