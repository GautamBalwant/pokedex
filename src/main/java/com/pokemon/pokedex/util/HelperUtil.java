package com.pokemon.pokedex.util;

import com.pokemon.pokedex.model.Error;
import com.pokemon.pokedex.model.Habitat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.Charset;

/**
 * Utility class use to provide common functions
 */
@Component
public class HelperUtil {

    private final ConfigUtil configUtil;

    public HelperUtil(ConfigUtil configUtil) {
        this.configUtil = configUtil;
    }

    /**
     * Get Pokemon api uri
     *
     * @param name
     * @return
     */
    public String getPokemonApiUri(final String name) {
        return configUtil.getPokemonUri() + name;
    }

    /**
     * Get Translation api uri based on habitat and pokemon status
     * @param isLengendary
     * @param habitat
     * @param description
     * @return
     */
    public String getTranslationAPIUri(final boolean isLengendary, final String habitat, final String description) {
        String uri = getAPIUriType(isLengendary, habitat);

        return getAPIUri(uri, description);
    }

    /**
     * Get HttpEntity
     * @return
     */
    public HttpEntity getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        return new HttpEntity(headers);
    }

    /**
     * Get the error object based on the Http error observed
     * @param exception
     * @return
     */
    public Error getHttpErrorResponse(final HttpStatusCodeException exception) {
        Error error = new Error();

        if (exception.getStatusCode().value() >= 500) {
            error.setMessage("Internal Server exception. Please try later");
            error.setHttpErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return error;
        }
        if (exception.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
            error.setMessage("Too many request, Please try after 1 minute");
            error.setHttpErrorCode(HttpStatus.TOO_MANY_REQUESTS.value());
            return error;
        }

        error.setMessage("Bad request");
        error.setHttpErrorCode(HttpStatus.BAD_REQUEST.value());
        return error;

    }

    /**
     * Get habitat of pokemon , in case there is no habitat return "not found"
     * @param habitat
     * @return
     */
    public String getHabitat(final Habitat habitat) {
        if (null != habitat && null != habitat.getName()) {
            return habitat.getName();
        }
        return "not found";

    }

    /**
     * Get Translation api uri type(ie yoda or Shakespeare) based on habitat and pokemon status
     * @param isLengendary
     * @param habitat
     * @return
     */
    private String getAPIUriType(final boolean isLengendary, final String habitat) {
        if (isLengendary || configUtil.getHabitat().equalsIgnoreCase(habitat)) {
            return configUtil.getYodaUri();
        }
        return configUtil.getShakespeareUri();
    }

    /**
     * Get the api uri with query param for Translation API
     * @param uri
     * @param description
     * @return
     */
    private String getAPIUri(final String uri, final String description) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);

        builder.queryParam(configUtil.getTranslationQueryParamKey(), description);
        return builder.encode().toUriString();
    }


}
