package com.pokemon.pokedex.model;

import java.io.Serializable;

public class ClientResponse implements Serializable {


    private Error error;


    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
