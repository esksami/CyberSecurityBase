package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Account;
import sec.project.domain.Task;
import sec.project.repository.AccountRepository;

@Controller
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder encoder;

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String getUserProfile(Authentication authentication, Model model) {
        Account account = accountRepository.findByUsername(authentication.getName());
        
        model.addAttribute("user", account);
        
        return "profile";
    }
//    @RequestMapping("/form")
//    public String passwordForm() {
//        return "form";
//    }

//    @RequestMapping(value = "/login", method = RequestMethod.GET)
//    public String loginForm() {
//        return "login";
//    }
//
//    @RequestMapping(value = "/login", method = RequestMethod.POST)
//    public String loginForm(@RequestParam String password, @RequestParam String username) {
//        return "login";
//    }

//    @RequestMapping(value = "/password", method = RequestMethod.POST)
//    public String changePassword(Authentication authentication, @RequestParam String password) {
//        Account account = accountRepository.findByUsername(authentication.getName());
//        if (account == null) {
//            return "redirect:/index";
//        }
//
//        account.setPassword(encoder.encode(password));
//        accountRepository.save(account);
//
//        return "thanks";
//    }
}
