package com.pokemon.pokedex.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HelperUtilTest {

    private HelperUtil helperUtil;

    @Mock
    private ConfigUtil configUtil;

    @Test
    public void getPokemonApiUriTest(){
        helperUtil = new HelperUtil(configUtil);
        when(configUtil.getPokemonUri()).thenReturn("http://localhost:8080/pokemon/");
        String result = helperUtil.getPokemonApiUri("mewtwo");
        assertNotNull(result);
        assertEquals("http://localhost:8080/pokemon/mewtwo",result);

    }

    @Test
    public void getTranslationAPIUriForLegendTest(){
        helperUtil = new HelperUtil(configUtil);
        when(configUtil.getTranslationQueryParamKey()).thenReturn("text");
        when(configUtil.getYodaUri()).thenReturn(getTranslationYodaAPI());

        String result = helperUtil.getTranslationAPIUri(true,"rare","test");
        assertNotNull(result);
        assertEquals("https://api.funtranslations.com/translate/yoda.json?text=test",result);

    }

    @Test
    public void getTranslationAPIUriForCaveHabitatTest(){
        helperUtil = new HelperUtil(configUtil);
        when(configUtil.getTranslationQueryParamKey()).thenReturn("text");
        when(configUtil.getYodaUri()).thenReturn(getTranslationYodaAPI());
        when(configUtil.getHabitat()).thenReturn("cave");

        String result = helperUtil.getTranslationAPIUri(false,"cave","test");
        assertNotNull(result);
        assertEquals("https://api.funtranslations.com/translate/yoda.json?text=test",result);

    }

    @Test
    public void getTranslationAPIUriForShakespeareTest(){
        helperUtil = new HelperUtil(configUtil);
        when(configUtil.getTranslationQueryParamKey()).thenReturn("text");
        when(configUtil.getShakespeareUri()).thenReturn(getTranslationShakespeareAPI());
        when(configUtil.getHabitat()).thenReturn("cave");

        String result = helperUtil.getTranslationAPIUri(false,"rare","test");
        assertNotNull(result);
        assertEquals("https://api.funtranslations.com/translate/shakespeare.json?text=test",result);

    }

    private String getTranslationYodaAPI(){
        return "https://api.funtranslations.com/translate/yoda.json";
    }

    private String getTranslationShakespeareAPI(){
        return "https://api.funtranslations.com/translate/shakespeare.json";
    }

}