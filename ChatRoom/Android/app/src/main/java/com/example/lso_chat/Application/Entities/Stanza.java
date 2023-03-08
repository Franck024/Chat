package com.example.lso_chat.Application.Entities;



public class Stanza{

    private final String nome;
    private final int codice;
    private Messaggio ultimo_msg;


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

    public void setUltimo_msg(Messaggio msg){ultimo_msg=msg;}

    public String getNome() { return nome; }

    public int getCodice() {return codice;}


}
