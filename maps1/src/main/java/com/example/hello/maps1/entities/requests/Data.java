package com.example.hello.maps1.entities.requests;

import com.example.hello.maps1.entities.Courier;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Data implements Serializable {

    @JsonProperty("l")
    @SerializedName("l")
    private String login;

    @JsonProperty("p")
    @SerializedName("p")
    private String password;

    @JsonProperty("wid")
    @SerializedName("wid")
    private String wid;

    public Data() {}

    public Data(String wid) {
        this.wid = wid;
    }

    public Data(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public Data(Courier courier) {
        if (courier == null) return;

        this.login = courier.getLogin();
        this.password = courier.getPassword();
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }
}
