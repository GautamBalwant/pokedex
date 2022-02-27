package com.pokemon.pokedex.controller;

import com.pokemon.pokedex.model.ResponseDTO;
import com.pokemon.pokedex.service.PokemonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PokemonRestControllerTest {

    private PokemonRestController pokemonRestController;

    @Mock
    private PokemonService pokemonService;

    @Test
    public void getPokemonByNameTest(){
        pokemonRestController = new PokemonRestController(pokemonService);

        when(pokemonService.getPokemonByName("mewtwo")).thenReturn(getResponseDTO());
        ResponseDTO responseDTO =  pokemonRestController.getPokemonByName("mewtwo");
        assertNotNull(responseDTO);
        assertEquals("mewtwo",responseDTO.getName());
        assertEquals("rare",responseDTO.getHabitat());
        assertEquals(true,responseDTO.isLengendary());
        assertEquals("test",responseDTO.getDescription());
    }

    @Test
    public void getTranslatedDescriptionTest(){
        pokemonRestController = new PokemonRestController(pokemonService);

        when(pokemonService.getTranslatedDescription("mewtwo")).thenReturn(getResponseDTO());
        ResponseDTO responseDTO =  pokemonRestController.getTranslatedPokemonDescription("mewtwo");
        assertNotNull(responseDTO);
        assertEquals("mewtwo",responseDTO.getName());
        assertEquals("rare",responseDTO.getHabitat());
        assertEquals(true,responseDTO.isLengendary());
        assertEquals("test",responseDTO.getDescription());
    }


    private ResponseDTO getResponseDTO(){
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setName("mewtwo");
        responseDTO.setLengendary(true);
        responseDTO.setHabitat("rare");
        responseDTO.setDescription("test");
        return  responseDTO;
    }


}