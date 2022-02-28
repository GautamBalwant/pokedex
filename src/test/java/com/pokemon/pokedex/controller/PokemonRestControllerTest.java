package com.pokemon.pokedex.controller;

import com.pokemon.pokedex.model.Error;
import com.pokemon.pokedex.model.ResponseDTO;
import com.pokemon.pokedex.service.PokemonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PokemonRestControllerTest {

    private PokemonRestController pokemonRestController;

    @Mock
    private PokemonService pokemonService;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache mockCache;

    @Test
    public void getPokemonByNameTest(){
        pokemonRestController = new PokemonRestController(pokemonService,cacheManager);

        when(pokemonService.getPokemonByName("mewtwo")).thenReturn(getResponseDTO());
        ResponseEntity<ResponseDTO> result =  pokemonRestController.getPokemonByName("mewtwo");
        assertNotNull(result);
        ResponseDTO responseDTO = result.getBody();
        assertNotNull(responseDTO);
        assertEquals("mewtwo",responseDTO.getName());
        assertEquals("rare",responseDTO.getHabitat());
        assertEquals(true,responseDTO.isLengendary());
        assertEquals("test",responseDTO.getDescription());
    }

    @Test
    public void getPokemonByInCaseOfServerErrorTest(){
        pokemonRestController = new PokemonRestController(pokemonService,cacheManager);
        String name="abc";
        when(pokemonService.getPokemonByName(name)).thenReturn(getError(500));
        when(cacheManager.getCache("pokemon")).thenReturn(mockCache);
       // doNothing().when(mockCache.evict(name));
        ResponseEntity<ResponseDTO> result =  pokemonRestController.getPokemonByName(name);
        assertNotNull(result);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,result.getStatusCode());
        ResponseDTO responseDTO = result.getBody();
        assertNotNull(responseDTO);
        Error error = responseDTO.getError();
        assertNotNull(error);
        assertEquals("Internal Server Error",error.getMessage());
    }

    @Test
    public void getTranslatedDescriptionTest(){
        pokemonRestController = new PokemonRestController(pokemonService,cacheManager);

        when(pokemonService.getTranslatedDescription("mewtwo")).thenReturn(getResponseDTO());
        ResponseEntity<ResponseDTO> result=  pokemonRestController.getTranslatedPokemonDescription("mewtwo");
        assertNotNull(result);
        ResponseDTO responseDTO = result.getBody();
        assertNotNull(responseDTO);
        assertEquals("mewtwo",responseDTO.getName());
        assertEquals("rare",responseDTO.getHabitat());
        assertEquals(true,responseDTO.isLengendary());
        assertEquals("test",responseDTO.getDescription());
    }

    @Test
    public void getTranslatedInCaseOfTooManyRequestErrorTest(){
        pokemonRestController = new PokemonRestController(pokemonService,cacheManager);

        when(pokemonService.getTranslatedDescription("mewtwo")).thenReturn(getError(429));
        when(cacheManager.getCache("translation")).thenReturn(mockCache);
        ResponseEntity<ResponseDTO> result=  pokemonRestController.getTranslatedPokemonDescription("mewtwo");

        assertNotNull(result);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS,result.getStatusCode());
        ResponseDTO responseDTO = result.getBody();
        assertNotNull(responseDTO);
        Error error = responseDTO.getError();
        assertNotNull(error);
        assertEquals("Internal Server Error",error.getMessage());

    }


    private ResponseDTO getResponseDTO(){
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setName("mewtwo");
        responseDTO.setLengendary(true);
        responseDTO.setHabitat("rare");
        responseDTO.setDescription("test");
        return  responseDTO;
    }

    private ResponseDTO getError(int code){
        ResponseDTO responseDTO = new ResponseDTO();
        Error error = new Error();
        error.setMessage("Internal Server Error");
        error.setHttpErrorCode(code);
        responseDTO.setError(error);
        return  responseDTO;
    }


}