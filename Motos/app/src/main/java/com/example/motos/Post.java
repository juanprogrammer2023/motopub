package com.example.motos;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Post {
    private int id;
    private String title;
    private String description;
    private String image;
    private String date;
    private int likes;

    @SerializedName("profile_name")
    private String profileName;

    // Lista para almacenar los IDs de las etiquetas asignadas
    private List<Integer> assignedTags;

    public Post() {
        // Inicializar la lista de etiquetas asignadas
        this.assignedTags = new ArrayList<>();
    }

    // Getters y Setters para cada campo
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    // Métodos para manejar las etiquetas asignadas
    public List<Integer> getAssignedTags() {
        return new ArrayList<>(assignedTags); // Devuelve una copia para evitar modificaciones externas
    }

    public void setAssignedTags(List<Tag> tags) {
        this.assignedTags.clear();
        if (tags != null) {
            for (Tag tag : tags) {
                this.assignedTags.add(tag.getId());
            }
        }
    }

    public void addAssignedTag(int tagId) {
        if (!assignedTags.contains(tagId)) {
            assignedTags.add(tagId);
        }
    }

    public void addAssignedTags(List<Integer> tagIds) {
        if (tagIds != null) {
            for (int tagId : tagIds) {
                addAssignedTag(tagId);
            }
        }
    }

    public void removeAssignedTag(int tagId) {
        assignedTags.remove(Integer.valueOf(tagId));
    }
}
