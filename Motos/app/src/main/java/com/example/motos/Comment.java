package com.example.motos;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Comment implements Serializable {
    private int id;

    @SerializedName("comment_text")
    private String commentText;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("nombre_usuario")
    private String nombreUsuario;

    public Comment(int id, String commentText, String createdAt, String nombreUsuario) {
        this.id = id;
        this.commentText = commentText;
        this.createdAt = createdAt;
        this.nombreUsuario = nombreUsuario;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
}
