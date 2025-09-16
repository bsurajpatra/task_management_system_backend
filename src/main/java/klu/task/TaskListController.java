package klu.task;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task-lists")
public class TaskListController {

    private final TaskListService taskListService;

    public TaskListController(TaskListService taskListService) {
        this.taskListService = taskListService;
    }

    private String userId(Authentication auth) { return auth.getName(); }

    @PostMapping
    public ResponseEntity<TaskList> create(Authentication auth, @Valid @RequestBody TaskList list) {
        return ResponseEntity.ok(taskListService.create(userId(auth), list));
    }

    @GetMapping
    public ResponseEntity<List<TaskList>> list(Authentication auth) {
        return ResponseEntity.ok(taskListService.list(userId(auth)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskList> update(Authentication auth, @PathVariable String id, @RequestBody TaskList updates) {
        return taskListService.update(userId(auth), id, updates)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable String id) {
        boolean deleted = taskListService.delete(userId(auth), id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}


