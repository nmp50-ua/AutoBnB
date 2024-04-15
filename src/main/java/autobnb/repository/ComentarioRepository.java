package autobnb.repository;

import autobnb.model.Comentario;
import autobnb.model.Usuario;
import autobnb.model.Vehiculo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ComentarioRepository extends CrudRepository<Comentario, Long> {
    Optional<Comentario> findByUsuario(Usuario usuario);
    Optional<Comentario> findByVehiculo(Vehiculo vehiculo);
    List<Comentario> findByUsuarioId(Long usuarioId);
    List<Comentario> findByVehiculoId(Long vehiculoId);
}
