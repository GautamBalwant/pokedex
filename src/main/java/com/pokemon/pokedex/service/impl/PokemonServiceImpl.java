package com.pokemon.pokedex.service.impl;


import com.pokemon.pokedex.model.*;
import com.pokemon.pokedex.service.PokemonService;
import com.pokemon.pokedex.util.HelperUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * The Service class , to call the Pokemon  and Translation api and storing result in memory cache.
 */
@Service
public class PokemonServiceImpl implements PokemonService {

    private final RestTemplate restTemplate;

    private final HelperUtil helperUtil;

    public PokemonServiceImpl(RestTemplate restTemplate, HelperUtil helperUtil) {
        this.restTemplate = restTemplate;
        this.helperUtil = helperUtil;
    }

    /**
     * The method to get the Pokemon details by name.
     * Store the result in in-memory cache
     *
     * @param name
     * @return ResponseDTO
     */
    @Override
    @Cacheable(value = "pokemon", key = "#name")
    public ResponseDTO getPokemonByName(final String name) {
        try {
            return getPokemonDetails(name);
        } catch (HttpClientErrorException | HttpServerErrorException exception) {
            ResponseDTO responseDTO = new ResponseDTO();
            responseDTO.setError(helperUtil.getHttpErrorResponse(exception));
            return responseDTO;
        }
    }

    /**
     * Get translated description of Pokemon. Get the detail using Pokemon api by name and then call the translation api.
     *
     * @param name
     * @return ResponseDTO
     */
    @Override
    @Cacheable(value = "translation", key = "#name")
    public ResponseDTO getTranslatedDescription(final String name) {
        try {
            ResponseDTO response = getPokemonDetails(name);
            String translatedDescription = getTransalatedDescription(response.isLengendary(), response.getHabitat(), response.getDescription());
            response.setDescription(translatedDescription);
            return response;
        } catch (HttpClientErrorException | HttpServerErrorException exception) {
            ResponseDTO responseDTO = new ResponseDTO();
            responseDTO.setError(helperUtil.getHttpErrorResponse(exception));
            return responseDTO;
        }
    }

    /**
     * This method is use to call the Pokemon API to get the detail by name
     * and then call the Pokemon species api to get the habitat , description and status of Pokemon
     *
     * @param name
     * @return ResponseDTO
     */
    private ResponseDTO getPokemonDetails(final String name) {
        String uri = helperUtil.getPokemonApiUri(name);
        PokemonAPIResponse res = restTemplate.getForObject(uri, PokemonAPIResponse.class);
        PokemonDTO pokemonDTO = res.getSpecies();
        PokemonSpeciesDTO pokemonSpeciesDTO = restTemplate.getForObject(pokemonDTO.getUrl(), PokemonSpeciesDTO.class);
        return getPokemonResponse(pokemonDTO.getName(), pokemonSpeciesDTO);
    }

    /**
     * The method to call the Translation api based on habitat and the pokemon status.
     * In case of error default description of Pokemon is returned.
     *
     * @param isLengendary
     * @param habitat
     * @param description
     * @return String
     */
    private String getTransalatedDescription(final boolean isLengendary, final String habitat, final String description) {
        try {
            String uri = helperUtil.getTranslationAPIUri(isLengendary, habitat, description);
            HttpEntity httpEntity = helperUtil.getHttpEntity();
            ResponseEntity<TranslationResponse> translated = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, TranslationResponse.class);
            TranslationResponse translationResponse = translated.getBody();
            return translationResponse.getContents().getTranslated();
        } catch (Exception exception) {
            return description;
        }
    }

    /**
     * The method is used to get the response Object.
     *
     * @param name
     * @param pokemonSpeciesDTO
     * @return ResponseDTO
     */
    private ResponseDTO getPokemonResponse(final String name, final PokemonSpeciesDTO pokemonSpeciesDTO) {
        ResponseDTO response = new ResponseDTO();
        response.setName(name);
        response.setHabitat(helperUtil.getHabitat(pokemonSpeciesDTO.getHabitat()));
        response.setLengendary(pokemonSpeciesDTO.isIs_legendary());
        response.setDescription(getDescription(pokemonSpeciesDTO.getFlavor_text_entries()));
        return response;
    }

    /**
     * Getting the first description element only and remove new line or tabs char.
     * @param flavor
     * @return
     */
    private String getDescription(final List<Flavor> flavor){
        Flavor flavortext = flavor.get(0);
        return flavortext.getFlavor_text().replaceAll("[\\n\\f\\t]", " ");
    }
}
