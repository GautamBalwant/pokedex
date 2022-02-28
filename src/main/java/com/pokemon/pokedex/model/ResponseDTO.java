package com.pokemon.pokedex.model;

public class ResponseDTO extends ClientResponse{

    private String name;
    private String description;
    private String habitat;
    private Boolean isLengendary;


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

    public Boolean isLengendary() {
        return isLengendary;
    }

    public void setLengendary(Boolean lengendary) {
        isLengendary = lengendary;
    }
}
