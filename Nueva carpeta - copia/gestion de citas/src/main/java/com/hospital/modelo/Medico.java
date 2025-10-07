package com.hospital.modelo;

public class Medico {
    private int id;
    private String nombre;
    private String especialidad;
    private String horarioDisponible;

    public Medico() {
    }

    public Medico(int id, String nombre, String especialidad, String horarioDisponible) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.horarioDisponible = horarioDisponible;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getHorarioDisponible() {
        return horarioDisponible;
    }

    public void setHorarioDisponible(String horarioDisponible) {
        this.horarioDisponible = horarioDisponible;
    }

    @Override
    public String toString() {
        return "Medico{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", especialidad='" + especialidad + '\'' +
                ", horarioDisponible='" + horarioDisponible + '\'' +
                '}';
    }
} 