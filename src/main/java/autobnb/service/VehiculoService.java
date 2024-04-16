package autobnb.service;

import autobnb.dto.VehiculoData;
import autobnb.model.*;
import autobnb.repository.*;
import autobnb.service.exception.UsuarioServiceException;
import autobnb.service.exception.VehiculoServiceException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VehiculoService {
    @Autowired
    private VehiculoRepository vehiculoRepository;
    @Autowired
    private ComentarioRepository comentarioRepository;
    @Autowired
    private MarcaRepository marcaRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private ModeloRepository modeloRepository;
    @Autowired
    private ColorRepository colorRepository;
    @Autowired
    private TransmisionRepository transmisionRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private CategoriaService categoriaService;
    @Autowired
    private MarcaService marcaService;
    @Autowired
    private ModeloService modeloService;
    @Autowired
    private ColorService colorService;
    @Autowired
    private TransmisionService transmisionService;
    @Autowired
    private ComentarioService comentarioService;
    @Autowired
    private AlquilerService alquilerService;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<Vehiculo> listadoCompleto() {
        return ((List<Vehiculo>) vehiculoRepository.findAll())
                .stream()
                .sorted(Comparator.comparingLong(Vehiculo::getId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Vehiculo> listadoVehiculosConOferta() {
        return ((List<Vehiculo>) vehiculoRepository.findAll())
                .stream()
                .filter(vehiculo -> vehiculo.getOferta() != null)
                .sorted(Comparator.comparingLong(Vehiculo::getId))
                .limit(3) // Limita la cantidad de vehículos a 3
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Vehiculo> listadoVehiculosConOfertaCompleto() {
        return ((List<Vehiculo>) vehiculoRepository.findAll())
                .stream()
                .filter(vehiculo -> vehiculo.getOferta() != null)
                .sorted(Comparator.comparingLong(Vehiculo::getId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VehiculoData findById(Long vehiculoId) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId).orElse(null);

        if (vehiculo == null) return null;
        else {
            return modelMapper.map(vehiculo, VehiculoData.class);
        }
    }

    @Transactional(readOnly = true)
    public Vehiculo buscarVehiculoPorId(List<Vehiculo> vehiculos, Long idBuscado) {
        for (Vehiculo vehiculo : vehiculos) {
            if (vehiculo.getId().equals(idBuscado)) {
                return vehiculo;
            }
        }
        return null;
    }

    @Transactional
    public void actualizarVehiculo(Long vehiculoId, VehiculoData vehiculoData) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId).orElse(null);

        if (vehiculo != null) {
            vehiculo.setMatricula(vehiculoData.getMatricula());
            vehiculo.setDescripcion(vehiculoData.getDescripcion());
            vehiculo.setImagen(vehiculoData.getImagen());
            vehiculo.setKilometraje(vehiculoData.getKilometraje());
            vehiculo.setAnyoFabricacion(vehiculoData.getAnyoFabricacion());
            vehiculo.setCapacidadPasajeros(vehiculoData.getCapacidadPasajeros());
            vehiculo.setCapacidadMaletero(vehiculoData.getCapacidadMaletero());
            vehiculo.setNumeroPuertas(vehiculoData.getNumeroPuertas());
            vehiculo.setNumeroMarchas(vehiculoData.getNumeroMarchas());
            vehiculo.setAireAcondicionado(vehiculoData.isAireAcondicionado());
            vehiculo.setEnMantenimiento(vehiculoData.isEnMantenimiento());
            vehiculo.setOferta(vehiculoData.getOferta());
            vehiculo.setPrecioPorDia(vehiculoData.getPrecioPorDia());
            vehiculo.setPrecioPorMedioDia(vehiculoData.getPrecioPorMedioDia());
            vehiculo.setPrecioCombustible(vehiculoData.getPrecioCombustible());
            vehiculo.setCategoria(categoriaService.buscarCategoriaPorId(categoriaService.listadoCompleto(),vehiculoData.getIdCategoria()));
            vehiculo.setMarca(marcaService.buscarMarcaPorId(marcaService.listadoCompleto(),vehiculoData.getIdMarca()));
            vehiculo.setModelo(modeloService.buscarModeloPorId(modeloService.listadoCompleto(),vehiculoData.getIdModelo()));
            vehiculo.setColor(colorService.buscarColorPorId(colorService.listadoCompleto(),vehiculoData.getIdColor()));
            vehiculo.setTransmision(transmisionService.buscarTransmisionPorId(transmisionService.listadoCompleto(),vehiculoData.getIdTransmision()));

            vehiculoRepository.save(vehiculo);
        } else {
            throw new EntityNotFoundException("No se encontró el vehículo con ID: " + vehiculoId);
        }
    }

    @Transactional
    public void eliminarVehiculo(Long vehiculoId) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId).orElse(null);

        if (vehiculo != null) {
            Marca marca = vehiculo.getMarca();
            if (marca != null) {
                marca.getVehiculos().remove(vehiculo);
                marcaRepository.save(marca);
            }

            Modelo modelo = vehiculo.getModelo();
            if (modelo != null) {
                modelo.getVehiculos().remove(vehiculo);
                modeloRepository.save(modelo);
            }

            Transmision transmision = vehiculo.getTransmision();
            if (transmision != null ) {
                transmision.getVehiculos().remove(vehiculo);
                transmisionRepository.save(transmision);
            }

            Categoria categoria = vehiculo.getCategoria();
            if (categoria != null) {
                categoria.getVehiculos().remove(vehiculo);
                categoriaRepository.save(categoria);
            }

            Color color = vehiculo.getColor();
            if (color != null) {
                color.getVehiculos().remove(vehiculo);
                colorRepository.save(color);
            }

            Usuario usuario = vehiculo.getUsuario();
            if (usuario != null) {
                usuario.getVehiculos().remove(vehiculo);
                usuarioRepository.save(usuario);
            }

            vehiculo.getComentarios().forEach(comentario -> comentarioService.eliminarComentario(comentario.getId()));
            vehiculo.getAlquileres().forEach(alquiler -> alquilerService.eliminarAlquiler(alquiler.getId()));

            vehiculoRepository.delete(vehiculo);
        } else {
            throw new EntityNotFoundException("No se encontró el vehículo con ID: " + vehiculoId);
        }
    }

    @Transactional(readOnly = true)
    public VehiculoData findByMatricula(String matricula) {
        Vehiculo vehiculo = vehiculoRepository.findByMatricula(matricula).orElse(null);

        if (vehiculo == null) return null;
        else {
            return modelMapper.map(vehiculo, VehiculoData.class);
        }
    }

    @Transactional
    public VehiculoData registrarVehiculo(VehiculoData vehiculoData) {
        Optional<Vehiculo> vehiculoBD = vehiculoRepository.findByMatricula(vehiculoData.getMatricula());

        if (vehiculoBD.isPresent())
            throw new VehiculoServiceException("El vehiculo con matrícula (" + vehiculoData.getMatricula() + ") ya está registrado");
        else {
            Optional<Marca> marca = marcaRepository.findById(vehiculoData.getIdMarca());
            Optional<Categoria> categoria = categoriaRepository.findById(vehiculoData.getIdCategoria());
            Optional<Modelo> modelo = modeloRepository.findById(vehiculoData.getIdModelo());
            Optional<Color> color = colorRepository.findById(vehiculoData.getIdColor());
            Optional<Transmision> transmision = transmisionRepository.findById(vehiculoData.getIdTransmision());
            Optional<Usuario> usuario = usuarioRepository.findById(vehiculoData.getIdUsuario());

            Vehiculo vehiculoNuevo = modelMapper.map(vehiculoData, Vehiculo.class);
            vehiculoNuevo.setMarca(marca.orElseThrow(() -> new VehiculoServiceException("No se encontró la marca con ID: " + vehiculoData.getIdMarca())));
            vehiculoNuevo.setCategoria(categoria.orElseThrow(() -> new VehiculoServiceException("No se encontró la categoría con ID: " + vehiculoData.getIdCategoria())));
            vehiculoNuevo.setModelo(modelo.orElseThrow(() -> new VehiculoServiceException("No se encontró el modelo con ID: " + vehiculoData.getIdModelo())));
            vehiculoNuevo.setColor(color.orElseThrow(() -> new VehiculoServiceException("No se encontró el color con ID: " + vehiculoData.getIdColor())));
            vehiculoNuevo.setTransmision(transmision.orElseThrow(() -> new VehiculoServiceException("No se encontró la transmisión con ID: " + vehiculoData.getIdTransmision())));
            vehiculoNuevo.setUsuario(usuario.orElseThrow(() -> new UsuarioServiceException("No se encontró el usuario con ID: " + vehiculoData.getIdUsuario())));
            vehiculoNuevo = vehiculoRepository.save(vehiculoNuevo);
            return modelMapper.map(vehiculoNuevo, VehiculoData.class);
        }
    }

    @Transactional(readOnly = true)
    public List<Comentario> obtenerComentariosPorVehiculoId(Long vehiculoId) {
        return comentarioRepository.findByVehiculoId(vehiculoId);
    }

    @Transactional
    public Comentario agregarComentarioAVehiculo(Long vehiculoId, Long usuarioId, String descripcion) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado con ID: " + vehiculoId));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        Comentario nuevoComentario = new Comentario();
        nuevoComentario.setVehiculo(vehiculo);
        nuevoComentario.setUsuario(usuario);
        nuevoComentario.setDescripcion(descripcion);

        LocalDate hoy = LocalDate.now();
        Date fechaHoy = Date.from(hoy.atStartOfDay(ZoneId.systemDefault()).toInstant());
        nuevoComentario.setFechaCreacion(fechaHoy);

        comentarioRepository.save(nuevoComentario);

        return nuevoComentario;
    }


    // Métodos para la paginación de vehículos

    @Transactional(readOnly = true)
    public Page<Vehiculo> listadoPaginado(Pageable pageable) {
        return vehiculoRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Vehiculo> listadoPaginadoPorMarca(String marca, Pageable pageable) {
        return vehiculoRepository.findByMarcaNombre(marca, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Vehiculo> listadoPaginadoPorMarcaYModelo(String marca, String modelo, Pageable pageable) {
        return vehiculoRepository.findByMarcaNombreAndModeloNombre(marca, modelo, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Vehiculo> listadoPaginadoVehiculosConOfertaCompleto(Pageable pageable) {
        return vehiculoRepository.findAllWithOferta(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Vehiculo> filtrarVehiculosConPaginacion(String categoria, String color, String marca, String transmision, Pageable pageable) {
        Specification<Vehiculo> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (categoria != null && !categoria.isEmpty() && !categoria.equals("Categoria")) {
                predicates.add(criteriaBuilder.equal(root.get("categoria").get("nombre"), categoria));
            }
            if (color != null && !color.isEmpty() && !color.equals("Color")) {
                predicates.add(criteriaBuilder.equal(root.get("color").get("nombre"), color));
            }
            if (marca != null && !marca.isEmpty() && !marca.equals("Marca")) {
                predicates.add(criteriaBuilder.equal(root.get("marca").get("nombre"), marca));
            }
            if (transmision != null && !transmision.isEmpty() && !transmision.equals("Transmision")) {
                predicates.add(criteriaBuilder.equal(root.get("transmision").get("nombre"), transmision));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return vehiculoRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Vehiculo> buscarVehiculosPorMarcaConOferta(String marca, Pageable pageable) {
        return vehiculoRepository.findByMarcaNombreAndOfertaIsNotNull(marca, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Vehiculo> buscarVehiculosPorMarcaYModeloConOferta(String marca, String modelo, Pageable pageable) {
        return vehiculoRepository.findByMarcaNombreAndModeloNombreAndOfertaIsNotNull(marca, modelo, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Vehiculo> filtrarVehiculosEnOfertaPaginados(String categoria, String color, String marca, String transmision, Pageable pageable) {
        Specification<Vehiculo> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.isNotNull(root.get("oferta")));
            predicates.add(criteriaBuilder.gt(root.get("oferta"), 0));

            if (categoria != null && !categoria.isEmpty() && !categoria.equals("Categoria")) {
                predicates.add(criteriaBuilder.equal(root.get("categoria").get("nombre"), categoria));
            }
            if (color != null && !color.isEmpty() && !color.equals("Color")) {
                predicates.add(criteriaBuilder.equal(root.get("color").get("nombre"), color));
            }
            if (marca != null && !marca.isEmpty() && !marca.equals("Marca")) {
                predicates.add(criteriaBuilder.equal(root.get("marca").get("nombre"), marca));
            }
            if (transmision != null && !transmision.isEmpty() && !transmision.equals("Transmision")) {
                predicates.add(criteriaBuilder.equal(root.get("transmision").get("nombre"), transmision));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return vehiculoRepository.findAll(spec, pageable);
    }
}
