package com.example.lso_chat.Application.Entities;

public class Richiesta {
    private String username;
    private int idstanza;
    private String nomestanza;

public Richiesta(int idstnz, String username, String nomestanza) {
    this.idstanza = idstnz;
    this.username = username;
    this.nomestanza= nomestanza;
    }

    public String getUsername() {
        return username;
    }

    public int getIdstanza() {
        return idstanza;
    }

    public String getNomestanza() {
        return nomestanza;
    }
}
