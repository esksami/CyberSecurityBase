package sec.project.repository;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import sec.project.domain.Task;

public class TaskRepositoryImpl implements TaskRepositoryCustom {
 
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Task> filterByName(String username, String filter) {
        String queryStr = "SELECT t FROM Task t WHERE " +
            String.format("t.username = '%s'", username) + 
            String.format("AND t.name LIKE '%%%s%%'", filter);
        
        TypedQuery<Task> query = entityManager.createQuery(queryStr, Task.class);
        
        return query.getResultList();
    }
}
