package klu.task;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskListService {

    private final TaskListRepository taskListRepository;

    public TaskListService(TaskListRepository taskListRepository) {
        this.taskListRepository = taskListRepository;
    }

    public TaskList create(String userId, TaskList list) {
        list.setId(null);
        list.setUserId(userId);
        return taskListRepository.save(list);
    }

    public List<TaskList> list(String userId) { return taskListRepository.findByUserId(userId); }

    public Optional<TaskList> update(String userId, String id, TaskList updates) {
        return taskListRepository.findByIdAndUserId(id, userId).map(existing -> {
            if (updates.getName() != null) existing.setName(updates.getName());
            if (updates.getDescription() != null) existing.setDescription(updates.getDescription());
            return taskListRepository.save(existing);
        });
    }

    public boolean delete(String userId, String id) {
        return taskListRepository.findByIdAndUserId(id, userId).map(l -> { taskListRepository.delete(l); return true; }).orElse(false);
    }
}


