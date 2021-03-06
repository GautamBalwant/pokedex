package com.pokemon.pokedex.service.impl;

import com.pokemon.pokedex.model.Error;
import com.pokemon.pokedex.model.*;
import com.pokemon.pokedex.util.HelperUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PokemonServiceImplTest {

    private PokemonServiceImpl pokemonService;

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private HelperUtil helperUtil;
    @Mock
    private Cache cache;


    @Test
    public void getPokemonDetailTest(){
        pokemonService = new PokemonServiceImpl(restTemplate, cacheManager, helperUtil);
        String name= "mewtwo";
        when(helperUtil.getPokemonApiUri(name)).thenReturn("http://localhost:8080/pokemon/mewtwo");
        when(helperUtil.getHabitat(any())).thenReturn("rare");
        when(restTemplate.getForObject(getPokemonUri(),PokemonAPIResponse.class)).thenReturn(getPokemonAPIResponse());
        when(restTemplate.getForObject(getSpeciesUri(),PokemonSpeciesDTO.class)).thenReturn(getPokemonSpeciesDTO());
        ResponseDTO responseDTO = pokemonService.getPokemonByName(name);

        assertNotNull(responseDTO);
        assertEquals(true,responseDTO.isLengendary());
        assertEquals("rare",responseDTO.getHabitat());
        assertEquals("mewtwo", responseDTO.getName());
        assertNotNull(responseDTO.getDescription());
    }


    @Test
    public void getPokemonDetailInCaseOfServerExceptionTest(){
        pokemonService = new PokemonServiceImpl(restTemplate, cacheManager, helperUtil);
        String name= "mewtwo";
        when(helperUtil.getPokemonApiUri(name)).thenReturn("http://localhost:8080/pokemon/mewtwo");
        when(helperUtil.getHttpErrorResponse(any())).thenReturn(getError(500));
        when(restTemplate.getForObject(getPokemonUri(),PokemonAPIResponse.class))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        ResponseDTO responseDTO = pokemonService.getPokemonByName(name);
        assertNotNull(responseDTO);
      Error error = responseDTO.getError();
      assertNotNull(error);
      assertEquals(500,error.getHttpErrorCode());
      assertEquals("Internal Error",error.getMessage());
      assertNull(responseDTO.getDescription());
    }

    @Test
    public void getPokemonDetailInCaseOfClientExceptionTest(){
        pokemonService = new PokemonServiceImpl(restTemplate, cacheManager, helperUtil);
        String name= "abc";
        when(helperUtil.getPokemonApiUri(name)).thenReturn("http://localhost:8080/pokemon/mewtwo");
        when(helperUtil.getHttpErrorResponse(any())).thenReturn(getError(429));
        when(restTemplate.getForObject(getPokemonUri(),PokemonAPIResponse.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        ResponseDTO responseDTO = pokemonService.getPokemonByName(name);
        assertNotNull(responseDTO);
        Error error = responseDTO.getError();
        assertNotNull(error);
        assertEquals(429,error.getHttpErrorCode());
        assertEquals("Internal Error",error.getMessage());
        assertNull(responseDTO.getDescription());
    }

    @Test
    public void getTranslatedDescriptionTest(){
        pokemonService = new PokemonServiceImpl(restTemplate, cacheManager, helperUtil);
        String name= "mewtwo";
        HttpEntity entity = getHttpEntity();
        when(helperUtil.getPokemonApiUri(name)).thenReturn("http://localhost:8080/pokemon/mewtwo");
        when(helperUtil.getTranslationAPIUri(true,"rare",getPokemonDescription())).thenReturn(getTranslationAPI());
        when(helperUtil.getHttpEntity()).thenReturn(entity);
        when(helperUtil.getHabitat(any())).thenReturn("rare");
        when(cacheManager.getCache("pokemon")).thenReturn(cache);
        when(cache.get("mewtwo", ResponseDTO.class)).thenReturn(null);
        when(restTemplate.getForObject(getPokemonUri(),PokemonAPIResponse.class)).thenReturn(getPokemonAPIResponse());
        when(restTemplate.getForObject(getSpeciesUri(),PokemonSpeciesDTO.class)).thenReturn(getPokemonSpeciesDTO());
        when(restTemplate.exchange(getTranslationAPI(), HttpMethod.GET, entity, TranslationResponse.class)).thenReturn(getTranslatedResponse());

        ResponseDTO responseDTO = pokemonService.getTranslatedDescription(name);

        assertNotNull(responseDTO);
        assertEquals(true,responseDTO.isLengendary());
        assertEquals("rare",responseDTO.getHabitat());
        assertEquals("mewtwo", responseDTO.getName());
        assertNotNull(responseDTO.getDescription());
        assertEquals("translated",responseDTO.getDescription());
    }

    @Test
    public void getTranslatedDescriptionWhenFetchFromCacheTest(){
        pokemonService = new PokemonServiceImpl(restTemplate, cacheManager, helperUtil);
        String name= "mewtwo";
        HttpEntity entity = getHttpEntity();

        when(helperUtil.getTranslationAPIUri(true,"rare",getPokemonDescription())).thenReturn(getTranslationAPI());
        when(helperUtil.getHttpEntity()).thenReturn(entity);

        when(cacheManager.getCache("pokemon")).thenReturn(cache);
        when(cache.get("mewtwo", ResponseDTO.class)).thenReturn(getResponseDto());
        when(restTemplate.exchange(getTranslationAPI(), HttpMethod.GET, entity, TranslationResponse.class)).thenReturn(getTranslatedResponse());

        ResponseDTO responseDTO = pokemonService.getTranslatedDescription(name);

        assertNotNull(responseDTO);
        assertEquals(true,responseDTO.isLengendary());
        assertEquals("rare",responseDTO.getHabitat());
        assertEquals("mewtwo", responseDTO.getName());
        assertNotNull(responseDTO.getDescription());
        assertEquals("translated",responseDTO.getDescription());
    }

    @Test
    public void getTranslatedDescriptionInCaseOfErrorTest(){
        pokemonService = new PokemonServiceImpl(restTemplate, cacheManager, helperUtil);
        String name= "mewtwo";
        HttpEntity entity = getHttpEntity();
        when(helperUtil.getPokemonApiUri(name)).thenReturn("http://localhost:8080/pokemon/mewtwo");
        when(helperUtil.getHttpErrorResponse(any())).thenReturn(getError(500));
        when(restTemplate.getForObject(getPokemonUri(),PokemonAPIResponse.class))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        when(cacheManager.getCache("pokemon")).thenReturn(cache);
        when(cache.get("mewtwo", ResponseDTO.class)).thenReturn(null);

        ResponseDTO responseDTO = pokemonService.getTranslatedDescription(name);

        assertNotNull(responseDTO);
        Error error = responseDTO.getError();
        assertNotNull(error);
        assertEquals(500,error.getHttpErrorCode());
        assertEquals("Internal Error",error.getMessage());
        assertNull(responseDTO.getDescription());
    }

    @Test
    public void getTranslatedDescriptionWhenErrorInTranslationAPITest(){
        pokemonService = new PokemonServiceImpl(restTemplate, cacheManager, helperUtil);
        String name= "mewtwo";
        HttpEntity entity = getHttpEntity();
        when(helperUtil.getPokemonApiUri(name)).thenReturn("http://localhost:8080/pokemon/mewtwo");
        when(helperUtil.getTranslationAPIUri(true,"rare",getPokemonDescription())).thenReturn(getTranslationAPI());
        when(helperUtil.getHttpEntity()).thenReturn(entity);
        when(helperUtil.getHabitat(any())).thenReturn("rare");
        when(cacheManager.getCache("pokemon")).thenReturn(cache);
        when(cache.get("mewtwo", ResponseDTO.class)).thenReturn(null);
        when(restTemplate.getForObject(getPokemonUri(),PokemonAPIResponse.class)).thenReturn(getPokemonAPIResponse());
        when(restTemplate.getForObject(getSpeciesUri(),PokemonSpeciesDTO.class)).thenReturn(getPokemonSpeciesDTO());
        when(restTemplate.exchange(getTranslationAPI(), HttpMethod.GET, entity, TranslationResponse.class)).thenThrow(new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS));

        ResponseDTO responseDTO = pokemonService.getTranslatedDescription(name);

        assertNotNull(responseDTO);
        assertEquals(true,responseDTO.isLengendary());
        assertEquals("rare",responseDTO.getHabitat());
        assertEquals("mewtwo", responseDTO.getName());
        assertNotNull(responseDTO.getDescription());
        assertEquals(getPokemonDescription(),responseDTO.getDescription());
    }

private String getTranslationAPI(){
        return "https://api.funtranslations.com/translate/yoda.json";
}

    private String getName(){
        return "mewtwo";
    }

    private String getPokemonUri(){
        return "http://localhost:8080/pokemon/mewtwo";
    }

    private  PokemonAPIResponse getPokemonAPIResponse(){
        PokemonAPIResponse res = new PokemonAPIResponse();
       res.setSpecies(getPokemonDTO());
        return res;
    }

    private PokemonDTO getPokemonDTO(){
        PokemonDTO pokemonDTO = new PokemonDTO();
        pokemonDTO.setName("mewtwo");
        pokemonDTO.setUrl(getSpeciesUri());
        return  pokemonDTO;
    }

    private String getSpeciesUri(){
        return "https://pokeapi.co/api/v2/pokemon-species/150/";
    }

    private PokemonSpeciesDTO getPokemonSpeciesDTO(){
        PokemonSpeciesDTO pokemonSpeciesDTO = new PokemonSpeciesDTO();
        pokemonSpeciesDTO.setHabitat(getHabitat());
        pokemonSpeciesDTO.setIs_legendary(true);
        pokemonSpeciesDTO.setFlavor_text_entries(getDescriptionList());
return pokemonSpeciesDTO;
    }

    private ResponseDTO getResponseDto(){
        ResponseDTO response = new ResponseDTO();
        response.setName("mewtwo");
        response.setLengendary(true);
        response.setDescription(getPokemonDescription());
        response.setHabitat("rare");
        return response;
    }

    private List<Flavor> getDescriptionList(){
        List<Flavor> flavors = new ArrayList<>();
        Flavor flavor = new Flavor();
        flavor.setFlavor_text(getPokemonDescription());
        flavors.add(flavor);
        return  flavors;
    }

    private String getPokemonDescription(){
       return  "It was created by a scientist after years of horrific gene splicing and DNA engineering experiments.";
    }

    private Habitat getHabitat(){
        Habitat habitat = new Habitat();
        habitat.setName("rare");
        return habitat;
    }

    private HttpEntity getHttpEntity(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        return new HttpEntity(headers);
    }

    private ResponseEntity<TranslationResponse> getTranslatedResponse(){
        TranslationResponse response = new TranslationResponse();
        TranslatedContents contents = new TranslatedContents();
        contents.setTranslated("translated");
        contents.setTranslation("yoda");
        contents.setText(getPokemonDescription());
        response.setContents(contents);
        return ResponseEntity.of(Optional.of(response));

    }

    private Error getError(int code){

        Error error = new Error();
        error.setMessage("Internal Error");
        error.setHttpErrorCode(code);

        return  error;
    }
}