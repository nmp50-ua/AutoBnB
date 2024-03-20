package autobnb.repository;

import autobnb.model.Categoria;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CategoriaRepository extends CrudRepository<Categoria, Long> {
    Optional<Categoria> findByNombre(String nombre);
}
