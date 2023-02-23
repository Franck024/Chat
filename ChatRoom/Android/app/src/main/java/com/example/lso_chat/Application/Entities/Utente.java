package com.example.lso_chat.Application.Entities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;


public class Utente {

    private String username;
    private String password;
    private ArrayList<Stanza> stanze;


    public Utente(String username,String password) throws IllegalArgumentException{
        this.username = username;
        this.password = password;
        stanze = new ArrayList<Stanza>();
    }

    public Utente(String username) {
        this.username = username;
    }

    public String getusername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public ArrayList<Stanza> getStanze(){
        return stanze;
    }

    public void addStanza(Stanza stanza){
        stanze.add(stanza); }
    public void addStanzaTop(Stanza stanza){
        stanze.add(0,stanza); }
    public void setUsername(String usr){ this.username = usr; }


}
