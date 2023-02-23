package com.example.lso_chat.Application.Entities;

import androidx.annotation.Nullable;

public class Stanza{

    private final String nome;
    private int capacita;
    private final int codice;
    private Messaggio ultimo_msg;

//Eventualmente avra un array dei partecipanti, nel caso dovesse servire durante l'implementazione.

    public Stanza(int codice, String nome) {
        this.nome = nome;
        this.codice = codice;

    }
    public Stanza(int codice, String nome,Messaggio messaggio) {
        this.nome = nome;
        this.codice = codice;
        this.ultimo_msg = messaggio;

    }

    public Messaggio getUltimo_msg() {return ultimo_msg; }

    public String getNome() { return nome; }

    public int getCodice() {return codice;}

    public void setCapacita(int capacita) { this.capacita = capacita; }

    public int getCapacita() { return capacita; }

}
