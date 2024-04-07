package autobnb.service;

import autobnb.model.*;
import autobnb.repository.AlquilerRepository;
import autobnb.repository.UsuarioRepository;
import autobnb.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlquilerService {
    @Autowired
    private AlquilerRepository alquilerRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Transactional
    public void eliminarAlquiler(Long alquilerId) {
        Alquiler alquiler = alquilerRepository.findById(alquilerId).orElse(null);
        if (alquiler != null) {
            Pago pago = alquiler.getPago();
            Vehiculo vehiculo = alquiler.getVehiculo();

            if (vehiculo != null) {
                vehiculo.getAlquileres().remove(alquiler);
                vehiculoRepository.save(vehiculo);
            }

            if (pago != null) {
                Usuario usuario = pago.getUsuario();
                if (usuario != null) {
                    usuario.getPagos().remove(pago);
                    usuarioRepository.save(usuario);
                }
            }

            alquilerRepository.delete(alquiler);
        }
    }

    @Transactional(readOnly = true)
    public List<Alquiler> listadoCompleto(){
        return (List<Alquiler>) alquilerRepository.findAll();
    }
}
