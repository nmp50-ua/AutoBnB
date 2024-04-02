package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.controller.exception.UsuarioNoLogeadoException;
import autobnb.dto.RegistroData;
import autobnb.dto.UsuarioData;
import autobnb.model.Usuario;
import autobnb.service.UsuarioService;
import autobnb.service.UsuarioServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PerfilController {
    @Autowired
    private ManagerUserSession managerUserSession;

    @Autowired
    UsuarioService usuarioService;

    // Método que devuelve el perfil
    @GetMapping("/perfil/{id}")
    public String perfil(@PathVariable(value = "id") Long idUsuario, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            // Si está logueado, lo buscamos en la base de datos y lo añadimos al atributo "usuario"
            UsuarioData user = usuarioService.findById(id);
            model.addAttribute("logueado", user);

            List<Usuario> usuarios = usuarioService.listadoCompleto();
            Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);

            model.addAttribute("usuario", usuario);
        }
        else {
            throw new UsuarioNoLogeadoException();
        }

        return "perfil";
    }

    @PostMapping("/perfil/cambiarRol/{id}")
    public String cambiarRolUsuario(@PathVariable(value = "id") Long idUsuario) {
        usuarioService.cambiarRolUsuario(idUsuario);
        return "redirect:/perfil/" + idUsuario;
    }

    @GetMapping("/perfil/{id}/actualizar")
    public String mostrarActualizarPerfil(@PathVariable(value = "id") Long idUsuario, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            List<Usuario> usuarios = usuarioService.listadoCompleto();
            Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);
            model.addAttribute("usuario", usuario);

            RegistroData nuevo = new RegistroData();
            nuevo.setEmail(usuario.getEmail());
            nuevo.setPassword(usuario.getPassword());
            nuevo.setNombre(usuario.getNombre());
            nuevo.setApellidos(usuario.getApellidos());
            nuevo.setTelefono(usuario.getTelefono());
            nuevo.setCodigoPostal(usuario.getCodigoPostal());
            nuevo.setCiudad(usuario.getCiudad());
            nuevo.setDireccion(usuario.getDireccion());
            nuevo.setDni(usuario.getDni());
            nuevo.setFechaCaducidadDni(usuario.getFechaCaducidadDni());
            nuevo.setFechaCarnetConducir(usuario.getFechaCarnetConducir());
            nuevo.setImagen(usuario.getImagen());
            nuevo.setNumeroCuenta(usuario.getCuenta().getNumeroCuenta());

            model.addAttribute("registroData", nuevo);
        }
        else {
            throw new UsuarioNoLogeadoException();
        }

        return "actualizarPerfil";
    }

    // Método para manejar la actualización del perfil
    @PostMapping("/perfil/{id}/actualizar")
    public String actualizarPerfil(@PathVariable(value = "id") Long idUsuario, @Valid RegistroData registroData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        if (result.hasErrors()) {
            System.out.println("Ha ocurrido un error.");
        }
        else{
            if(id != null){
                try {
                    UsuarioData nuevoUsuarioData = usuarioService.findById(idUsuario);

                    if(registroData.getEmail() != null && registroData.getPassword() != null && registroData.getNombre() != null
                            && registroData.getTelefono() != null && registroData.getCodigoPostal() != null
                            && registroData.getCiudad() != null && registroData.getDireccion() != null && registroData.getDni() != null
                            && registroData.getFechaCaducidadDni() != null && registroData.getFechaCarnetConducir() != null) {
                        nuevoUsuarioData.setEmail(registroData.getEmail());
                        nuevoUsuarioData.setPassword(registroData.getPassword());
                        nuevoUsuarioData.setNombre(registroData.getNombre());
                        nuevoUsuarioData.setApellidos(registroData.getApellidos());
                        nuevoUsuarioData.setTelefono(registroData.getTelefono());
                        nuevoUsuarioData.setCiudad(registroData.getCiudad());
                        nuevoUsuarioData.setDireccion(registroData.getDireccion());
                        nuevoUsuarioData.setCodigoPostal(registroData.getCodigoPostal());
                        nuevoUsuarioData.setDni(registroData.getDni());
                        nuevoUsuarioData.setFechaCaducidadDni(registroData.getFechaCaducidadDni());
                        nuevoUsuarioData.setFechaCarnetConducir(registroData.getFechaCarnetConducir());
                        nuevoUsuarioData.setImagen(registroData.getImagen());

                        // Validar y actualizar los datos del usuario en el servicio
                        usuarioService.actualizarUsuarioPorId(idUsuario, nuevoUsuarioData);

                        // Redirigir al perfil del usuario
                        return "redirect:/perfil/" + idUsuario;
                    }
                    else{
                        model.addAttribute("errorActualizar", "Ho ocurrido un error al intentar actualizar.");
                    }

                } catch (UsuarioServiceException e) {
                    model.addAttribute("errorActualizar", e.getMessage());
                }
            }
            else {
                throw new UsuarioNoLogeadoException();
            }
        }

        model.addAttribute("registroData", registroData);

        UsuarioData user = usuarioService.findById(id);
        model.addAttribute("logueado", user);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);

        model.addAttribute("usuario", usuario);

        return "actualizarPerfil";
    }
}
