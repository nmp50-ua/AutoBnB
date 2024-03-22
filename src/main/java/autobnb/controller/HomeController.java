package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.dto.UsuarioData;
import autobnb.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class HomeController {
    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;

    @GetMapping("/")
    public String init(Model model) {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            UsuarioData user = usuarioService.findById(id);
            model.addAttribute("usuario", user);
        }

        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            UsuarioData user = usuarioService.findById(id);
            model.addAttribute("usuario", user);
        }

        return "about";
    }
}
