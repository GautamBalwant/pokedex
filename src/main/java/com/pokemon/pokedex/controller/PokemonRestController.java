package com.pokemon.pokedex.controller;


import com.pokemon.pokedex.model.Error;
import com.pokemon.pokedex.model.ResponseDTO;
import com.pokemon.pokedex.service.PokemonService;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

/**
 * The Rest controller class , which is used to expose the api.
 */
@RestController
@RequestMapping("/pokemon")
public class PokemonRestController {

    private final PokemonService pokemonService;

    private final CacheManager cacheManager;


    public PokemonRestController(PokemonService service, CacheManager cacheManager) {
        this.pokemonService = service;
        this.cacheManager = cacheManager;
    }

    /**
     * Search pokemon details by name , in case of server error or too many request error the cache is removed for the pokemon
     * @param  name
     * @return ResponseEntity<ResponseDTO>
     *
     */
    @GetMapping(value = "/{name}")
    public ResponseEntity<ResponseDTO> getPokemonByName(@PathVariable String name) {

        ResponseDTO responseDTO = pokemonService.getPokemonByName(name);
        Error error = responseDTO.getError();
        if (error != null) {
            evictSingleCacheValue("pokemon", name, responseDTO.getError().getHttpErrorCode());
            return new ResponseEntity<ResponseDTO>(
                    responseDTO, new HttpHeaders(), HttpStatus.valueOf(error.getHttpErrorCode()));
        }

        return new ResponseEntity<ResponseDTO>(
                responseDTO, new HttpHeaders(), HttpStatus.OK);
    }

    /**
     * Search Translate Pokemon description , in case of server error or too many request error the cache is removed for the pokemon
     * @param  name
     * @return ResponseEntity<ResponseDTO>
     *
     */
    @GetMapping(value = "/translated/{name}")
    public ResponseEntity<ResponseDTO> getTranslatedPokemonDescription(@PathVariable String name) {

        ResponseDTO responseDTO = pokemonService.getTranslatedDescription(name);
        Error error = responseDTO.getError();
        if (responseDTO.getError() != null) {
            evictSingleCacheValue("translation", name, responseDTO.getError().getHttpErrorCode());
            return new ResponseEntity<ResponseDTO>(
                    responseDTO, new HttpHeaders(), HttpStatus.valueOf(error.getHttpErrorCode()));
        }

        return new ResponseEntity<ResponseDTO>(
                responseDTO, new HttpHeaders(), HttpStatus.OK);

    }

    /**
     * Method is used to remove cache for the pokemon when too many request or server error occurs.
     * @param cacheName
     * @param cacheKey
     * @param httpStatusCode
     */
    private void evictSingleCacheValue(final String cacheName, final String cacheKey, int httpStatusCode) {
        if (httpStatusCode == 429 || httpStatusCode >= 500) {
            cacheManager.getCache(cacheName).evict(cacheKey);
        }
    }
}
