package autobnb.service;

import autobnb.dto.UsuarioData;
import autobnb.model.Cuenta;
import autobnb.model.Usuario;
import autobnb.repository.CuentaRepository;
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
}
