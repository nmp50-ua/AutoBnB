package autobnb.repository;

import autobnb.model.Pago;
import autobnb.model.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface VehiculoRepository extends PagingAndSortingRepository<Vehiculo, Long>, JpaSpecificationExecutor<Vehiculo> {
    Optional<Vehiculo> findByMatricula(String matricula);
    List<Vehiculo> findByUsuarioId(Long usuarioId);
    Page<Vehiculo> findByMarcaNombre(String marca, Pageable pageable);
    Page<Vehiculo> findByMarcaNombreAndModeloNombre(String marca, String modelo, Pageable pageable);
    @Query("SELECT v FROM Vehiculo v WHERE v.oferta IS NOT NULL")
    Page<Vehiculo> findAllWithOferta(Pageable pageable);
    Page<Vehiculo> findByMarcaNombreAndOfertaIsNotNull(String marca, Pageable pageable);
    Page<Vehiculo> findByMarcaNombreAndModeloNombreAndOfertaIsNotNull(String marca, String modelo, Pageable pageable);
    Page<Vehiculo> findByUsuarioId(Long usuarioId, Pageable pageable);

}
