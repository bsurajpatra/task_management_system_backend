package klu.task;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskListRepository extends MongoRepository<TaskList, String> {
    List<TaskList> findByUserId(String userId);
    Optional<TaskList> findByIdAndUserId(String id, String userId);
}


