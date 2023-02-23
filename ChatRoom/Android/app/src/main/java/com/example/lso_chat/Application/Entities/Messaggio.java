package com.example.lso_chat.Application.Entities;

import java.time.LocalDateTime;
import java.time.LocalTime;


public class Messaggio{

    private  String corpo;
    private  LocalDateTime tempo_invio;
    private  Utente mittente;

    public Messaggio(String corpo, LocalDateTime tempo_invio, Utente mittente) {
        this.corpo = corpo;
        this.tempo_invio = tempo_invio;
        this.mittente = mittente;
    }

    public String getCorpo() { return corpo; }

    public LocalDateTime getTempo() { return tempo_invio; }

    public Utente getMittente() {return mittente; }

}
