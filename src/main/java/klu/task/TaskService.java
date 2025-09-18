package klu.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(String userId, Task task) {
        task.setId(null);
        task.setUserId(userId);
        if (task.getStatus() == Task.Status.COMPLETED && task.getCompletedAt() == null) {
            task.setCompletedAt(Instant.now());
        }
        return taskRepository.save(task);
    }

    public Page<Task> listTasks(String userId, Integer page, Integer size, String priority, String status, LocalDate dueOn, Boolean overdue, String taskListId) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 20 : size);
        var all = taskRepository.findByUserId(userId, PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        var stream = all.stream()
                .filter(t -> priority == null || t.getPriority().name().equalsIgnoreCase(priority))
                .filter(t -> status == null || t.getStatus().name().equalsIgnoreCase(status))
                .filter(t -> dueOn == null || (t.getDueDate() != null && t.getDueDate().toLocalDate().equals(dueOn)))
                .filter(t -> overdue == null || (overdue && t.getDueDate() != null && t.getDueDate().isBefore(LocalDateTime.now())))
                .filter(t -> taskListId == null || taskListId.equals(t.getTaskListId()))
                .toList();

        int from = Math.min((int) pageable.getOffset(), stream.size());
        int to = Math.min(from + pageable.getPageSize(), stream.size());
        var pageContent = stream.subList(from, to);

        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, stream.size());
    }

    public Optional<Task> getTask(String userId, String id) {
        return taskRepository.findByIdAndUserId(id, userId);
    }

    public Optional<Task> updateTask(String userId, String id, Task updates) {
        return taskRepository.findByIdAndUserId(id, userId).map(existing -> {
            if (updates.getTitle() != null) existing.setTitle(updates.getTitle());
            if (updates.getDescription() != null) existing.setDescription(updates.getDescription());
            if (updates.getDueDate() != null) existing.setDueDate(updates.getDueDate());
            if (updates.getPriority() != null) existing.setPriority(updates.getPriority());
            if (updates.getStatus() != null) {
                existing.setStatus(updates.getStatus());
                if (updates.getStatus() == Task.Status.COMPLETED) {
                    existing.setCompletedAt(existing.getCompletedAt() != null ? existing.getCompletedAt() : Instant.now());
                } else {
                    existing.setCompletedAt(null);
                }
            }
            if (updates.getAttachments() != null) existing.setAttachments(updates.getAttachments());
            if (updates.getNotes() != null) existing.setNotes(updates.getNotes());
            if (updates.getTaskListId() != null) existing.setTaskListId(updates.getTaskListId());
            return taskRepository.save(existing);
        });
    }

    public Optional<Task> setDueDate(String userId, String id, LocalDateTime dueDate) {
        return taskRepository.findByIdAndUserId(id, userId).map(t -> {
            t.setDueDate(dueDate);
            return taskRepository.save(t);
        });
    }

    public boolean deleteTask(String userId, String id) {
        return taskRepository.findByIdAndUserId(id, userId).map(t -> { taskRepository.delete(t); return true; }).orElse(false);
    }

    public Optional<Task> setCompleted(String userId, String id, boolean completed) {
        return taskRepository.findByIdAndUserId(id, userId).map(t -> {
            t.setStatus(completed ? Task.Status.COMPLETED : Task.Status.PENDING);
            t.setCompletedAt(completed ? Instant.now() : null);
            return taskRepository.save(t);
        });
    }
}


