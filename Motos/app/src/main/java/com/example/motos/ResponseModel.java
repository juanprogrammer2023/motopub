package com.example.motos;

public class ResponseModel {
    private String message;
    private String error;
    private String nombreUsuario;
    private String correo;
    private String edad;
    private int user_id; // Agregar el campo user_id

    // Getters
    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public String getEdad() {
        return edad;
    }

    public int getUserId() { // Getter para user_id
        return user_id;
    }

    // Setters (si es necesario)
    public void setMessage(String message) {
        this.message = message;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public void setUserId(int user_id) { // Setter para user_id
        this.user_id = user_id;
    }
}
