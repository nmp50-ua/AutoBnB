package autobnb.repository;

import autobnb.model.Modelo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ModeloRepository extends CrudRepository<Modelo, Long> {
    Optional<Modelo> findByNombre(String nombre);
    List<Modelo> findByMarcaId(Long marcaId);
}
