package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.controller.exception.UsuarioNoAutorizadoException;
import autobnb.controller.exception.UsuarioNoLogeadoException;
import autobnb.dto.ComentarioData;
import autobnb.dto.MarcaData;
import autobnb.dto.ModeloData;
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
    @Autowired
    ModeloService modeloService;

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

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Comentario> comentarios = comentarioService.listadoCompleto();
        model.addAttribute("comentarios", comentarios);

        return "administracionComentarios";
    }

    @GetMapping("/administracion/comentarios/editar/{comentarioId}")
    public String mostrarEditarComentario(@PathVariable(value = "comentarioId") Long comentarioId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

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


        return "redirect:/administracion/comentarios";
    }

    @PostMapping("/administracion/comentarios/editar/{comentarioId}")
    public String actualizarComentario(@PathVariable Long comentarioId, @Valid ComentarioData comentarioData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        if (result.hasErrors()) {
            System.out.println("Ha ocurrido un error.");
        }
        else{
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
        comprobarAdmin(id);
        comentarioService.eliminarComentario(comentarioId);
        return "redirect:/administracion/comentarios";
    }


    // ALQUILERES

    @GetMapping("/administracion/alquileres")
    public String mostrarListadoAlquileres(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Alquiler> alquileres = alquilerService.listadoCompleto();
        model.addAttribute("alquileres", alquileres);

        return "administracionAlquileres";
    }

    @PostMapping("/administracion/alquileres/eliminar/{alquilerId}")
    public String eliminarAlquiler(@PathVariable("alquilerId") Long alquilerId) {
        Long id = managerUserSession.usuarioLogeado();
        comprobarAdmin(id);
        alquilerService.eliminarAlquiler(alquilerId);
        return "redirect:/administracion/alquileres";
    }


    // CUENTAS

    @GetMapping("/administracion/cuentas")
    public String mostrarListadoCuentas(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Cuenta> cuentas = cuentaService.listadoCompleto();
        model.addAttribute("cuentas", cuentas);

        return "administracionCuentas";
    }


    // MARCAS

    @GetMapping("/administracion/marcas")
    public String mostrarListadoMarcas(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Marca> marcas = marcaService.listadoCompleto();
        model.addAttribute("marcas", marcas);

        return "administracionMarcas";
    }

    @GetMapping("/administracion/marcas/editar/{marcaId}")
    public String mostrarEditarMarca(@PathVariable(value = "marcaId") Long marcaId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

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

        return "redirect:/administracion/marcas";
    }

    @PostMapping("/administracion/marcas/editar/{marcaId}")
    public String actualizarMarca(@PathVariable Long marcaId, @Valid MarcaData marcaData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

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

        return "editarMarcaAdministrador";
    }

    @PostMapping("/administracion/marcas/eliminar/{marcaId}")
    public String eliminarMarca(@PathVariable("marcaId") Long marcaId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        if (marcaService.tieneVehiculosAsociados(marcaId)) {
            model.addAttribute("error", "No se puede eliminar la marca porque tiene vehículos asociados.");
        } else {
            marcaService.eliminarMarca(marcaId);
            model.addAttribute("success", "Marca eliminada con éxito.");
        }

        return "redirect:/administracion/marcas";
    }

    @GetMapping("/administracion/marcas/crear")
    public String mostrarCrearMarca(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        MarcaData marcaData = new MarcaData();
        model.addAttribute("marcaData", marcaData);
        return "crearMarca";
    }

    @PostMapping("/administracion/marcas/crear")
    public String crearMarca(@Valid MarcaData marcaData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

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

        return "crearMarca";
    }


    // MODELOS

    @GetMapping("/administracion/modelos")
    public String mostrarListadoModelos(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Modelo> modelos = modeloService.listadoCompleto();
        model.addAttribute("modelos", modelos);

        return "administracionModelos";
    }

    @GetMapping("/administracion/modelos/editar/{modeloId}")
    public String mostrarEditarModelo(@PathVariable(value = "modeloId") Long modeloId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        ModeloData modelo = modeloService.findById(modeloId);

        if (modelo != null) {
            List<Modelo> modelos = modeloService.listadoCompleto();
            Modelo modeloBuscado = modeloService.buscarModeloPorId(modelos, modeloId);
            model.addAttribute("modelo", modeloBuscado);

            ModeloData modeloData = new ModeloData();
            modeloData.setNombre(modeloBuscado.getNombre());

            model.addAttribute("modeloData", modeloData);
            return "editarModeloAdministrador";
        }

        return "redirect:/administracion/modelos";
    }

    @PostMapping("/administracion/modelos/editar/{modeloId}")
    public String actualizarModelo(@PathVariable Long modeloId, @Valid ModeloData modeloData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        model.addAttribute("modeloData", modeloData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Modelo> modelos = modeloService.listadoCompleto();
        Modelo modeloBuscado = modeloService.buscarModeloPorId(modelos, modeloId);
        model.addAttribute("modelo", modeloBuscado);

        if (result.hasErrors() || modeloData.getNombre().trim().isEmpty()) {
            if(modeloData.getNombre().trim().isEmpty()) {
                model.addAttribute("errorActualizar", "El nombre del modelo no puede estar vacío.");
            } else {
                System.out.println("Ha ocurrido un error.");
            }
            return "editarModeloAdministrador";
        }
        else{
            try {
                ModeloData nuevoModeloData = modeloService.findById(modeloId);

                if(nuevoModeloData.getNombre() != null) {
                    if (modeloService.findByNombre(modeloData.getNombre()) != null) {
                        model.addAttribute("errorActualizar", "El modelo con nombre (" + modeloData.getNombre() + ") ya existe.");
                        return "editarModeloAdministrador";
                    }

                    nuevoModeloData.setNombre(modeloData.getNombre());

                    modeloService.actualizarModelo(modeloId, nuevoModeloData);

                    return "redirect:/administracion/modelos";
                }
                else{
                    model.addAttribute("errorActualizar", "Ha ocurrido un error al intentar actualizar.");
                }

            } catch (UsuarioServiceException e) {
                model.addAttribute("errorActualizar", e.getMessage());
            }
        }

        return "editarModeloAdministrador";
    }

    @PostMapping("/administracion/modelos/eliminar/{modeloId}")
    public String eliminarModelo(@PathVariable("modeloId") Long modeloId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        if (modeloService.tieneVehiculosAsociados(modeloId)) {
            model.addAttribute("error", "No se puede eliminar el modelo porque tiene vehículos asociados.");
        } else {
            modeloService.eliminarModelo(modeloId);
            model.addAttribute("success", "Modelo eliminada con éxito.");
        }

        return "redirect:/administracion/modelos";
    }

    @GetMapping("/administracion/modelos/crear")
    public String mostrarCrearModelo(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        ModeloData modeloData = new ModeloData();
        model.addAttribute("modeloData", modeloData);

        List<Marca> marcas = marcaService.listadoCompleto();
        model.addAttribute("marcas", marcas);

        return "crearModelo";
    }

    @PostMapping("/administracion/modelos/crear")
    public String crearModelo(@Valid ModeloData modeloData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        model.addAttribute("modeloData", modeloData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Marca> marcas = marcaService.listadoCompleto();
        model.addAttribute("marcas", marcas);

        if (result.hasErrors() || modeloData.getNombre().trim().isEmpty() || modeloData.getIdMarca() == null) {
            if(modeloData.getNombre().trim().isEmpty()) {
                model.addAttribute("errorCrear", "El nombre del modelo no puede estar vacío.");
            } else if(modeloData.getIdMarca() == null) {
                model.addAttribute("errorCrear", "Debe seleccionar una marca.");
            } else {
                System.out.println("Ha ocurrido un error.");
            }
            return "crearModelo";
        }
        else{
            try {
                if (modeloService.findByNombre(modeloData.getNombre()) != null) {
                    model.addAttribute("errorCrear", "El modelo con nombre (" + modeloData.getNombre() + ") ya existe.");
                    return "crearModelo";
                }

                modeloService.crearModelo(modeloData);

                return "redirect:/administracion/modelos";

            } catch (UsuarioServiceException e) {
                model.addAttribute("errorCrear", e.getMessage());
            }
        }

        return "crearModelo";
    }
}
