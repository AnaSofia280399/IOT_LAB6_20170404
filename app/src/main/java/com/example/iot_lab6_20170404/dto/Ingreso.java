package com.example.iot_lab6_20170404.dto;

public class Ingreso {

    private String descripcion;
    private String fecha;
    private String titulo;
    private Double monto;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Ingreso(String descripcion, String fecha, String titulo, Double monto, String id) {
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.titulo = titulo;
        this.monto = monto;
        this.id = id;
    }

    public Ingreso(){

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

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }
}
