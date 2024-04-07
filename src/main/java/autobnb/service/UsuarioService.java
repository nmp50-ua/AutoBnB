package autobnb.service;

import autobnb.dto.UsuarioData;
import autobnb.model.Comentario;
import autobnb.model.Cuenta;
import autobnb.model.Pago;
import autobnb.model.Usuario;
import autobnb.repository.ComentarioRepository;
import autobnb.repository.CuentaRepository;
import autobnb.repository.PagoRepository;
import autobnb.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    public enum LoginStatus {LOGIN_OK, USER_NOT_FOUND, ERROR_PASSWORD}

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private CuentaRepository cuentaRepository;
    @Autowired
    private ComentarioRepository comentarioRepository;
    @Autowired
    private PagoRepository pagoRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public LoginStatus login(String email, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);

        if (!usuario.isPresent()) {
            return LoginStatus.USER_NOT_FOUND;
        } else if (!usuario.get().getPassword().equals(password)) {
            return LoginStatus.ERROR_PASSWORD;
        } else {
            return LoginStatus.LOGIN_OK;
        }
    }

    @Transactional
    public UsuarioData registrar(UsuarioData usuario) {
        Optional<Usuario> usuarioBD = usuarioRepository.findByEmail(usuario.getEmail());
        if (usuarioBD.isPresent())
            throw new UsuarioServiceException("El usuario " + usuario.getEmail() + " ya está registrado");
        else {
            Usuario usuarioNuevo = modelMapper.map(usuario, Usuario.class);
            usuarioNuevo = usuarioRepository.save(usuarioNuevo);
            return modelMapper.map(usuarioNuevo, UsuarioData.class);
        }
    }

    @Transactional(readOnly = true)
    public UsuarioData findByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null) return null;
        else {
            return modelMapper.map(usuario, UsuarioData.class);
        }
    }

    @Transactional(readOnly = true)
    public UsuarioData findById(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

        if (usuario == null) return null;
        else {
            return modelMapper.map(usuario, UsuarioData.class);
        }
    }

    // Método que devuelve el listado completo de objetos Usuario que hay en la base de datos.
    @Transactional(readOnly = true)
    public List<Usuario> listadoCompleto(){
        return (List<Usuario>) usuarioRepository.findAll();
    }

    // Método que busca un Usuario en una lista de Usuarios pasado por parámetro y un id concreto a buscar
    @Transactional(readOnly = true)
    public Usuario buscarUsuarioPorId(List<Usuario> usuarios, Long idBuscado) {
        for (Usuario usuario : usuarios) {
            if (usuario.getId().equals(idBuscado)) {
                return usuario; // Devuelve el usuario si se encuentra
            }
        }
        return null; // Devuelve null si no se encuentra el usuario
    }

    // Método que añade una cuenta a un usuario
    @Transactional
    public UsuarioData añadirCuenta(Long usuarioId, UsuarioData nuevosDatos) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(usuarioId);

        if (!usuarioExistente.isPresent()) {
            throw new UsuarioServiceException("El usuario con ID " + usuarioId + " no existe en la base de datos");
        }

        Usuario usuarioActualizado = usuarioExistente.get();

        // Actualiza los campos con los nuevos datos proporcionados
        if (nuevosDatos.getIdCuenta() != null) {
            Optional<Cuenta> cuenta = cuentaRepository.findById(nuevosDatos.getIdCuenta());
            usuarioActualizado.setCuenta(cuenta.get());
        }
        else{
            throw new UsuarioServiceException("Se ha recibido una cuenta NULL");
        }

        usuarioActualizado = usuarioRepository.save(usuarioActualizado);

        return modelMapper.map(usuarioActualizado, UsuarioData.class);
    }

    @Transactional
    public UsuarioData cambiarRolUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioServiceException("El usuario con ID " + usuarioId + " no se encuentra."));

        // Cambiar roles
        usuario.setEsArrendador(!usuario.isEsArrendador());
        usuario.setEsArrendatario(!usuario.isEsArrendatario());

        // Guardar el usuario actualizado
        usuario = usuarioRepository.save(usuario);

        // Convertir el usuario a UsuarioData para devolver
        return modelMapper.map(usuario, UsuarioData.class);
    }

    @Transactional
    public UsuarioData actualizarUsuarioPorId(Long usuarioId, UsuarioData nuevosDatos) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(usuarioId);

        if (!usuarioExistente.isPresent()) {
            throw new UsuarioServiceException("El usuario con ID " + usuarioId + " no existe en la base de datos");
        }

        Usuario usuarioActualizado = usuarioExistente.get();

        // Actualiza los campos con los nuevos datos proporcionados
        if (nuevosDatos.getNombre() != null) {
            usuarioActualizado.setNombre(nuevosDatos.getNombre());
        }
        else{
            throw new UsuarioServiceException("Se ha recibido un nombre NULL");
        }

        if (nuevosDatos.getPassword() != null) {
            usuarioActualizado.setPassword(nuevosDatos.getPassword());
        }
        else{
            throw new UsuarioServiceException("Se ha recibido un password NULL");
        }

        if (nuevosDatos.getEmail() != null) {
            usuarioActualizado.setEmail(nuevosDatos.getEmail());
        }
        else{
            throw new UsuarioServiceException("Se ha recibido un email NULL");
        }

        if (nuevosDatos.getTelefono() != null) {
            usuarioActualizado.setTelefono(nuevosDatos.getTelefono());
        }
        else{
            throw new UsuarioServiceException("Se ha recibido un telefono NULL");
        }

        if (nuevosDatos.getCodigoPostal() != null) {
            usuarioActualizado.setCodigoPostal(nuevosDatos.getCodigoPostal());
        }
        else{
            throw new UsuarioServiceException("Se ha recibido un codigo postal NULL");
        }

        if (nuevosDatos.getCiudad() != null) {
            usuarioActualizado.setCiudad(nuevosDatos.getCiudad());
        }
        else{
            throw new UsuarioServiceException("Se ha recibido una ciudad NULL");
        }

        if (nuevosDatos.getDireccion() != null) {
            usuarioActualizado.setDireccion(nuevosDatos.getDireccion());
        }
        else{
            throw new UsuarioServiceException("Se ha recibido una direccion NULL");
        }

        if (nuevosDatos.getDni() != null) {
            usuarioActualizado.setDni(nuevosDatos.getDni());
        }
        else{
            throw new UsuarioServiceException("Se ha recibido un dni NULL");
        }

        if (nuevosDatos.getFechaCaducidadDni() != null) {
            usuarioActualizado.setFechaCaducidadDni(nuevosDatos.getFechaCaducidadDni());
        }
        else{
            throw new UsuarioServiceException("Se ha recibido una fecha de caducidad de dni NULL");
        }

        if (nuevosDatos.getFechaCarnetConducir() != null) {
            usuarioActualizado.setFechaCarnetConducir(nuevosDatos.getFechaCarnetConducir());
        }
        else{
            throw new UsuarioServiceException("Se ha recibido una fecha de carnet de conducir NULL");
        }

        usuarioActualizado.setApellidos(nuevosDatos.getApellidos());
        usuarioActualizado.setImagen(nuevosDatos.getImagen());

        usuarioActualizado = usuarioRepository.save(usuarioActualizado);

        return modelMapper.map(usuarioActualizado, UsuarioData.class);
    }

    // Método para obtener todos los comentarios de un usuario específico
    @Transactional(readOnly = true)
    public List<Comentario> obtenerComentariosPorUsuarioId(Long usuarioId) {
        return comentarioRepository.findByUsuarioId(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<Pago> obtenerPagosPorUsuarioId(Long usuarioId) {
        return pagoRepository.findByUsuarioId(usuarioId);
    }
}
