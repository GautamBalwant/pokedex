package com.pokemon.pokedex.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.pokemon.pokedex.model.ResponseDTO;
import com.pokemon.pokedex.service.PokemonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@RestController
@RequestMapping("/pokemon")
public class PokemonRestController {


    private PokemonService pokemonService;

    public PokemonRestController(PokemonService service) {
        this.pokemonService = service;
    }

    /**
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/{name}")
    public ResponseDTO getPokemonByName(@PathVariable String name) {

        return pokemonService.getPokemonByName(name);
    }

    @GetMapping("/translated/{name}")
    public ResponseDTO getTranslatedPokemonDescription(@PathVariable String name){

        return pokemonService.getTranslatedDescription(name);
    }
}
