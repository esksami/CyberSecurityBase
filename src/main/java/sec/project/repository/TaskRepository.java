package sec.project.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sec.project.domain.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT task FROM #{#entityName} task WHERE task.username = :username")
    List<Task> findByUsername(@Param("username") String username);
}