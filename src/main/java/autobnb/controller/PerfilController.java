package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.controller.exception.UsuarioNoLogeadoException;
import autobnb.dto.ComentarioData;
import autobnb.dto.RegistroData;
import autobnb.dto.UsuarioData;
import autobnb.model.Comentario;
import autobnb.model.Pago;
import autobnb.model.Usuario;
import autobnb.service.AlquilerService;
import autobnb.service.ComentarioService;
import autobnb.service.UsuarioService;
import autobnb.service.UsuarioServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
public class PerfilController {
    @Autowired
    private ManagerUserSession managerUserSession;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ComentarioService comentarioService;

    @Autowired
    AlquilerService alquilerService;

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

    // COMENTARIOS DE USUARIO

    // Método para mostrar los comentarios de un usuario
    @GetMapping("/perfil/{id}/comentarios")
    public String mostrarListadoComentarios(@PathVariable(value = "id") Long idUsuario, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            List<Usuario> usuarios = usuarioService.listadoCompleto();
            Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);
            model.addAttribute("usuario", usuario);

            List<Comentario> comentarios = usuarioService.obtenerComentariosPorUsuarioId(idUsuario);

            model.addAttribute("comentarios", comentarios);
        }
        else {
            throw new UsuarioNoLogeadoException();
        }

        return "comentariosUsuario";
    }

    @GetMapping("/perfil/{id}/comentarios/editar/{comentarioId}")
    public String mostrarEditarComentario(@PathVariable(value = "id") Long idUsuario, @PathVariable(value = "comentarioId") Long comentarioId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);
        model.addAttribute("usuario", usuario);

        if(id != null){
            ComentarioData comentario = comentarioService.findById(comentarioId);

            if (comentario != null) {
                List<Comentario> comentarios = comentarioService.listadoCompleto();
                Comentario comentarioBuscado = comentarioService.buscarComentarioPorId(comentarios, comentarioId);
                model.addAttribute("comentario", comentarioBuscado);

                ComentarioData comentarioData = new ComentarioData();
                comentarioData.setDescripcion(comentarioBuscado.getDescripcion());
                comentarioData.setFechaCreacion(comentarioBuscado.getFechaCreacion());
                comentarioData.setIdVehiculo(comentarioBuscado.getVehiculo().getId());
                comentarioData.setIdUsuario(comentarioBuscado.getUsuario().getId());

                model.addAttribute("comentarioData", comentarioData);
                return "editarComentario";
            }
        }
        else {
            throw new UsuarioNoLogeadoException();
        }

        return "redirect:/perfil/" + idUsuario + "/comentarios";
    }

    @PostMapping("/perfil/{idUsuario}/comentarios/editar/{comentarioId}")
    public String actualizarComentario(@PathVariable Long idUsuario, @PathVariable Long comentarioId, @Valid ComentarioData comentarioData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        if (result.hasErrors()) {
            System.out.println("Ha ocurrido un error.");
        }
        else{
            if(id != null){
                try {
                    ComentarioData nuevoComentarioData = comentarioService.findById(comentarioId);

                    if(comentarioData.getDescripcion() != null) {
                        nuevoComentarioData.setDescripcion(comentarioData.getDescripcion());
                        nuevoComentarioData.setFechaCreacion(comentarioData.getFechaCreacion());
                        nuevoComentarioData.setIdUsuario(comentarioData.getIdUsuario());
                        nuevoComentarioData.setIdVehiculo(comentarioData.getIdVehiculo());

                        comentarioService.actualizarComentario(comentarioId, nuevoComentarioData);

                        return "redirect:/perfil/" + id + "/comentarios";
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

        model.addAttribute("comentarioData", comentarioData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);
        model.addAttribute("usuario", usuario);

        List<Comentario> comentarios = comentarioService.listadoCompleto();
        Comentario comentarioBuscado = comentarioService.buscarComentarioPorId(comentarios, comentarioId);
        model.addAttribute("comentario", comentarioBuscado);

        return "editarComentario";
    }

    @PostMapping("/perfil/{id}/comentarios/eliminar/{comentarioId}")
    public String eliminarComentario(@PathVariable("id") Long idUsuario, @PathVariable("comentarioId") Long comentarioId) {
        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            comentarioService.eliminarComentario(comentarioId);
        }
        else {
            throw new UsuarioNoLogeadoException();
        }

        return "redirect:/perfil/" + idUsuario + "/comentarios";
    }

    // ALQUILERES DE USUARIO

    // Método para mostrar los alquileres de un usuario
    @GetMapping("/perfil/{id}/alquileres")
    public String mostrarListadoAlquileres(@PathVariable(value = "id") Long idUsuario, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            List<Usuario> usuarios = usuarioService.listadoCompleto();
            Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);
            model.addAttribute("usuario", usuario);

            List<Pago> pagos = usuarioService.obtenerPagosPorUsuarioId(idUsuario);

            model.addAttribute("pagos", pagos);
        }
        else {
            throw new UsuarioNoLogeadoException();
        }

        return "alquileresUsuario";
    }

    //
    @PostMapping("/perfil/{id}/alquileres/eliminar/{alquilerId}")
    public String eliminarAlquiler(@PathVariable("id") Long idUsuario, @PathVariable("alquilerId") Long alquilerId) {
        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            alquilerService.eliminarAlquiler(alquilerId);
        }
        else {
            throw new UsuarioNoLogeadoException();
        }

        return "redirect:/perfil/" + idUsuario + "/alquileres";
    }
}
