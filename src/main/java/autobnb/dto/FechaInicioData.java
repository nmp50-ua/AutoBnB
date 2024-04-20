package autobnb.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class FechaInicioData {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaInicial;

    // Getters y setters

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }
}
