package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.controller.exception.UsuarioNoLogeadoException;
import autobnb.dto.UsuarioData;
import autobnb.dto.VehiculoData;
import autobnb.model.Usuario;
import autobnb.model.Vehiculo;
import autobnb.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class VehiculoController {
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    VehiculoService vehiculoService;
    @Autowired
    MarcaService marcaService;
    @Autowired
    ColorService colorService;
    @Autowired
    CategoriaService categoriaService;
    @Autowired
    ManagerUserSession managerUserSession;

    @PostMapping("/comentarios/agregar/{vehiculoId}")
    public String agregarComentario(@PathVariable Long vehiculoId, @RequestParam String descripcion, HttpSession session, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        if(id != null) {
            vehiculoService.agregarComentarioAVehiculo(vehiculoId, id, descripcion);
        }
        else {
            throw new UsuarioNoLogeadoException();
        }

        return "redirect:/detalles-vehiculo/" + vehiculoId;
    }

    @GetMapping("/listado-vehiculos")
    public String mostrarListadoVehiculos(Model model) {
        List<Vehiculo> vehiculos = vehiculoService.listadoCompleto();

        //model.addAttribute("vehiculos", vehiculos.subList(0, Math.min(5, vehiculos.size())));
        model.addAttribute("marcas", marcaService.listadoCompleto());
        model.addAttribute("categorias", categoriaService.listadoCompleto());
        model.addAttribute("colores", colorService.listadoCompleto());
        model.addAttribute("vehiculos", vehiculos);
        //model.addAttribute("hayMasVehiculos", vehiculos.size() > 10); // Para saber si hay más vehículos que cargar

        Map<Long, BigDecimal> preciosOferta = new HashMap<>();

        for (Vehiculo vehiculo : vehiculos) {
            if (vehiculo.getOferta() != null && vehiculo.getOferta() > 0) {
                BigDecimal precioOriginal = vehiculo.getPrecioPorDia();
                BigDecimal porcentajeOferta = BigDecimal.valueOf(vehiculo.getOferta());
                BigDecimal descuento = porcentajeOferta.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                BigDecimal precioOferta = precioOriginal.multiply(BigDecimal.ONE.subtract(descuento));
                precioOferta = precioOferta.setScale(2, RoundingMode.HALF_UP);
                preciosOferta.put(vehiculo.getId(), precioOferta);
            }
        }

        model.addAttribute("preciosOferta", preciosOferta);

        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            UsuarioData user = usuarioService.findById(id);
            model.addAttribute("usuario", user);
        } else {
            model.addAttribute("usuario", null);
        }

        return "listadoVehiculos";
    }

    @GetMapping("/listado-vehiculos/detalles-vehiculo/{vehiculoId}")
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

            return "detallesVehiculo";
        }

        return "redirect:/listado-vehiculos";
    }

    @GetMapping("/listado-vehiculos/ofertas")
    public String mostrarListadoVehiculosOfertas(Model model) {
        List<Vehiculo> vehiculos = vehiculoService.listadoVehiculosConOfertaCompleto();

        model.addAttribute("marcas", marcaService.listadoCompleto());
        model.addAttribute("categorias", categoriaService.listadoCompleto());
        model.addAttribute("colores", colorService.listadoCompleto());
        model.addAttribute("vehiculos", vehiculos);

        Map<Long, BigDecimal> preciosOferta = new HashMap<>();

        for (Vehiculo vehiculo : vehiculos) {
            if (vehiculo.getOferta() != null && vehiculo.getOferta() > 0) {
                BigDecimal precioOriginal = vehiculo.getPrecioPorDia();
                BigDecimal porcentajeOferta = BigDecimal.valueOf(vehiculo.getOferta());
                BigDecimal descuento = porcentajeOferta.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                BigDecimal precioOferta = precioOriginal.multiply(BigDecimal.ONE.subtract(descuento));
                precioOferta = precioOferta.setScale(2, RoundingMode.HALF_UP);
                preciosOferta.put(vehiculo.getId(), precioOferta);
            }
        }

        model.addAttribute("preciosOferta", preciosOferta);

        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            UsuarioData user = usuarioService.findById(id);
            model.addAttribute("usuario", user);
        } else {
            model.addAttribute("usuario", null);
        }

        return "listadoVehiculosOferta";
    }

    @GetMapping("/listado-vehiculos/detalles-vehiculo/oferta/{vehiculoId}")
    public String mostrarDetallesVehiculoOferta(@PathVariable(value = "vehiculoId") Long vehiculoId, Model model) {
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

            return "detallesVehiculoOferta";
        }

        return "redirect:/listado-vehiculos/ofertas";
    }

    /*@GetMapping("/buscar-vehiculos")
    public String buscarVehiculosPorMarca(@RequestParam String marca, Model model) {
        List<Vehiculo> vehiculosFiltrados = vehiculoService.buscarPorMarca(marca);

        model.addAttribute("vehiculos", vehiculosFiltrados);
        model.addAttribute("marcas", marcaService.listadoCompleto());
        model.addAttribute("categorias", categoriaService.listadoCompleto());

        Map<Long, BigDecimal> preciosOferta = new HashMap<>();

        for (Vehiculo vehiculo : vehiculosFiltrados) {
            if (vehiculo.getOferta() != null && vehiculo.getOferta() > 0) {
                BigDecimal precioOriginal = vehiculo.getPrecioPorDia();
                BigDecimal porcentajeOferta = BigDecimal.valueOf(vehiculo.getOferta());
                BigDecimal descuento = porcentajeOferta.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                BigDecimal precioOferta = precioOriginal.multiply(BigDecimal.ONE.subtract(descuento));
                precioOferta = precioOferta.setScale(2, RoundingMode.HALF_UP);
                preciosOferta.put(vehiculo.getId(), precioOferta);
            }
        }

        model.addAttribute("preciosOferta", preciosOferta);

        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            UsuarioData user = usuarioService.findById(id);
            model.addAttribute("usuario", user);
        } else {
            model.addAttribute("usuario", null);
        }

        return "listadoVehiculos";
    }*/

    /*@GetMapping("/filtrar-categoria")
    public String filtrarVehiculosPorCategoria(@RequestParam String categoria, Model model) {
        List<Vehiculo> vehiculosFiltrados = vehiculoService.filtrarPorCategoria(categoria);

        model.addAttribute("vehiculos", vehiculosFiltrados);
        model.addAttribute("marcas", marcaService.listadoCompleto());
        model.addAttribute("categorias", categoriaService.listadoCompleto());

        model.addAttribute("filtroCategoria", categoria);

        Map<Long, BigDecimal> preciosOferta = new HashMap<>();

        for (Vehiculo vehiculo : vehiculosFiltrados) {
            if (vehiculo.getOferta() != null && vehiculo.getOferta() > 0) {
                BigDecimal precioOriginal = vehiculo.getPrecioPorDia();
                BigDecimal porcentajeOferta = BigDecimal.valueOf(vehiculo.getOferta());
                BigDecimal descuento = porcentajeOferta.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                BigDecimal precioOferta = precioOriginal.multiply(BigDecimal.ONE.subtract(descuento));
                precioOferta = precioOferta.setScale(2, RoundingMode.HALF_UP);
                preciosOferta.put(vehiculo.getId(), precioOferta);
            }
        }

        model.addAttribute("preciosOferta", preciosOferta);

        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            UsuarioData user = usuarioService.findById(id);
            model.addAttribute("usuario", user);
        } else {
            model.addAttribute("usuario", null);
        }

        return "listadoVehiculos";
    }*/

    /* @RequestMapping(value = "/cargar-mas-vehiculos", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Vehiculo>> cargarMasVehiculos(@RequestParam("offset") int offset) {
        final int pageSize = 5;
        List<Vehiculo> vehiculos = vehiculoService
                .listadoCompleto()
                .stream()
                .skip(offset)
                .limit(pageSize)
                .collect(Collectors.toList());

        if (vehiculos.isEmpty()) {
            return ResponseEntity.noContent().build();  // Retorna 204 No Content cuando no hay más vehículos.
        }

        return ResponseEntity.ok(vehiculos);
    }*/
}
