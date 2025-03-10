package com.example.motos;

public class User {
    private String nombre_usuario;
    private String correo;
    private String contrasena;
    private Integer edad;

    // Constructor para registro (nombre, correo, contraseña, edad)
    public User(String nombre_usuario, String correo, String contrasena, Integer edad) {
        this.nombre_usuario = nombre_usuario;
        this.correo = correo;
        this.contrasena = contrasena;
        this.edad = edad;
    }

    // Constructor para login (solo correo y contraseña)
    public User(String correo, String contrasena) {
        this.correo = correo;
        this.contrasena = contrasena;
    }

    // Getters y setters
    public String getCorreo() {
        return correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getNombreUsuario() {
        return nombre_usuario;
    }

    public Integer getEdad() {
        return edad;
    }
}
