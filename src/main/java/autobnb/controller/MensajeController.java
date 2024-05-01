package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.model.Mensaje;
import autobnb.model.Usuario;
import autobnb.service.MensajeService;
import autobnb.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/chat")
public class MensajeController {

    @Autowired
    private MensajeService mensajeService;

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    ManagerUserSession managerUserSession;

    @GetMapping("/{destinatarioId}")
    public String mostrarChat(@PathVariable Long destinatarioId, Model model) {
        Long remitenteId = managerUserSession.usuarioLogeado();
        Usuario remitente = usuarioService.buscarUsuarioPorId(usuarioService.listadoCompleto(), remitenteId);
        Usuario destinatario = usuarioService.buscarUsuarioPorId(usuarioService.listadoCompleto(), destinatarioId);

        List<Mensaje> mensajes = mensajeService.obtenerConversacion(remitente, destinatario);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        mensajes.forEach(mensaje -> mensaje.setContenido(
                String.format("%s (%s)",
                        mensaje.getContenido(),
                        mensaje.getTimestamp().format(formatter))
        ));

        model.addAttribute("usuario", remitente);
        model.addAttribute("destinatarioNombre", destinatario.getNombre());
        model.addAttribute("destinatarioId", destinatarioId);
        model.addAttribute("mensajes", mensajes);

        return "chat";
    }

    @PostMapping("/enviar")
    public String enviarMensaje(@RequestParam Long remitenteId, @RequestParam Long destinatarioId, @RequestParam String contenido) {
        Usuario remitente = usuarioService.buscarUsuarioPorId(usuarioService.listadoCompleto(), remitenteId);
        Usuario destinatario = usuarioService.buscarUsuarioPorId(usuarioService.listadoCompleto(), destinatarioId);
        mensajeService.enviarMensaje(remitente, destinatario, contenido);
        return "redirect:/chat/" + destinatarioId;
    }
}
