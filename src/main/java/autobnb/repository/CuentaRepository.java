package autobnb.repository;

import autobnb.model.Cuenta;
import autobnb.model.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CuentaRepository extends CrudRepository<Cuenta, Long> {
    Optional<Cuenta> findByUsuario(Usuario usuario);
}