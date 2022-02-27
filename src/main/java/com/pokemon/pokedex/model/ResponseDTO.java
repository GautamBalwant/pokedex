package com.pokemon.pokedex.model;

public class ResponseDTO {

    private String name;
    private String description;
    private String habitat;
    private boolean isLengendary;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHabitat() {
        return habitat;
    }

    public void setHabitat(String habitat) {
        this.habitat = habitat;
    }

    public boolean isLengendary() {
        return isLengendary;
    }

    public void setLengendary(boolean lengendary) {
        isLengendary = lengendary;
    }
}
