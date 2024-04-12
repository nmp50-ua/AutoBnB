package autobnb.repository;

import autobnb.model.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AlquilerRepository extends CrudRepository<Alquiler, Long> {
    Optional<Alquiler> findByPago(Pago pago);
    Optional<Alquiler> findByVehiculo(Vehiculo vehiculo);
    List<Alquiler> findByVehiculoId(Long vehiculoId);
}
