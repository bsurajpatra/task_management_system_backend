package klu.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {
    Page<Task> findByUserId(String userId, Pageable pageable);
    Optional<Task> findByIdAndUserId(String id, String userId);
    List<Task> findByUserIdAndDueDateBefore(String userId, LocalDateTime before);
    List<Task> findByUserIdAndTaskListId(String userId, String taskListId);
}


