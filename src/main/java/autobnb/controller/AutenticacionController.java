package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.dto.LoginData;
import autobnb.dto.RegistroData;
import autobnb.dto.UsuarioData;
import autobnb.model.Usuario;
import autobnb.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
public class AutenticacionController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginData", new LoginData());
        return "formLogin";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute LoginData loginData, Model model) {
        UsuarioService.LoginStatus loginStatus = usuarioService.login(loginData.getEmail(), loginData.getPassword());

        if (loginStatus == UsuarioService.LoginStatus.LOGIN_OK) {
            UsuarioData usuario = usuarioService.findByEmail(loginData.getEmail());

            managerUserSession.logearUsuario(usuario.getId());

            return "redirect:/about";
        } else if (loginStatus == UsuarioService.LoginStatus.USER_NOT_FOUND) {
            model.addAttribute("error", "No existe el usuario introducido.");
            return "formLogin";
        } else if (loginStatus == UsuarioService.LoginStatus.ERROR_PASSWORD) {
            model.addAttribute("error", "Contraseña incorrecta.");
            return "formLogin";
        }

        return "formLogin";
    }

    // FALTA POR IMPLEMENTAR
    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("registroData", new RegistroData());

        boolean admin = false;

        List<Usuario> listaUsuarios = usuarioService.listadoCompleto();

        // Buscamos en el listado completo de usuarios si hay alguno de ellos que sea admin
        for (Usuario listaUsuario : listaUsuarios) {
            if (listaUsuario.isAdministrador()) {
                admin = true;
                break;
            }
        }

        model.addAttribute("admin", admin);

        return "formRegistro";
    }

    // FALTA POR IMPLEMENTAR
   @PostMapping("/registro")
   public String registroSubmit(@Valid RegistroData registroData, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "formRegistro";
        }

        if (usuarioService.findByEmail(registroData.getEmail()) != null) {
            model.addAttribute("registroData", registroData);
            model.addAttribute("error", "El usuario " + registroData.getEmail() + " ya existe.");
            return "formRegistro";
        }

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail(registroData.getEmail());
        usuario.setPassword(registroData.getPassword());
        usuario.setNombre(registroData.getNombre());

       usuario.setApellidos(registroData.getApellidos());
       usuario.setTelefono(registroData.getTelefono());
       //usuario.setCodigopostal(registroData.getCodigopostal());
       //usuario.setPais(registroData.getPais());
       //usuario.setPoblacion(registroData.getPoblacion());
       usuario.setDireccion(registroData.getDireccion());
       //usuario.setAdmin(registroData.isAdmin());

        usuarioService.registrar(usuario);
        return "redirect:/login";
   }

   @GetMapping("/logout")
   public String logout(HttpSession session) {
        managerUserSession.logout();
        return "redirect:/login";
   }
}
