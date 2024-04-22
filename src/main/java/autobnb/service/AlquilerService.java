package autobnb.service;

import autobnb.model.*;
import autobnb.repository.AlquilerRepository;
import autobnb.repository.PagoRepository;
import autobnb.repository.UsuarioRepository;
import autobnb.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlquilerService {
    @Autowired
    private AlquilerRepository alquilerRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private VehiculoRepository vehiculoRepository;
    @Autowired
    private PagoRepository pagoRepository;

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
    public List<Alquiler> listadoCompleto() {
        return ((List<Alquiler>) alquilerRepository.findAll())
                .stream()
                .sorted(Comparator.comparingLong(Alquiler::getId))
                .collect(Collectors.toList());
    }

    /**
     * Crea y guarda un nuevo alquiler en la base de datos.
     *
     * @param fechaCreacion Fecha en que se crea el alquiler.
     * @param fechaEntrega Fecha de entrega del vehículo.
     * @param fechaDevolucion Fecha de devolución del vehículo.
     * @param precioFinal Precio final del alquiler.
     * @param vehiculoId ID del vehículo asociado al alquiler.
     * @param pagoId ID del pago asociado al alquiler.
     * @return El alquiler creado y guardado, o null si el vehículo o el pago no existen.
     */
    @Transactional
    public Alquiler crearAlquiler(Date fechaCreacion, Date fechaEntrega, Date fechaDevolucion, BigDecimal precioFinal, BigDecimal litrosCombustibles, Long vehiculoId, Long pagoId) {
        Optional<Vehiculo> vehiculo = vehiculoRepository.findById(vehiculoId);
        Optional<Pago> pago = pagoRepository.findById(pagoId);

        if (!vehiculo.isPresent() || !pago.isPresent()) {
            return null;
        }

        Alquiler nuevoAlquiler = new Alquiler(fechaCreacion, fechaEntrega, fechaDevolucion, precioFinal, vehiculo.get(), pago.get());
        if (litrosCombustibles != null) {
            nuevoAlquiler.setLitrosCombustible(litrosCombustibles);
        }
        return alquilerRepository.save(nuevoAlquiler);
    }
}
