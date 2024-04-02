package autobnb.dto;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.util.Date;

public class RegistroData {

    @NotNull(message = "El nombre no puede ser nulo.")
    private String nombre;

    private String apellidos;

    @NotNull(message = "El email no puede ser nulo.")
    @Email(message = "Por favor, introduce una dirección de correo electrónico válida.")
    private String email;

    @NotNull(message = "La contraseña no puede ser nula.")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres.")
    private String password;

    @NotNull(message = "La contraseña no puede ser nula.")
    @Digits(integer = 9, fraction = 0, message = "El teléfono debe tener 9 dígitos.")
    private Integer telefono;

    @NotNull(message = "El DNI no puede ser nulo.")
    @Size(min = 9, max = 9, message = "El DNI debe contener 9 carácteres.")
    private String dni;

    @NotNull(message = "La fecha de caducidad del DNI no puede ser nula.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaCaducidadDni;

    @NotNull(message = "La dirección no puede ser nula.")
    @Size(max = 100, message = "La dirección no puede superar los 100 caracteres.")
    private String direccion;

    @NotNull(message = "La ciudad no puede ser nula.")
    @Size(max = 50, message = "La ciudad no debe superar los 50 caracteres.")
    private String ciudad;

    @NotNull(message = "El código postal no puede ser nulo.")
    @Min(value = 0, message = "El código postal debe ser mayor o igual a 0.")
    private Integer codigoPostal;

    @NotNull(message = "La fecha de expedición del carnet de conducir no puede ser nula.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaCarnetConducir;

    @NotNull(message = "El número de cuenta no puede ser nulo.")
    @Size(max = 50, message = "El número de cuenta debe tener 50 caracteres como máximo.")
    private String numeroCuenta;

    private String imagen;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getTelefono() {
        return telefono;
    }

    public void setTelefono(Integer telefono) {
        this.telefono = telefono;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Integer getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(Integer codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public Date getFechaCaducidadDni() {
        return fechaCaducidadDni;
    }

    public void setFechaCaducidadDni(Date fechaCaducidadDni) {
        this.fechaCaducidadDni = fechaCaducidadDni;
    }

    public Date getFechaCarnetConducir() {
        return fechaCarnetConducir;
    }

    public void setFechaCarnetConducir(Date fechaCarnetConducir) {
        this.fechaCarnetConducir = fechaCarnetConducir;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
