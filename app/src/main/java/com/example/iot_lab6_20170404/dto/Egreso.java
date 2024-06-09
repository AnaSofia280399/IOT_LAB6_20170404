package com.example.iot_lab6_20170404.dto;

public class Egreso {

    private String descripcion;
    private String fecha;
    private String titulo;
    private Float monto;

    public Egreso(String descripcion, String fecha, String titulo, Float monto) {
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.titulo = titulo;
        this.monto = monto;
    }

    public Egreso(){

    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Float getMonto() {
        return monto;
    }

    public void setMonto(Float monto) {
        this.monto = monto;
    }
}
