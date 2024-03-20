package autobnb.repository;

import autobnb.model.Transmision;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TransmisionRepository extends CrudRepository<Transmision, Long> {
    Optional<Transmision> findByNombre(String nombre);
}
