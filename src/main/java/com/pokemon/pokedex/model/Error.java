package com.pokemon.pokedex.model;

public class Error {

    private String message;
    private int httpErrorCode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getHttpErrorCode() {
        return httpErrorCode;
    }

    public void setHttpErrorCode(int httpErrorCode) {
        this.httpErrorCode = httpErrorCode;
    }
}
