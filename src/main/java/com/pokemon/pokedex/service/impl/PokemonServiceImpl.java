package com.pokemon.pokedex.service.impl;


import com.pokemon.pokedex.model.*;
import com.pokemon.pokedex.service.PokemonService;
import com.pokemon.pokedex.util.ConfigUtil;
import com.pokemon.pokedex.util.HelperUtil;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpEntity;

import org.springframework.http.HttpMethod;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;




@Service
public class PokemonServiceImpl implements PokemonService {

    private RestTemplate restTemplate;

    private HelperUtil helperUtil;

    public PokemonServiceImpl(RestTemplate restTemplate, HelperUtil helperUtil) {
        this.restTemplate = restTemplate;
        this.helperUtil = helperUtil;
    }

    @Override
    public ResponseDTO getPokemonByName(final String name) {
        return getPokemonDetails(name);
    }

    @Override
    public ResponseDTO getTranslatedDescription(final String name) {
        ResponseDTO response =getPokemonDetails(name);
        String translatedDescription = getTransalatedDescription(response.isLengendary(),response.getHabitat(),response.getDescription());
        response.setDescription(translatedDescription);
        return response;
    }

    private ResponseDTO getPokemonDetails(final String name){
        String uri =helperUtil.getPokemonApiUri(name);
        PokemonAPIResponse res =  restTemplate.getForObject(uri, PokemonAPIResponse.class);
        PokemonDTO pokemonDTO= res.getSpecies();
        PokemonSpeciesDTO pokemonSpeciesDTO = restTemplate.getForObject(pokemonDTO.getUrl(),PokemonSpeciesDTO.class);
        ResponseDTO response = getPokemonResponse(pokemonDTO.getName(),pokemonSpeciesDTO);
       return response;
    }

    private String getTransalatedDescription(final boolean isLengendary, final String habitat, final String description){
        String uri= helperUtil.getTranslationAPIUri(isLengendary,habitat,description);
        HttpEntity httpEntity = helperUtil.getHttpEntity();
        ResponseEntity<TranslationResponse> translated = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, TranslationResponse.class);
        TranslationResponse translationResponse= translated.getBody();
        return translationResponse.getContents().getTranslated();
    }

    private ResponseDTO getPokemonResponse(final String name, final PokemonSpeciesDTO pokemonSpeciesDTO) {
        ResponseDTO response = new ResponseDTO();
        Flavor flavor= pokemonSpeciesDTO.getFlavor_text_entries().get(0);
        response.setName(name);
        response.setHabitat(pokemonSpeciesDTO.getHabitat().getName());
        response.setLengendary(pokemonSpeciesDTO.isIs_legendary());
        response.setDescription(flavor.getFlavor_text());
        return response;
    }

}
