package com.pokemon.pokedex.model;

import java.util.List;

public class PokemonSpeciesDTO {

    private List<Flavor> flavor_text_entries;
    private boolean is_legendary;
    private Habitat habitat;

    public List<Flavor> getFlavor_text_entries() {
        return flavor_text_entries;
    }

    public void setFlavor_text_entries(List<Flavor> flavor_text_entries) {
        this.flavor_text_entries = flavor_text_entries;
    }

    public boolean isIs_legendary() {
        return is_legendary;
    }

    public void setIs_legendary(boolean is_legendary) {
        this.is_legendary = is_legendary;
    }

    public Habitat getHabitat() {
        return habitat;
    }

    public void setHabitat(Habitat habitat) {
        this.habitat = habitat;
    }
}
