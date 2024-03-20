package autobnb.repository;

import autobnb.model.Color;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ColorRepository extends CrudRepository<Color, Long> {
    Optional<Color> findByNombre(String nombre);
}
