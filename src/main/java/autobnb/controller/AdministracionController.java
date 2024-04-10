package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.controller.exception.UsuarioNoAutorizadoException;
import autobnb.controller.exception.UsuarioNoLogeadoException;
import autobnb.dto.*;
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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    @Autowired
    ColorService colorService;
    @Autowired
    TransmisionService transmisionService;
    @Autowired
    CategoriaService categoriaService;
    @Autowired
    VehiculoService vehiculoService;

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

        return "administracion/panelAdministrador";
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

        return "administracion/listar/administracionComentarios";
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
            return "administracion/editar/editarComentarioAdministrador";
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

        return "administracion/editar/editarComentarioAdministrador";
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

        return "administracion/listar/administracionAlquileres";
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

        return "administracion/listar/administracionCuentas";
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

        return "administracion/listar/administracionMarcas";
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
            return "administracion/editar/editarMarcaAdministrador";
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
            return "administracion/editar/editarMarcaAdministrador";
        }
        else{
            try {
                MarcaData nuevaMarcaData = marcaService.findById(marcaId);

                if(nuevaMarcaData.getNombre() != null) {
                    if (marcaService.findByNombre(marcaData.getNombre()) != null) {
                        model.addAttribute("errorActualizar", "La marca con nombre (" + marcaData.getNombre() + ") ya existe.");
                        return "administracion/editar/editarMarcaAdministrador";
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

        return "administracion/editar/editarMarcaAdministrador";
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
        return "administracion/crear/crearMarca";
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
            return "administracion/crear/crearMarca";
        }
        else{
            try {
                if (marcaService.findByNombre(marcaData.getNombre()) != null) {
                    model.addAttribute("errorCrear", "La marca con nombre (" + marcaData.getNombre() + ") ya existe.");
                    return "administracion/crear/crearMarca";
                }

                marcaService.crearMarca(marcaData);

                return "redirect:/administracion/marcas";

            } catch (UsuarioServiceException e) {
                model.addAttribute("errorCrear", e.getMessage());
            }
        }

        return "administracion/crear/crearMarca";
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

        return "administracion/listar/administracionModelos";
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
            return "administracion/editar/editarModeloAdministrador";
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
            return "administracion/editar/editarModeloAdministrador";
        }
        else{
            try {
                ModeloData nuevoModeloData = modeloService.findById(modeloId);

                if(nuevoModeloData.getNombre() != null) {
                    if (modeloService.findByNombre(modeloData.getNombre()) != null) {
                        model.addAttribute("errorActualizar", "El modelo con nombre (" + modeloData.getNombre() + ") ya existe.");
                        return "administracion/editar/editarModeloAdministrador";
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

        return "administracion/editar/editarModeloAdministrador";
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

        return "administracion/crear/crearModelo";
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
            return "administracion/crear/crearModelo";
        }
        else{
            try {
                if (modeloService.findByNombre(modeloData.getNombre()) != null) {
                    model.addAttribute("errorCrear", "El modelo con nombre (" + modeloData.getNombre() + ") ya existe.");
                    return "administracion/crear/crearModelo";
                }

                modeloService.crearModelo(modeloData);

                return "redirect:/administracion/modelos";

            } catch (UsuarioServiceException e) {
                model.addAttribute("errorCrear", e.getMessage());
            }
        }

        return "administracion/crear/crearModelo";
    }


    // COLORES

    @GetMapping("/administracion/colores")
    public String mostrarListadoColores(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Color> colores = colorService.listadoCompleto();
        model.addAttribute("colores", colores);

        return "administracion/listar/administracionColores";
    }

    @GetMapping("/administracion/colores/editar/{colorId}")
    public String mostrarEditarColor(@PathVariable(value = "colorId") Long colorId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        ColorData color = colorService.findById(colorId);

        if (color != null) {
            List<Color> colores = colorService.listadoCompleto();
            Color colorBuscado = colorService.buscarColorPorId(colores, colorId);
            model.addAttribute("color", colorBuscado);

            ColorData colorData = new ColorData();
            colorData.setNombre(colorBuscado.getNombre());

            model.addAttribute("colorData", colorData);
            return "administracion/editar/editarColorAdministrador";
        }

        return "redirect:/administracion/colores";
    }

    @PostMapping("/administracion/colores/editar/{colorId}")
    public String actualizarColor(@PathVariable Long colorId, @Valid ColorData colorData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        model.addAttribute("colorData", colorData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Color> colores = colorService.listadoCompleto();
        Color colorBuscado = colorService.buscarColorPorId(colores, colorId);
        model.addAttribute("color", colorBuscado);

        if (result.hasErrors() || colorData.getNombre().trim().isEmpty()) {
            if(colorData.getNombre().trim().isEmpty()) {
                model.addAttribute("errorActualizar", "El nombre del color no puede estar vacío.");
            } else {
                System.out.println("Ha ocurrido un error.");
            }
            return "administracion/editar/editarColorAdministrador";
        }
        else{
            try {
                ColorData nuevoColorData = colorService.findById(colorId);

                if(nuevoColorData.getNombre() != null) {
                    if (colorService.findByNombre(colorData.getNombre()) != null) {
                        model.addAttribute("errorActualizar", "El color con nombre (" + colorData.getNombre() + ") ya existe.");
                        return "administracion/editar/editarColorAdministrador";
                    }

                    nuevoColorData.setNombre(colorData.getNombre());

                    colorService.actualizarColor(colorId, nuevoColorData);

                    return "redirect:/administracion/colores";
                }
                else{
                    model.addAttribute("errorActualizar", "Ha ocurrido un error al intentar actualizar.");
                }

            } catch (UsuarioServiceException e) {
                model.addAttribute("errorActualizar", e.getMessage());
            }
        }

        return "administracion/editar/editarColorAdministrador";
    }

    @PostMapping("/administracion/colores/eliminar/{colorId}")
    public String eliminarColor(@PathVariable("colorId") Long colorId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        if (colorService.tieneVehiculosAsociados(colorId)) {
            model.addAttribute("error", "No se puede eliminar el color porque tiene vehículos asociados.");
        } else {
            colorService.eliminarColor(colorId);
            model.addAttribute("success", "Color eliminado con éxito.");
        }

        return "redirect:/administracion/colores";
    }

    @GetMapping("/administracion/colores/crear")
    public String mostrarCrearColor(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        ColorData colorData = new ColorData();
        model.addAttribute("colorData", colorData);

        return "administracion/crear/crearColor";
    }

    @PostMapping("/administracion/colores/crear")
    public String crearColor(@Valid ColorData colorData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        model.addAttribute("colorData", colorData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        if (result.hasErrors() || colorData.getNombre().trim().isEmpty()) {
            if(colorData.getNombre().trim().isEmpty()) {
                model.addAttribute("errorCrear", "El nombre del color no puede estar vacío.");
            } else {
                System.out.println("Ha ocurrido un error.");
            }
            return "administracion/crear/crearColor";
        }
        else{
            try {
                if (colorService.findByNombre(colorData.getNombre()) != null) {
                    model.addAttribute("errorCrear", "El color con nombre (" + colorData.getNombre() + ") ya existe.");
                    return "administracion/crear/crearColor";
                }

                colorService.crearColor(colorData);

                return "redirect:/administracion/colores";

            } catch (UsuarioServiceException e) {
                model.addAttribute("errorCrear", e.getMessage());
            }
        }

        return "administracion/crear/crearColor";
    }


    // TRANSMISIONES

    @GetMapping("/administracion/transmisiones")
    public String mostrarListadoTransmisiones(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Transmision> transmisiones = transmisionService.listadoCompleto();
        model.addAttribute("transmisiones", transmisiones);

        return "administracion/listar/administracionTransmisiones";
    }

    @GetMapping("/administracion/transmisiones/editar/{transmisionId}")
    public String mostrarEditarTransmision(@PathVariable(value = "transmisionId") Long transmisionId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        TransmisionData transmision = transmisionService.findById(transmisionId);

        if (transmision != null) {
            List<Transmision> transmisiones = transmisionService.listadoCompleto();
            Transmision transmisionBuscada = transmisionService.buscarTransmisionPorId(transmisiones, transmisionId);
            model.addAttribute("transmision", transmisionBuscada);

            TransmisionData transmisionData = new TransmisionData();
            transmisionData.setNombre(transmisionBuscada.getNombre());

            model.addAttribute("transmisionData", transmisionData);
            return "administracion/editar/editarTransmisionAdministrador";
        }

        return "redirect:/administracion/transmisiones";
    }

    @PostMapping("/administracion/transmisiones/editar/{transmisionId}")
    public String actualizarTransmision(@PathVariable Long transmisionId, @Valid TransmisionData transmisionData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        model.addAttribute("transmisionData", transmisionData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Transmision> transmisiones = transmisionService.listadoCompleto();
        Transmision transmisionBuscada = transmisionService.buscarTransmisionPorId(transmisiones, transmisionId);
        model.addAttribute("transmision", transmisionBuscada);

        if (result.hasErrors() || transmisionData.getNombre().trim().isEmpty()) {
            if(transmisionData.getNombre().trim().isEmpty()) {
                model.addAttribute("errorActualizar", "El nombre de la transmisión no puede estar vacío.");
            } else {
                System.out.println("Ha ocurrido un error.");
            }
            return "administracion/editar/editarTransmisionAdministrador";
        }
        else{
            try {
                TransmisionData nuevoTransmisionData = transmisionService.findById(transmisionId);

                if(nuevoTransmisionData.getNombre() != null) {
                    if (transmisionService.findByNombre(transmisionData.getNombre()) != null) {
                        model.addAttribute("errorActualizar", "La transmisión con nombre (" + transmisionData.getNombre() + ") ya existe.");
                        return "administracion/editar/editarTransmisionAdministrador";
                    }

                    nuevoTransmisionData.setNombre(transmisionData.getNombre());

                    transmisionService.actualizarTransmision(transmisionId, nuevoTransmisionData);

                    return "redirect:/administracion/transmisiones";
                }
                else{
                    model.addAttribute("errorActualizar", "Ha ocurrido un error al intentar actualizar.");
                }

            } catch (UsuarioServiceException e) {
                model.addAttribute("errorActualizar", e.getMessage());
            }
        }

        return "administracion/editar/editarTransmisionAdministrador";
    }

    @PostMapping("/administracion/transmisiones/eliminar/{transmisionId}")
    public String eliminarTransmision(@PathVariable("transmisionId") Long transmisionId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        if (transmisionService.tieneVehiculosAsociados(transmisionId)) {
            model.addAttribute("error", "No se puede eliminar la transmisión porque tiene vehículos asociados.");
        } else {
            transmisionService.eliminarTransmision(transmisionId);
            model.addAttribute("success", "Transmisión eliminada con éxito.");
        }

        return "redirect:/administracion/transmisiones";
    }

    @GetMapping("/administracion/transmisiones/crear")
    public String mostrarCrearTransmision(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        TransmisionData transmisionData = new TransmisionData();
        model.addAttribute("transmisionData", transmisionData);

        return "administracion/crear/crearTransmision";
    }

    @PostMapping("/administracion/transmisiones/crear")
    public String crearTransmision(@Valid TransmisionData transmisionData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        model.addAttribute("transmisionData", transmisionData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        if (result.hasErrors() || transmisionData.getNombre().trim().isEmpty()) {
            if(transmisionData.getNombre().trim().isEmpty()) {
                model.addAttribute("errorCrear", "El nombre de la transmisión no puede estar vacío.");
            } else {
                System.out.println("Ha ocurrido un error.");
            }
            return "administracion/crear/crearTransmision";
        }
        else{
            try {
                if (transmisionService.findByNombre(transmisionData.getNombre()) != null) {
                    model.addAttribute("errorCrear", "La transmisión con nombre (" + transmisionData.getNombre() + ") ya existe.");
                    return "administracion/crear/crearTransmision";
                }

                transmisionService.crearTransmision(transmisionData);

                return "redirect:/administracion/transmisiones";

            } catch (UsuarioServiceException e) {
                model.addAttribute("errorCrear", e.getMessage());
            }
        }

        return "administracion/crear/crearTransmision";
    }


    // CATETORIAS

    @GetMapping("/administracion/categorias")
    public String mostrarListadoCategorias(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Categoria> categorias = categoriaService.listadoCompleto();
        model.addAttribute("categorias", categorias);

        return "administracion/listar/administracionCategorias";
    }

    @GetMapping("/administracion/categorias/editar/{categoriaId}")
    public String mostrarEditarCategoria(@PathVariable(value = "categoriaId") Long categoriaId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        CategoriaData categoria = categoriaService.findById(categoriaId);

        if (categoria != null) {
            List<Categoria> categorias = categoriaService.listadoCompleto();
            Categoria categoriaBuscada = categoriaService.buscarCategoriaPorId(categorias, categoriaId);
            model.addAttribute("categoria", categoriaBuscada);

            CategoriaData categoriaData = new CategoriaData();
            categoriaData.setNombre(categoriaBuscada.getNombre());
            categoriaData.setDescripcion(categoriaBuscada.getDescripcion());

            model.addAttribute("categoriaData", categoriaData);
            return "administracion/editar/editarCategoriaAdministrador";
        }

        return "redirect:/administracion/categorias";
    }

    @PostMapping("/administracion/categorias/editar/{categoriaId}")
    public String actualizarCategoria(@PathVariable Long categoriaId, @Valid CategoriaData categoriaData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        model.addAttribute("categoriaData", categoriaData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Categoria> categorias = categoriaService.listadoCompleto();
        Categoria categoriaBuscada = categoriaService.buscarCategoriaPorId(categorias, categoriaId);
        model.addAttribute("categoria", categoriaBuscada);

        if (result.hasErrors() || categoriaData.getNombre().trim().isEmpty()) {
            if(categoriaData.getNombre().trim().isEmpty()) {
                model.addAttribute("errorActualizar", "El nombre de la categoria no puede estar vacío.");
            } else {
                System.out.println("Ha ocurrido un error.");
            }
            return "administracion/editar/editarCategoriaAdministrador";
        }
        else{
            try {
                CategoriaData nuevaCategoriaData = categoriaService.findById(categoriaId);

                if(nuevaCategoriaData.getNombre() != null) {
                    if ((categoriaService.findByNombre(categoriaData.getNombre()) != null) && Objects.equals(categoriaData.getDescripcion(), nuevaCategoriaData.getDescripcion())) {
                        model.addAttribute("errorActualizar", "La categoria con nombre (" + categoriaData.getNombre() + ") ya existe.");
                        return "administracion/editar/editarCategoriaAdministrador";
                    }

                    nuevaCategoriaData.setNombre(categoriaData.getNombre());
                    nuevaCategoriaData.setDescripcion(categoriaData.getDescripcion());

                    categoriaService.actualizarCategoria(categoriaId, nuevaCategoriaData);

                    return "redirect:/administracion/categorias";
                }
                else{
                    model.addAttribute("errorActualizar", "Ha ocurrido un error al intentar actualizar.");
                }

            } catch (UsuarioServiceException e) {
                model.addAttribute("errorActualizar", e.getMessage());
            }
        }

        return "administracion/editar/editarCategoriaAdministrador";
    }

    @PostMapping("/administracion/categorias/eliminar/{categoriaId}")
    public String eliminarCategoria(@PathVariable("categoriaId") Long categoriaId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        if (categoriaService.tieneVehiculosAsociados(categoriaId)) {
            model.addAttribute("error", "No se puede eliminar la categoria porque tiene vehículos asociados.");
        } else {
            categoriaService.eliminarCategoria(categoriaId);
            model.addAttribute("success", "Categoria eliminada con éxito.");
        }

        return "redirect:/administracion/categorias";
    }

    @GetMapping("/administracion/categorias/crear")
    public String mostrarCrearCategoria(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        CategoriaData categoriaData = new CategoriaData();
        model.addAttribute("categoriaData", categoriaData);

        return "administracion/crear/crearCategoria";
    }

    @PostMapping("/administracion/categorias/crear")
    public String crearCategoria(@Valid CategoriaData categoriaData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        model.addAttribute("categoriaData", categoriaData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        if (result.hasErrors() || categoriaData.getNombre().trim().isEmpty()) {
            if(categoriaData.getNombre().trim().isEmpty()) {
                model.addAttribute("errorCrear", "El nombre de la categoria no puede estar vacío.");
            } else {
                System.out.println("Ha ocurrido un error.");
            }
            return "administracion/crear/crearCategoria";
        }
        else{
            try {
                if (categoriaService.findByNombre(categoriaData.getNombre()) != null) {
                    model.addAttribute("errorCrear", "La categoria con nombre (" + categoriaData.getNombre() + ") ya existe.");
                    return "administracion/crear/crearCategoria";
                }

                categoriaService.crearCategoria(categoriaData);

                return "redirect:/administracion/categorias";

            } catch (UsuarioServiceException e) {
                model.addAttribute("errorCrear", e.getMessage());
            }
        }

        return "administracion/crear/crearCategoria";
    }


    // VEHICULOS

    @GetMapping("/administracion/vehiculos")
    public String mostrarListadoVehiculos(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Vehiculo> vehiculos = vehiculoService.listadoCompleto();
        model.addAttribute("vehiculos", vehiculos);

        return "administracion/listar/administracionVehiculos";
    }

    // Manda un JSON con los modelos de una marca concreta (la que elige el usuario en el listado de marcas)
    @GetMapping("/administracion/modelosPorMarca/{marcaId}")
    public @ResponseBody Map<Long, String> getModelosPorMarca(@PathVariable Long marcaId) {
        List<ModeloData> modelos = modeloService.findByMarcaId(marcaId);
        Map<Long, String> modeloMap = new HashMap<>();
        for (ModeloData modelo : modelos) {
            modeloMap.put(modelo.getId(), modelo.getNombre());
        }
        return modeloMap;
    }

    @GetMapping("/administracion/vehiculos/editar/{vehiculoId}")
    public String mostrarEditarVehiculo(@PathVariable(value = "vehiculoId") Long vehiculoId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        VehiculoData vehiculo = vehiculoService.findById(vehiculoId);

        if (vehiculo != null) {
            List<Vehiculo> vehiculos = vehiculoService.listadoCompleto();
            Vehiculo vehiculoBuscado = vehiculoService.buscarVehiculoPorId(vehiculos, vehiculoId);
            model.addAttribute("vehiculo", vehiculoBuscado);

            VehiculoData vehiculoData = new VehiculoData();
            vehiculoData.setMatricula(vehiculoBuscado.getMatricula());
            vehiculoData.setDescripcion(vehiculoBuscado.getDescripcion());
            vehiculoData.setImagen(vehiculoBuscado.getImagen());
            vehiculoData.setKilometraje(vehiculoBuscado.getKilometraje());
            vehiculoData.setAnyoFabricacion(vehiculoBuscado.getAnyoFabricacion());
            vehiculoData.setCapacidadPasajeros(vehiculoBuscado.getCapacidadPasajeros());
            vehiculoData.setCapacidadMaletero(vehiculoBuscado.getCapacidadMaletero());
            vehiculoData.setNumeroPuertas(vehiculoBuscado.getNumeroPuertas());
            vehiculoData.setNumeroMarchas(vehiculoBuscado.getNumeroMarchas());
            vehiculoData.setAireAcondicionado(vehiculoBuscado.isAireAcondicionado());
            vehiculoData.setEnMantenimiento(vehiculoBuscado.isEnMantenimiento());
            vehiculoData.setOferta(vehiculoBuscado.getOferta());
            vehiculoData.setPrecioPorDia(vehiculoBuscado.getPrecioPorDia());
            vehiculoData.setPrecioPorMedioDia(vehiculoBuscado.getPrecioPorMedioDia());
            vehiculoData.setPrecioCombustible(vehiculoBuscado.getPrecioCombustible());
            vehiculoData.setIdMarca(vehiculoBuscado.getMarca().getId());
            vehiculoData.setIdModelo(vehiculoBuscado.getModelo().getId());
            vehiculoData.setIdCategoria(vehiculoBuscado.getCategoria().getId());
            vehiculoData.setIdTransmision(vehiculoBuscado.getTransmision().getId());
            vehiculoData.setIdColor(vehiculoBuscado.getColor().getId());

            model.addAttribute("vehiculoData", vehiculoData);

            List<Marca> marcas = marcaService.listadoCompleto();
            model.addAttribute("marcas", marcas);
            List<Modelo> modelos = modeloService.listadoCompleto();
            model.addAttribute("modelos", modelos);
            List<Categoria> categorias = categoriaService.listadoCompleto();
            model.addAttribute("categorias", categorias);
            List<Color> colores = colorService.listadoCompleto();
            model.addAttribute("colores", colores);
            List<Transmision> transmisiones = transmisionService.listadoCompleto();
            model.addAttribute("transmisiones", transmisiones);

            return "administracion/editar/editarVehiculoAdministrador";
        }

        return "redirect:/administracion/vehiculos";
    }

    @PostMapping("/administracion/vehiculos/editar/{vehiculoId}")
    public String actualizarVehiculo(@PathVariable Long vehiculoId, @Valid VehiculoData vehiculoData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        model.addAttribute("vehiculoData", vehiculoData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Marca> marcas = marcaService.listadoCompleto();
        model.addAttribute("marcas", marcas);
        List<Modelo> modelos = modeloService.listadoCompleto();
        model.addAttribute("modelos", modelos);
        List<Categoria> categorias = categoriaService.listadoCompleto();
        model.addAttribute("categorias", categorias);
        List<Color> colores = colorService.listadoCompleto();
        model.addAttribute("colores", colores);
        List<Transmision> transmisiones = transmisionService.listadoCompleto();
        model.addAttribute("transmisiones", transmisiones);

        List<Vehiculo> vehiculos = vehiculoService.listadoCompleto();
        Vehiculo vehiculoBuscado = vehiculoService.buscarVehiculoPorId(vehiculos, vehiculoId);
        model.addAttribute("vehiculo", vehiculoBuscado);

        if (result.hasErrors() || vehiculoData.getMatricula().trim().isEmpty() || vehiculoData.getDescripcion().trim().isEmpty() || vehiculoData.getImagen().trim().isEmpty() || vehiculoData.getKilometraje() == null || vehiculoData.getAnyoFabricacion() == null || vehiculoData.getCapacidadPasajeros() == null || vehiculoData.getCapacidadMaletero() == null || vehiculoData.getNumeroPuertas() == null || vehiculoData.getNumeroMarchas() == null || vehiculoData.getPrecioPorDia() == null || vehiculoData.getPrecioPorMedioDia() == null || vehiculoData.getPrecioCombustible() == null || vehiculoData.getIdMarca() == null || vehiculoData.getIdModelo() == null || vehiculoData.getIdCategoria() == null || vehiculoData.getIdTransmision() == null || vehiculoData.getIdColor() == null) {
            model.addAttribute("errorActualizar", "Unicamente puede estar vacio el campo de la oferta. Todos los demás campos son obligatorios.");
            return "administracion/editar/editarVehiculoAdministrador";
        }
        else{
            try {
                VehiculoData nuevoVehiculoData = vehiculoService.findById(vehiculoId);

                if(nuevoVehiculoData.getMatricula() != null) {
                    if ((vehiculoService.findByMatricula(vehiculoData.getMatricula()) != null) && !vehiculoData.getMatricula().equals(nuevoVehiculoData.getMatricula())) {
                        model.addAttribute("errorActualizar", "El vehículo con matrícula (" + vehiculoData.getMatricula() + ") ya existe.");
                        return "administracion/editar/editarVehiculoAdministrador";
                    }

                    nuevoVehiculoData.setMatricula(vehiculoData.getMatricula());
                    nuevoVehiculoData.setDescripcion(vehiculoData.getDescripcion());
                    nuevoVehiculoData.setImagen(vehiculoData.getImagen());
                    nuevoVehiculoData.setKilometraje(vehiculoData.getKilometraje());
                    nuevoVehiculoData.setAnyoFabricacion(vehiculoData.getAnyoFabricacion());
                    nuevoVehiculoData.setCapacidadPasajeros(vehiculoData.getCapacidadPasajeros());
                    nuevoVehiculoData.setCapacidadMaletero(vehiculoData.getCapacidadMaletero());
                    nuevoVehiculoData.setNumeroPuertas(vehiculoData.getNumeroPuertas());
                    nuevoVehiculoData.setNumeroMarchas(vehiculoData.getNumeroMarchas());
                    nuevoVehiculoData.setAireAcondicionado(vehiculoData.isAireAcondicionado());
                    nuevoVehiculoData.setEnMantenimiento(vehiculoData.isEnMantenimiento());
                    nuevoVehiculoData.setOferta(vehiculoData.getOferta());
                    nuevoVehiculoData.setPrecioPorDia(vehiculoData.getPrecioPorDia());
                    nuevoVehiculoData.setPrecioPorMedioDia(vehiculoData.getPrecioPorMedioDia());
                    nuevoVehiculoData.setPrecioCombustible(vehiculoData.getPrecioCombustible());
                    nuevoVehiculoData.setIdMarca(vehiculoData.getIdMarca());
                    nuevoVehiculoData.setIdModelo(vehiculoData.getIdModelo());
                    nuevoVehiculoData.setIdCategoria(vehiculoData.getIdCategoria());
                    nuevoVehiculoData.setIdTransmision(vehiculoData.getIdTransmision());
                    nuevoVehiculoData.setIdColor(vehiculoData.getIdColor());

                    vehiculoService.actualizarVehiculo(vehiculoId, nuevoVehiculoData);

                    return "redirect:/administracion/vehiculos";
                }
                else{
                    model.addAttribute("errorActualizar", "Ha ocurrido un error al intentar actualizar.");
                }
            } catch (Exception e) {
                model.addAttribute("errorActualizar", e.getMessage());
            }
        }

        return "administracion/editar/editarVehiculoAdministrador";
    }

    @PostMapping("/administracion/vehiculos/eliminar/{vehiculoId}")
    public String eliminarVehiculo(@PathVariable("vehiculoId") Long vehiculoId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        try {
            vehiculoService.eliminarVehiculo(vehiculoId);
            model.addAttribute("success", "Vehículo eliminado con éxito.");
        } catch (Exception e) {
            model.addAttribute("error", "No se puede eliminar el vehículo debido a: " + e.getMessage());
        }

        return "redirect:/administracion/vehiculos";
    }
}
