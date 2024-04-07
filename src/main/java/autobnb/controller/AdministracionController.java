package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.controller.exception.UsuarioNoAutorizadoException;
import autobnb.controller.exception.UsuarioNoLogeadoException;
import autobnb.dto.ComentarioData;
import autobnb.dto.MarcaData;
import autobnb.dto.UsuarioData;
import autobnb.model.*;
import autobnb.service.*;
import autobnb.service.exception.UsuarioServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    @Autowired
    AlquilerService alquilerService;
    @Autowired
    CuentaService cuentaService;
    @Autowired
    MarcaService marcaService;

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

    // ALQUILERES

    @GetMapping("/administracion/alquileres")
    public String mostrarListadoAlquileres(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            List<Usuario> usuarios = usuarioService.listadoCompleto();
            Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
            model.addAttribute("usuario", usuario);

            List<Alquiler> alquileres = alquilerService.listadoCompleto();
            model.addAttribute("alquileres", alquileres);
        }
        else {
            throw new UsuarioNoLogeadoException();
        }

        return "administracionAlquileres";
    }

    @PostMapping("/administracion/alquileres/eliminar/{alquilerId}")
    public String eliminarAlquiler(@PathVariable("alquilerId") Long alquilerId) {
        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            alquilerService.eliminarAlquiler(alquilerId);
        }
        else {
            throw new UsuarioNoLogeadoException();
        }

        return "redirect:/administracion/alquileres";
    }

    // CUENTAS

    @GetMapping("/administracion/cuentas")
    public String mostrarListadoCuentas(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            List<Usuario> usuarios = usuarioService.listadoCompleto();
            Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
            model.addAttribute("usuario", usuario);

            List<Cuenta> cuentas = cuentaService.listadoCompleto();
            model.addAttribute("cuentas", cuentas);
        }
        else {
            throw new UsuarioNoLogeadoException();
        }

        return "administracionCuentas";
    }

    // MARCAS

    @GetMapping("/administracion/marcas")
    public String mostrarListadoMarcas(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            List<Usuario> usuarios = usuarioService.listadoCompleto();
            Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
            model.addAttribute("usuario", usuario);

            List<Marca> marcas = marcaService.listadoCompleto();
            model.addAttribute("marcas", marcas);
        }
        else {
            throw new UsuarioNoLogeadoException();
        }

        return "administracionMarcas";
    }

    @GetMapping("/administracion/marcas/editar/{marcaId}")
    public String mostrarEditarMarca(@PathVariable(value = "marcaId") Long marcaId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        if(id != null){
            MarcaData marca = marcaService.findById(marcaId);

            if (marca != null) {
                List<Marca> marcas = marcaService.listadoCompleto();
                Marca marcaBuscada = marcaService.buscarMarcaPorId(marcas, marcaId);
                model.addAttribute("marca", marcaBuscada);

                MarcaData marcaData = new MarcaData();
                marcaData.setNombre(marcaBuscada.getNombre());

                model.addAttribute("marcaData", marcaData);
                return "editarMarcaAdministrador";
            }
        }
        else {
            throw new UsuarioNoLogeadoException();
        }

        return "redirect:/administracion/marcas";
    }

    @PostMapping("/administracion/marcas/editar/{marcaId}")
    public String actualizarMarca(@PathVariable Long marcaId, @Valid MarcaData marcaData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        model.addAttribute("marcaData", marcaData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Marca> marcas = marcaService.listadoCompleto();
        Marca marcaBuscada = marcaService.buscarMarcaPorId(marcas, marcaId);
        model.addAttribute("marca", marcaBuscada);

        if (result.hasErrors() || marcaData.getNombre().trim().isEmpty()) {
            if(marcaData.getNombre().trim().isEmpty()) {
                model.addAttribute("errorActualizar", "El nombre de la marca no puede estar vacío.");
            } else {
                System.out.println("Ha ocurrido un error.");
            }
            return "editarMarcaAdministrador";
        }
        else{
            if(id != null){
                try {
                    MarcaData nuevaMarcaData = marcaService.findById(marcaId);

                    if(nuevaMarcaData.getNombre() != null) {
                        if (marcaService.findByNombre(nuevaMarcaData.getNombre()) != null) {
                            model.addAttribute("errorActualizar", "La marca con nombre (" + marcaData.getNombre() + ") ya existe.");
                            return "editarMarcaAdministrador";
                        }

                        nuevaMarcaData.setNombre(marcaData.getNombre());

                        marcaService.actualizarMarca(marcaId, nuevaMarcaData);

                        return "redirect:/administracion/marcas";
                    }
                    else{
                        model.addAttribute("errorActualizar", "Ha ocurrido un error al intentar actualizar.");
                    }

                } catch (UsuarioServiceException e) {
                    model.addAttribute("errorActualizar", e.getMessage());
                }
            }
            else {
                throw new UsuarioNoLogeadoException();
            }
        }

        return "editarMarcaAdministrador";
    }

    @PostMapping("/administracion/marcas/eliminar/{marcaId}")
    public String eliminarMarca(@PathVariable("marcaId") Long marcaId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        if (id != null) {
            if (marcaService.tieneVehiculosAsociados(marcaId)) {
                model.addAttribute("error", "No se puede eliminar la marca porque tiene vehículos asociados.");
            } else {
                marcaService.eliminarMarca(marcaId);
                model.addAttribute("success", "Marca eliminada con éxito.");
            }
        } else {
            throw new UsuarioNoLogeadoException();
        }

        return "redirect:/administracion/marcas";
    }

    @GetMapping("/administracion/marcas/crear")
    public String mostrarCrearMarca(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        if(id != null){
                MarcaData marcaData = new MarcaData();
                model.addAttribute("marcaData", marcaData);
                return "crearMarca";
        }
        else {
            throw new UsuarioNoLogeadoException();
        }
    }

    @PostMapping("/administracion/marcas/crear")
    public String crearMarca(@Valid MarcaData marcaData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        model.addAttribute("marcaData", marcaData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        if (result.hasErrors() || marcaData.getNombre().trim().isEmpty()) {
            if(marcaData.getNombre().trim().isEmpty()) {
                model.addAttribute("errorCrear", "El nombre de la marca no puede estar vacío.");
            } else {
                System.out.println("Ha ocurrido un error.");
            }
            return "crearMarca";
        }
        else{
            if(id != null){
                try {
                    if (marcaService.findByNombre(marcaData.getNombre()) != null) {
                        model.addAttribute("errorCrear", "La marca con nombre (" + marcaData.getNombre() + ") ya existe.");
                        return "crearMarca";
                    }

                    marcaService.crearMarca(marcaData);

                    return "redirect:/administracion/marcas";

                } catch (UsuarioServiceException e) {
                    model.addAttribute("errorCrear", e.getMessage());
                }
            }
            else {
                throw new UsuarioNoLogeadoException();
            }
        }

        return "crearMarca";
    }

    // MODELOS
}
