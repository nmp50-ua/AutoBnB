package autobnb.repository;

import autobnb.model.Pago;
import autobnb.model.Vehiculo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface VehiculoRepository extends CrudRepository<Vehiculo, Long> {
    Optional<Vehiculo> findByMatricula(String matricula);
    List<Vehiculo> findByUsuarioId(Long usuarioId);
    List<Vehiculo> findByMarcaNombre(String marca);
    List<Vehiculo> findByCategoriaNombre(String categoria);
}
