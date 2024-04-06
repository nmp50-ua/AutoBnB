package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.controller.exception.UsuarioNoAutorizadoException;
import autobnb.controller.exception.UsuarioNoLogeadoException;
import autobnb.dto.ComentarioData;
import autobnb.dto.UsuarioData;
import autobnb.model.Comentario;
import autobnb.model.Usuario;
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

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
public class AdministracionController {
    @Autowired
    private ManagerUserSession managerUserSession;

    @Autowired
    UsuarioService usuarioService;
    @Autowired
    ComentarioService comentarioService;

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

    // COMENTARIOS

    @GetMapping("/administracion/comentarios")
    public String mostrarListadoComentarios(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            List<Usuario> usuarios = usuarioService.listadoCompleto();
            Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
            model.addAttribute("usuario", usuario);

            List<Comentario> comentarios = comentarioService.listadoCompleto();
            model.addAttribute("comentarios", comentarios);
        }
        else {
            throw new UsuarioNoLogeadoException();
        }

        return "administracionComentarios";
    }

    @GetMapping("/administracion/comentarios/editar/{comentarioId}")
    public String mostrarEditarComentario(@PathVariable(value = "comentarioId") Long comentarioId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
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
                return "editarComentarioAdministrador";
            }
        }
        else {
            throw new UsuarioNoLogeadoException();
        }

        return "redirect:/administracion/comentarios";
    }

    @PostMapping("/administracion/comentarios/editar/{comentarioId}")
    public String actualizarComentario(@PathVariable Long comentarioId, @Valid ComentarioData comentarioData, BindingResult result, Model model) {
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

                        return "redirect:/administracion/comentarios";
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
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Comentario> comentarios = comentarioService.listadoCompleto();
        Comentario comentarioBuscado = comentarioService.buscarComentarioPorId(comentarios, comentarioId);
        model.addAttribute("comentario", comentarioBuscado);

        return "editarComentarioAdministrador";
    }

    @PostMapping("/administracion/comentarios/eliminar/{comentarioId}")
    public String eliminarComentario(@PathVariable("comentarioId") Long comentarioId) {
        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            comentarioService.eliminarComentario(comentarioId);
        }
        else {
            throw new UsuarioNoLogeadoException();
        }

        return "redirect:/administracion/comentarios";
    }
}
