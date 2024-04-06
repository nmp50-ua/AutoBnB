package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.controller.exception.UsuarioNoAutorizadoException;
import autobnb.controller.exception.UsuarioNoLogeadoException;
import autobnb.dto.UsuarioData;
import autobnb.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class AdministracionController {
    @Autowired
    private ManagerUserSession managerUserSession;

    @Autowired
    UsuarioService usuarioService;

    private void comprobarAdmin(Long idUsuario) {
        if (idUsuario != null) {
            UsuarioData user = usuarioService.findById(idUsuario);
            if (user != null && !user.isAdministrador()) {
                throw new UsuarioNoAutorizadoException();
            }
        } else {
            throw new UsuarioNoLogeadoException();
        }
    }

    @GetMapping("/administracion")
    public String panelAdministracion(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        UsuarioData user = usuarioService.findById(id);
        model.addAttribute("usuario", user);

        return "panelAdministrador";
    }
}
