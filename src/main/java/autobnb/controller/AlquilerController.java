package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.controller.exception.UsuarioNoLogeadoException;
import autobnb.model.Alquiler;
import autobnb.model.Usuario;
import autobnb.model.Vehiculo;
import autobnb.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class AlquilerController {
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    VehiculoService vehiculoService;
    @Autowired
    MarcaService marcaService;
    @Autowired
    ColorService colorService;
    @Autowired
    TransmisionService transmisionService;
    @Autowired
    CategoriaService categoriaService;
    @Autowired
    ManagerUserSession managerUserSession;

    private void comprobarLogueado(Long idUsuario) {
        if (idUsuario == null) {
            throw new UsuarioNoLogeadoException();
        }
    }

    @GetMapping("/api/alquileres/{vehiculoId}")
    @ResponseBody
    public List<Map<String, Object>> getAlquileres(@PathVariable(value = "vehiculoId") Long vehiculoId) {
        List<Alquiler> alquileres = vehiculoService.obtenerAlquileresPorVehiculoId(vehiculoId);
        List<Map<String, Object>> eventos = new ArrayList<>();
        for (Alquiler alquiler : alquileres) {
            Map<String, Object> evento = new HashMap<>();
            evento.put("title", "ALQUILADO");

            // Formatear fechaEntrega como String
            String fechaEntregaStr = new SimpleDateFormat("yyyy-MM-dd").format(alquiler.getFechaEntrega());
            evento.put("start", fechaEntregaStr);

            // Sumar un día a fechaDevolucion y formatear como String
            Calendar cal = Calendar.getInstance();
            cal.setTime(alquiler.getFechaDevolucion());
            cal.add(Calendar.DATE, 1);
            String fechaDevolucionStr = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
            evento.put("end", fechaDevolucionStr);

            evento.put("allDay", true);
            eventos.add(evento);
        }
        return eventos;
    }

    @GetMapping("/alquilar/{vehiculoId}")
    public String eleccionFechaInicio(@PathVariable(value = "vehiculoId") Long vehiculoId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Vehiculo> vehiculos = vehiculoService.listadoCompleto();
        model.addAttribute("vehiculo", vehiculoService.buscarVehiculoPorId(vehiculos, vehiculoId));

        return "alquilar/eleccionFechaInicio";
    }
}
