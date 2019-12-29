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
        String username;
        try {
            username = authentication.getName();
        } catch(Exception NullPointerException) {
            return "redirect:/login";
        }
        
        List<Task> tasks = taskRepository.findByUsername(username);
        
        model.addAttribute("username", username);
        model.addAttribute("tasks", tasks);
        
        return "tasks";
    }
}
