package sec.project.config;

import java.util.Arrays;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sec.project.domain.Account;
import sec.project.repository.AccountRepository;
import sec.project.domain.Task;
import sec.project.repository.TaskRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private TaskRepository taskRepository;

    @PostConstruct
    public void init() {
        Account account = new Account();
        
        account.setUsername("hello");
        account.setPassword("world");
        accountRepository.save(account);
        
        Account account2 = new Account();
        
        account2.setUsername("red");
        account2.setPassword("world");
        accountRepository.save(account2);
        
        Account account3 = new Account();
        
        account3.setUsername("blue");
        account3.setPassword("world");
        accountRepository.save(account3);
        
        Task task = new Task();
        
        task.setName("Clean your room!<script> console.log(\"Hello World!\"); </script>");
        task.setUsername("hello");
        taskRepository.save(task);
        
        taskRepository.save(new Task("New", "hello"));
        taskRepository.save(new Task("Another", "hello"));
        taskRepository.save(new Task("Test", "hello"));
        
        taskRepository.save(new Task("Red New", "red"));
        taskRepository.save(new Task("Red Another", "red"));
        taskRepository.save(new Task("Red Test", "red"));
        
        taskRepository.save(new Task("Blue New", "blue"));
        taskRepository.save(new Task("Blue Another", "blue"));
        taskRepository.save(new Task("Blue Test", "blue"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("No such user: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                account.getUsername(),
                account.getPassword(),
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority("USER")));
    }
}
