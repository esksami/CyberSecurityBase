package sec.project.repository;

import java.util.List;
import sec.project.domain.Task;

public interface TaskRepositoryCustom {
    List<Task> filterByName(String username, String filter);
}
