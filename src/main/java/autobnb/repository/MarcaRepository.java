package autobnb.repository;

import autobnb.model.Marca;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MarcaRepository extends CrudRepository<Marca, Long> {
    Optional<Marca> findByNombre(String nombre);
}
