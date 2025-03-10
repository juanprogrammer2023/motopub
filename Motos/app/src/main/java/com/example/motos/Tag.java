package com.example.motos;

import com.google.gson.annotations.SerializedName;

public class Tag {
    @SerializedName("id")
    private int id;

    @SerializedName("nombre_etiqueta")
    private String nombreEtiqueta;

    // Constructor
    public Tag(int id, String nombreEtiqueta) {
        this.id = id;
        this.nombreEtiqueta = nombreEtiqueta;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombreEtiqueta() {
        return nombreEtiqueta;
    }

    // Setters (opcional)
    public void setId(int id) {
        this.id = id;
    }

    public void setNombreEtiqueta(String nombreEtiqueta) {
        this.nombreEtiqueta = nombreEtiqueta;
    }
}
