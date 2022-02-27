package com.pokemon.pokedex.util;

import com.pokemon.pokedex.model.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class HelperUtil {

    private ConfigUtil configUtil;

    public HelperUtil(ConfigUtil configUtil) {
        this.configUtil = configUtil;
    }

    public String getPokemonApiUri(final String name) {
       return configUtil.getPokemonUri()+name;
   }

    public String getTranslationAPIUri(final boolean isLengendary, final String habitat, final String description) {
        String uri = getAPIUriType(isLengendary, habitat);

        return getAPIUri(uri, description);
    }

    public HttpEntity getHttpEntity(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        return new HttpEntity(headers);
    }

    private String getAPIUriType(final boolean isLengendary, final String habitat) {
        if (isLengendary || configUtil.getHabitat().equalsIgnoreCase(habitat)) {
            return configUtil.getYodaUri();
        }
        return configUtil.getShakespeareUri();
    }

    private String getAPIUri(final String uri, final String description){
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);
        builder.queryParam(configUtil.getTranslationQueryParamKey(),description);
       return  builder.toUriString();
    }
}
