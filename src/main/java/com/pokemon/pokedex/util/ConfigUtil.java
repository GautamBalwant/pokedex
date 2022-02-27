package com.pokemon.pokedex.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigUtil {

    @Value("${pokemon.uri}")
    private String pokemonUri;

    @Value("${translation.yoda.uri}")
    private String yodaUri;


    @Value("${translation.shakespeare.uri}")
    private String shakespeareUri;

    @Value("${translation.query.param}")
    private String translationQueryParamKey;

    @Value("${habitat}")
    private String habitat;


    public String getPokemonUri() {
        return pokemonUri;
    }

    public String getYodaUri() {
        return yodaUri;
    }

    public String getShakespeareUri() {
        return shakespeareUri;
    }

    public String getTranslationQueryParamKey() {
        return translationQueryParamKey;
    }

    public String getHabitat() {
        return habitat;
    }
}
