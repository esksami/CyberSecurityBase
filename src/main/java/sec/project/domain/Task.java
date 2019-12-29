package sec.project.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Task extends AbstractPersistable<Long> {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
    
    private String name;
    private String username;

    public Task() {
        super();
    }

    public Task(String name, String username) {
        this();
        this.name = name;
        this.username = username;
    }

//    @Override
//    public Long getId() {
//        return id;
//    }
//
//    @Override
//    public void setId(Long id) {
//        this.id = id;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
