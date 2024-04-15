package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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

        return "perfil/perfil";
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

        return "perfil/actualizarPerfil";
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

        return "perfil/actualizarPerfil";
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

        return "perfil/comentariosUsuario";
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
            return "perfil/editarComentario";
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

        return "perfil/editarComentario";
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

        return "perfil/alquileresUsuario";
    }

    @PostMapping("/perfil/{id}/alquileres/eliminar/{alquilerId}")
    public String eliminarAlquiler(@PathVariable("id") Long idUsuario, @PathVariable("alquilerId") Long alquilerId) {
        Long id = managerUserSession.usuarioLogeado();
        comprobarLogueado(id);
        alquilerService.eliminarAlquiler(alquilerId);
        return "redirect:/perfil/" + idUsuario + "/alquileres";
    }


    // VEHÍCULOS DE USUARIO

    @GetMapping("/perfil/{id}/añadir-vehiculo")
    public String mostrarAñadirVehiculo(Model model) {
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

        return "perfil/añadirVehiculoUsuario";
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

    @PostMapping("/perfil/{usuarioId}/añadir-vehiculo")
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
            return "perfil/añadirVehiculoUsuario";
        }
        else{
            try {
                if (vehiculoService.findByMatricula(vehiculoData.getMatricula()) != null) {
                    model.addAttribute("errorActualizar", "El vehículo con matrícula (" + vehiculoData.getMatricula() + ") ya existe.");
                    return "perfil/añadirVehiculoUsuario";
                }

                vehiculoData.setIdUsuario(idUsuario);

                vehiculoService.registrarVehiculo(vehiculoData);

                return "redirect:/perfil/" + idUsuario;
            } catch (Exception e) {
                model.addAttribute("errorActualizar", e.getMessage());
            }
        }

        return "perfil/añadirVehiculoUsuario";
    }

    @GetMapping("/perfil/{id}/vehiculos")
    public String mostrarListadoVehiculos(@PathVariable(value = "id") Long idUsuario, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);
        model.addAttribute("usuario", usuario);

        List<Vehiculo> vehiculos = usuarioService.obtenerVehiculosPorUsuarioId(idUsuario);

        model.addAttribute("vehiculos", vehiculos);

        return "perfil/vehiculosUsuario";
    }

    @GetMapping("/perfil/{usuarioId}/vehiculos/editar/{vehiculoId}")
    public String mostrarEditarVehiculo(@PathVariable(value = "usuarioId") Long idUsuario, @PathVariable(value = "vehiculoId") Long vehiculoId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

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

            model.addAttribute("marcas", marcaService.listadoCompleto());
            model.addAttribute("modelos", modeloService.listadoCompleto());
            model.addAttribute("categorias", categoriaService.listadoCompleto());
            model.addAttribute("colores", colorService.listadoCompleto());
            model.addAttribute("transmisiones", transmisionService.listadoCompleto());

            return "perfil/editarVehiculoUsuario";
        }

        return "redirect:/perfil/" + idUsuario + "/vehiculos";
    }

    @PostMapping("/perfil/{usuarioId}/vehiculos/editar/{vehiculoId}")
    public String actualizarVehiculo(@PathVariable(value = "usuarioId") Long idUsuario, @PathVariable Long vehiculoId, @Valid VehiculoData vehiculoData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        model.addAttribute("vehiculoData", vehiculoData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        model.addAttribute("marcas", marcaService.listadoCompleto());
        model.addAttribute("modelos", modeloService.listadoCompleto());
        model.addAttribute("categorias", categoriaService.listadoCompleto());
        model.addAttribute("colores", colorService.listadoCompleto());
        model.addAttribute("transmisiones", transmisionService.listadoCompleto());

        List<Vehiculo> vehiculos = vehiculoService.listadoCompleto();
        Vehiculo vehiculoBuscado = vehiculoService.buscarVehiculoPorId(vehiculos, vehiculoId);
        model.addAttribute("vehiculo", vehiculoBuscado);

        if (result.hasErrors() || vehiculoData.getMatricula().trim().isEmpty() || vehiculoData.getDescripcion().trim().isEmpty() || vehiculoData.getImagen().trim().isEmpty() || vehiculoData.getKilometraje() == null || vehiculoData.getAnyoFabricacion() == null || vehiculoData.getCapacidadPasajeros() == null || vehiculoData.getCapacidadMaletero() == null || vehiculoData.getNumeroPuertas() == null || vehiculoData.getNumeroMarchas() == null || vehiculoData.getPrecioPorDia() == null || vehiculoData.getPrecioPorMedioDia() == null || vehiculoData.getPrecioCombustible() == null || vehiculoData.getIdMarca() == null || vehiculoData.getIdModelo() == null || vehiculoData.getIdCategoria() == null || vehiculoData.getIdTransmision() == null || vehiculoData.getIdColor() == null) {
            model.addAttribute("errorActualizar", "Unicamente puede estar vacio el campo de la oferta. Todos los demás campos son obligatorios.");
            return "perfil/editarVehiculoUsuario";
        }
        else{
            try {
                VehiculoData nuevoVehiculoData = vehiculoService.findById(vehiculoId);

                if(nuevoVehiculoData.getMatricula() != null) {
                    if ((vehiculoService.findByMatricula(vehiculoData.getMatricula()) != null) && !vehiculoData.getMatricula().equals(nuevoVehiculoData.getMatricula())) {
                        model.addAttribute("errorActualizar", "El vehículo con matrícula (" + vehiculoData.getMatricula() + ") ya existe.");
                        return "perfil/editarVehiculoUsuario";
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

                    return "redirect:/perfil/" + idUsuario + "/vehiculos";
                }
                else{
                    model.addAttribute("errorActualizar", "Ha ocurrido un error al intentar actualizar.");
                }
            } catch (Exception e) {
                model.addAttribute("errorActualizar", e.getMessage());
            }
        }

        return "perfil/editarVehiculoUsuario";
    }

    @PostMapping("/perfil/{usuarioId}/vehiculos/eliminar/{vehiculoId}")
    public String eliminarVehiculo(@PathVariable(value = "usuarioId") Long idUsuario, @PathVariable("vehiculoId") Long vehiculoId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        try {
            vehiculoService.eliminarVehiculo(vehiculoId);
            model.addAttribute("success", "Vehículo eliminado con éxito.");
        } catch (Exception e) {
            model.addAttribute("error", "No se puede eliminar el vehículo debido a: " + e.getMessage());
        }

        return "redirect:/perfil/" + idUsuario + "/vehiculos";
    }

    @GetMapping("/perfil/{usuarioId}/vehiculos/detalles/{vehiculoId}")
    public String mostrarDetallesVehiculo(@PathVariable(value = "usuarioId") Long idUsuario, @PathVariable(value = "vehiculoId") Long vehiculoId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        VehiculoData vehiculo = vehiculoService.findById(vehiculoId);

        if (vehiculo != null) {
            List<Vehiculo> vehiculos = vehiculoService.listadoCompleto();
            Vehiculo vehiculoBuscado = vehiculoService.buscarVehiculoPorId(vehiculos, vehiculoId);
            model.addAttribute("vehiculo", vehiculoBuscado);

            if (vehiculoBuscado.getOferta() != null) {
                BigDecimal precioOriginal = vehiculoBuscado.getPrecioPorDia();
                BigDecimal porcentajeOferta = BigDecimal.valueOf(vehiculoBuscado.getOferta());
                BigDecimal descuento = porcentajeOferta.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                BigDecimal precioOferta = precioOriginal.multiply(BigDecimal.ONE.subtract(descuento));
                precioOferta = precioOferta.setScale(2, RoundingMode.HALF_UP);
                model.addAttribute("precioOferta", precioOferta);
            }

            if (vehiculoBuscado.getComentarios() != null) {
                model.addAttribute("comentarios", vehiculoService.obtenerComentariosPorVehiculoId(vehiculoBuscado.getId()));

                if (!vehiculoService.obtenerComentariosPorVehiculoId(vehiculoBuscado.getId()).isEmpty()) {
                    model.addAttribute("cantidadComentarios", vehiculoService.obtenerComentariosPorVehiculoId(vehiculoBuscado.getId()).size());
                }
                else {
                    model.addAttribute("cantidadComentarios", null);
                }
            }

            return "perfil/detallesVehiculoUsuario";
        }

        return "redirect:/perfil/" + idUsuario + "/vehiculos";
    }


    // CUENTA DE USUARIO

    @GetMapping("/perfil/{id}/cuenta")
    public String mostrarCuenta(@PathVariable(value = "id") Long idUsuario, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);
        List<Vehiculo> vehiculos = usuarioService.obtenerVehiculosPorUsuarioId(idUsuario);
        model.addAttribute("vehiculos", vehiculos);
        model.addAttribute("usuario", usuario);

        List<Pago> pagos = usuarioService.obtenerPagosPorUsuarioId(idUsuario);
        model.addAttribute("pagos", pagos);

        List<Alquiler> alquileres = new ArrayList<>();

        // Recorrer cada vehículo y obtener sus alquileres
        for (Vehiculo vehiculo : vehiculos) {
            List<Alquiler> alquileresDeVehiculo = usuarioService.obtenerAlquileresPorVehiculoId(vehiculo.getId());
            alquileres.addAll(alquileresDeVehiculo);
        }

        model.addAttribute("alquileres", alquileres);

        return "perfil/cuentaUsuario";
    }

    @PostMapping("/perfil/{id}/añadirSaldo")
    public String añadirSaldo(@PathVariable(value = "id") Long idUsuario, Model model) {
        Usuario usuario = usuarioService.añadirSaldo(idUsuario, BigDecimal.valueOf(100));
        return "redirect:/perfil/" + idUsuario + "/cuenta";
    }
}
