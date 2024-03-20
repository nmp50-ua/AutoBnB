package autobnb.dto;

import javax.validation.constraints.*;

public class RegistroData {
    @NotNull(message = "El email no puede ser nulo.")
    @Email(message = "Por favor, introduce una dirección de correo electrónico válida.")
    private String eMail;

    @NotNull(message = "La contraseña no puede ser nula.")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres.")
    private String password;

    @NotNull(message = "El nombre no puede ser nulo.")
    private String nombre;

    private String apellidos;

    @Pattern(regexp = "\\d{1,20}", message = "El teléfono debe contener solo números y tener máximo 20 dígitos.")
    private String telefono;

    @Min(value = 0, message = "El código postal debe ser mayor o igual a 0.")
    private Integer codigopostal;

    private String pais;

    private String poblacion;

    private String direccion;

    private boolean admin;

    public String getEmail() {
        return eMail;
    }

    public void setEmail(String eMail) {
        this.eMail = eMail;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getCodigopostal() {
        return codigopostal;
    }

    public void setCodigopostal(Integer codigopostal) {
        this.codigopostal = codigopostal;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
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

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

}
