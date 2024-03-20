package autobnb.repository;

import autobnb.model.Vehiculo;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VehiculoRepository extends CrudRepository<Vehiculo, Long> {
    Optional<Vehiculo> findByMatricula(String matricula);
}
