package com.pokemon.pokedex.service.impl;

import com.pokemon.pokedex.model.*;
import com.pokemon.pokedex.util.HelperUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PokemonServiceImplTest {

    private PokemonServiceImpl pokemonService;

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private HelperUtil helperUtil;


    @Test
    public void getPokemonDetailTest(){
        pokemonService = new PokemonServiceImpl(restTemplate,helperUtil);
        String name= "mewtwo";
        when(helperUtil.getPokemonApiUri(name)).thenReturn("http://localhost:8080/pokemon/mewtwo");
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
    public void getTranslatedDescriptionTest(){
        pokemonService = new PokemonServiceImpl(restTemplate,helperUtil);
        String name= "mewtwo";
        HttpEntity entity = getHttpEntity();
        when(helperUtil.getPokemonApiUri(name)).thenReturn("http://localhost:8080/pokemon/mewtwo");
        when(helperUtil.getTranslationAPIUri(true,"rare",getPokemonDescription())).thenReturn(getTranslationAPI());
        when(helperUtil.getHttpEntity()).thenReturn(entity);
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
}