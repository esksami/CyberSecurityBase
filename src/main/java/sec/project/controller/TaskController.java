package sec.project.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Task;
import sec.project.repository.TaskRepository;

@Controller
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/tasks";
    }

    @RequestMapping(value = "/tasks", method = RequestMethod.GET)
    public String getTasks(Authentication authentication, Model model) {
        String username = authentication.getName();
        
        List<Task> tasks = taskRepository.findByUsername(username);
        
        model.addAttribute("username", username);
        model.addAttribute("tasks", tasks);
        
        return "tasks";
    }

    @RequestMapping(value = "/tasks/new", method = RequestMethod.GET)
    public String createTaskForm(Authentication authentication) {    
        return "newTask";
    }
    
    @RequestMapping(value = "/tasks/new", method = RequestMethod.POST)
    public String createTask(Authentication authentication, @RequestParam String name) {
        String username = authentication.getName();
        
        taskRepository.save(new Task(name, username));
        
        return "redirect:/tasks";
    }
}
