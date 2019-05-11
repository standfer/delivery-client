package com.example.hello.maps1.entities.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseIlkato implements Serializable {
    @JsonProperty("id_session")
    private int idSession;

    public ResponseIlkato() {
    }

    public int getIdSession() {
        return idSession;
    }

    public void setIdSession(int idSession) {
        this.idSession = idSession;
    }

    public boolean isEmpty() {
        return idSession == 0;
    }
}
