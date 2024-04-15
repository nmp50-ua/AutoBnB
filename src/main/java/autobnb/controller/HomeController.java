package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.dto.BusquedaHomeData;
import autobnb.dto.UsuarioData;
import autobnb.dto.VehiculoData;
import autobnb.model.Usuario;
import autobnb.model.Vehiculo;
import autobnb.service.CategoriaService;
import autobnb.service.MarcaService;
import autobnb.service.UsuarioService;
import autobnb.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    VehiculoService vehiculoService;
    @Autowired
    MarcaService marcaService;
    @Autowired
    CategoriaService categoriaService;
    @Autowired
    ManagerUserSession managerUserSession;

    @GetMapping("/")
    public String init(Model model) {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        List<Vehiculo> vehiculos = vehiculoService.listadoVehiculosConOferta();
        model.addAttribute("vehiculos", vehiculos);

        Map<Long, BigDecimal> preciosOferta = new HashMap<>();

        for (Vehiculo vehiculo : vehiculos) {
            BigDecimal precioOriginal = vehiculo.getPrecioPorDia();
            BigDecimal porcentajeOferta = BigDecimal.valueOf(vehiculo.getOferta());
            BigDecimal descuento = porcentajeOferta.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            BigDecimal precioOferta = precioOriginal.multiply(BigDecimal.ONE.subtract(descuento));
            precioOferta = precioOferta.setScale(2, RoundingMode.HALF_UP);
            preciosOferta.put(vehiculo.getId(), precioOferta);
        }

        model.addAttribute("vehiculos", vehiculos);
        model.addAttribute("preciosOferta", preciosOferta);

        model.addAttribute("marcas", marcaService.listadoCompleto());
        model.addAttribute("categorias", categoriaService.listadoCompleto());

        if(id != null){
            UsuarioData user = usuarioService.findById(id);
            model.addAttribute("usuario", user);
        }
        else {
            model.addAttribute("usuario", null);
        }

        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            UsuarioData user = usuarioService.findById(id);
            model.addAttribute("usuario", user);
        }
        else {
            model.addAttribute("usuario", null);
        }

        return "about";
    }

    /*@GetMapping("/buscar-vehiculos")
    public String buscarVehiculosEnHome(BusquedaHomeData busqueda, Model model) {
        List<Vehiculo> vehiculos = vehiculoService.buscarPorCriterios(busqueda);
        model.addAttribute("vehiculos", vehiculos);
        return "listadoVehiculos";
    }*/

    @GetMapping("/home/detalles-vehiculo/{vehiculoId}")
    public String mostrarDetallesVehiculo(@PathVariable(value = "vehiculoId") Long vehiculoId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            List<Usuario> usuarios = usuarioService.listadoCompleto();
            Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
            model.addAttribute("usuario", usuario);
        }
        else {
            model.addAttribute("usuario", null);
        }

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

            return "detallesVehiculoHome";
        }

        return "redirect:/home";
    }
}
