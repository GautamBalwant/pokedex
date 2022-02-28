package com.pokemon.pokedex.util;

import com.pokemon.pokedex.model.Error;
import com.pokemon.pokedex.model.Habitat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HelperUtilTest {

    private HelperUtil helperUtil;

    @Mock
    private ConfigUtil configUtil;

    @Test
    public void getPokemonApiUriTest() {
        helperUtil = new HelperUtil(configUtil);
        when(configUtil.getPokemonUri()).thenReturn("http://localhost:8080/pokemon/");
        String result = helperUtil.getPokemonApiUri("mewtwo");
        assertNotNull(result);
        assertEquals("http://localhost:8080/pokemon/mewtwo", result);

    }

    @Test
    public void getTranslationAPIUriForLegendTest() {
        helperUtil = new HelperUtil(configUtil);
        when(configUtil.getTranslationQueryParamKey()).thenReturn("text");
        when(configUtil.getYodaUri()).thenReturn(getTranslationYodaAPI());

        String result = helperUtil.getTranslationAPIUri(true, "rare", "test");
        assertNotNull(result);
        assertEquals("https://api.funtranslations.com/translate/yoda.json?text=test", result);

    }

    @Test
    public void getTranslationAPIUriForCaveHabitatTest() {
        helperUtil = new HelperUtil(configUtil);
        when(configUtil.getTranslationQueryParamKey()).thenReturn("text");
        when(configUtil.getYodaUri()).thenReturn(getTranslationYodaAPI());
        when(configUtil.getHabitat()).thenReturn("cave");

        String result = helperUtil.getTranslationAPIUri(false, "cave", "test");
        assertNotNull(result);
        assertEquals("https://api.funtranslations.com/translate/yoda.json?text=test", result);

    }

    @Test
    public void getTranslationAPIUriForShakespeareTest() {
        helperUtil = new HelperUtil(configUtil);
        when(configUtil.getTranslationQueryParamKey()).thenReturn("text");
        when(configUtil.getShakespeareUri()).thenReturn(getTranslationShakespeareAPI());
        when(configUtil.getHabitat()).thenReturn("cave");

        String result = helperUtil.getTranslationAPIUri(false, "rare", "test");
        assertNotNull(result);
        assertEquals("https://api.funtranslations.com/translate/shakespeare.json?text=test", result);

    }

    @Test
    public void getHttpErrorResponseInCaseOfServerErrorTest() {
        helperUtil = new HelperUtil(configUtil);
        HttpServerErrorException serverErrorException = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        Error error = helperUtil.getHttpErrorResponse(serverErrorException);

        assertNotNull(error);
        assertEquals("Internal Server exception. Please try later", error.getMessage());
        assertEquals(500, error.getHttpErrorCode());
    }

    @Test
    public void getHttpErrorResponseInCaseOfTooManyRequestTest() {
        helperUtil = new HelperUtil(configUtil);
        HttpClientErrorException serverErrorException = new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS);
        Error error = helperUtil.getHttpErrorResponse(serverErrorException);

        assertNotNull(error);
        assertEquals("Too many request, Please try after 1 minute", error.getMessage());
        assertEquals(429, error.getHttpErrorCode());
    }

    @Test
    public void getHttpErrorResponseInCaseOfBadRequestTest() {
        helperUtil = new HelperUtil(configUtil);
        HttpClientErrorException serverErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        Error error = helperUtil.getHttpErrorResponse(serverErrorException);

        assertNotNull(error);
        assertEquals("Bad request", error.getMessage());
        assertEquals(400, error.getHttpErrorCode());
    }

    @Test
    public void getHabitatTest() {
        helperUtil = new HelperUtil(configUtil);
        Habitat habitat = new Habitat();
        habitat.setName("rare");
        String result = helperUtil.getHabitat(habitat);
        assertNotNull(result);
        assertEquals("rare", result);
    }

    @Test
    public void getHabitatWhenNoHabitatTest() {
        helperUtil = new HelperUtil(configUtil);

        String result = helperUtil.getHabitat(null);
        assertNotNull(result);
        assertEquals("not found", result);
    }

    private String getTranslationYodaAPI() {
        return "https://api.funtranslations.com/translate/yoda.json";
    }

    private String getTranslationShakespeareAPI() {
        return "https://api.funtranslations.com/translate/shakespeare.json";
    }

}