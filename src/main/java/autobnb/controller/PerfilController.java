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
    @Autowired
    VehiculoService vehiculoService;
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

    private void comprobarLogueado(Long idUsuario) {
        if (idUsuario == null) {
            throw new UsuarioNoLogeadoException();
        }
    }

    // Método que devuelve el perfil
    @GetMapping("/perfil/{id}")
    public String perfil(@PathVariable(value = "id") Long idUsuario, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        // Si está logueado, lo buscamos en la base de datos y lo añadimos al atributo "usuario"
        UsuarioData user = usuarioService.findById(id);
        model.addAttribute("logueado", user);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);

        model.addAttribute("usuario", usuario);

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

        comprobarLogueado(id);

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
            comprobarLogueado(id);

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

        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);
        model.addAttribute("usuario", usuario);

        List<Comentario> comentarios = usuarioService.obtenerComentariosPorUsuarioId(idUsuario);

        model.addAttribute("comentarios", comentarios);

        return "comentariosUsuario";
    }

    @GetMapping("/perfil/{id}/comentarios/editar/{comentarioId}")
    public String mostrarEditarComentario(@PathVariable(value = "id") Long idUsuario, @PathVariable(value = "comentarioId") Long comentarioId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);
        model.addAttribute("usuario", usuario);

        comprobarLogueado(id);

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

        return "redirect:/perfil/" + idUsuario + "/comentarios";
    }

    @PostMapping("/perfil/{idUsuario}/comentarios/editar/{comentarioId}")
    public String actualizarComentario(@PathVariable Long idUsuario, @PathVariable Long comentarioId, @Valid ComentarioData comentarioData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        if (result.hasErrors()) {
            System.out.println("Ha ocurrido un error.");
        }
        else{
            comprobarLogueado(id);

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
        comprobarLogueado(id);
        comentarioService.eliminarComentario(comentarioId);
        return "redirect:/perfil/" + idUsuario + "/comentarios";
    }


    // ALQUILERES DE USUARIO

    // Método para mostrar los alquileres de un usuario
    @GetMapping("/perfil/{id}/alquileres")
    public String mostrarListadoAlquileres(@PathVariable(value = "id") Long idUsuario, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);
        model.addAttribute("usuario", usuario);

        List<Pago> pagos = usuarioService.obtenerPagosPorUsuarioId(idUsuario);

        model.addAttribute("pagos", pagos);

        return "alquileresUsuario";
    }

    @PostMapping("/perfil/{id}/alquileres/eliminar/{alquilerId}")
    public String eliminarAlquiler(@PathVariable("id") Long idUsuario, @PathVariable("alquilerId") Long alquilerId) {
        Long id = managerUserSession.usuarioLogeado();
        comprobarLogueado(id);
        alquilerService.eliminarAlquiler(alquilerId);
        return "redirect:/perfil/" + idUsuario + "/alquileres";
    }


    // VEHÍCULOS DE USUARIO

    @GetMapping("/perfil/{id}/añadir-coche")
    public String mostrarEditarVehiculo(@PathVariable("id") Long idUsuario, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        VehiculoData vehiculoData = new VehiculoData();

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

        return "añadirVehiculoUsuario";
    }

    @GetMapping("/modelosPorMarca/{marcaId}")
    public @ResponseBody Map<Long, String> getModelosPorMarca(@PathVariable Long marcaId) {
        List<ModeloData> modelos = modeloService.findByMarcaId(marcaId);
        Map<Long, String> modeloMap = new HashMap<>();
        for (ModeloData modelo : modelos) {
            modeloMap.put(modelo.getId(), modelo.getNombre());
        }
        return modeloMap;
    }

    @PostMapping("/perfil/{usuarioId}/añadir-coche")
    public String registrarVehiculo(@PathVariable("usuarioId") Long idUsuario, @Valid VehiculoData vehiculoData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

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

        if (result.hasErrors() || vehiculoData.getMatricula().trim().isEmpty() || vehiculoData.getDescripcion().trim().isEmpty() || vehiculoData.getImagen().trim().isEmpty() || vehiculoData.getKilometraje() == null || vehiculoData.getAnyoFabricacion() == null || vehiculoData.getCapacidadPasajeros() == null || vehiculoData.getCapacidadMaletero() == null || vehiculoData.getNumeroPuertas() == null || vehiculoData.getNumeroMarchas() == null || vehiculoData.getPrecioPorDia() == null || vehiculoData.getPrecioPorMedioDia() == null || vehiculoData.getPrecioCombustible() == null || vehiculoData.getIdMarca() == null || vehiculoData.getIdModelo() == null || vehiculoData.getIdCategoria() == null || vehiculoData.getIdTransmision() == null || vehiculoData.getIdColor() == null) {
            model.addAttribute("errorActualizar", "Únicamente puede estar vacio el campo de la oferta. Todos los demás campos son obligatorios.");
            return "añadirVehiculoUsuario";
        }
        else{
            try {
                if (vehiculoService.findByMatricula(vehiculoData.getMatricula()) != null) {
                    model.addAttribute("errorActualizar", "El vehículo con matrícula (" + vehiculoData.getMatricula() + ") ya existe.");
                    return "añadirVehiculoUsuario";
                }

                vehiculoData.setIdUsuario(idUsuario);

                vehiculoService.registrarVehiculo(vehiculoData);

                return "redirect:/perfil/" + idUsuario;
            } catch (Exception e) {
                model.addAttribute("errorActualizar", e.getMessage());
            }
        }

        return "añadirVehiculoUsuario";
    }
}
