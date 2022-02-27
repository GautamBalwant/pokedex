package com.pokemon.pokedex.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pokemon.pokedex.model.ResponseDTO;
import org.springframework.stereotype.Service;


public interface PokemonService {

    public ResponseDTO getPokemonByName(String name);

    public ResponseDTO getTranslatedDescription(String name);
}
