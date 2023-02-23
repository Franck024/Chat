package com.example.lso_chat.Application.Controller;

import com.example.lso_chat.Application.Activity.*;
import com.example.lso_chat.Application.Client.ClientSocket;
import com.example.lso_chat.Application.Entities.Messaggio;
import com.example.lso_chat.Application.Entities.Richiesta;
import com.example.lso_chat.Application.Entities.Stanza;
import com.example.lso_chat.Application.Entities.Utente;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class Controller {
    private static Controller istanza = null;
    private final ClientSocket clientSocket;
    private ControllerLogin controllerlogin;
    private ControllerRegistrazione controllerRegistrazione;
    private ControllerListChat controllerListChat;
    private ControllerChat controllerChat;
    private CercaGruppi cercagruppi;
    private Utente utente;
    private ControllerCreazioneGruppo creazioneGruppo;
    private Richieste activityRichieste;




    private Controller(){
        clientSocket = ClientSocket.getIstanza();
        clientSocket.setSocket();
    }

    public static Controller getIstanza(){
        if(istanza == null){
            istanza = new Controller();
        }
        return istanza;
    }




    public void infoLogin(String username, String password){
        String messaggio = "login".concat(" ").concat(username).concat(" ").concat(password);
        this.clientSocket.mandaMessaggio(messaggio);
    }

    public void infoRegistrazione(String username, String password){
        String messaggio = "sign_in".concat(" ").concat(username).concat(" ").concat(password);
        this.clientSocket.mandaMessaggio(messaggio);

    }

    public void recuperaChat(){
        this.clientSocket.mandaMessaggio("recupera_chat");
    }

    public void recuperaAllChat(){
        this.clientSocket.mandaMessaggio("recupera_all_chat");
    }

    public void recuperaRichieste() {this.clientSocket.mandaMessaggio("recupera_richieste");}

    public void inviaRichiesta(String codice){
        String comando= "invia_richiesta".concat(" ").concat(codice);
        this.clientSocket.mandaMessaggio(comando);
        Log.d("ok","ho inviato "+comando);
    }

    public void recuperaMessaggi(String id_stanza) {
        String comando = "recupera_messaggi".concat(" ").concat(id_stanza);
        this.clientSocket.mandaMessaggio(comando);}

    public void controllaUtente(String username){
        String comando= "check_user".concat(" ").concat(username);
        this.clientSocket.mandaMessaggio(comando);
    }

    public void lasciaChat(){
        this.clientSocket.mandaMessaggio("leave_chat");
    }


    public void LoginOK(String username,String password){
        utente = new Utente(username,password);
        controllerlogin.startActivity(new Intent(controllerlogin, ControllerListChat.class));
    }

    public void LoginError(){
        controllerlogin.LoginError();

    }

    public void SignOK(){
        controllerlogin.startActivity(new Intent(controllerRegistrazione, ControllerLogin.class));
        controllerlogin.SignOK();
    }

    public void SignError(){
        controllerRegistrazione.SignError();
    }

    public void AlreadySigned(){
        controllerRegistrazione.AlreadySigned();
    }

    public void RecuperoUserChatFallito(){
        controllerListChat.RecuperoChatFallito();
    }

    public void RecuperoChatFallito(){
        cercagruppi.RecuperoChatFallito();
    }

    public void RecuperaChatOK(String[] stanze) {
        for (int i = 0; i < stanze.length; i++) {
            Log.d("ok","Stanze.length è "+String.valueOf(stanze.length));
            int codice = Integer.parseInt(stanze[i]);
            String nome = stanze[++i];
            String last_person = stanze[++i];
            String timestamp = stanze[++i];
            String corpo = stanze[++i];


            if(!last_person.contentEquals("null")) {
                LocalDateTime dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));


                Log.d("ok", "Timestamp: " + timestamp);
                Log.d("ok","Codice:"+String.valueOf(codice)+"Nome: "+nome+"last_person:"+last_person+"timestamp: "+timestamp+"corpo: "+corpo);
                Utente mittente = new Utente(last_person);
                Messaggio messaggio = new Messaggio(corpo,dateTime,mittente);
                Stanza st = new Stanza(codice, nome,messaggio);
                boolean ris=false;
                for(Stanza ns: utente.getStanze()){
                    if(ns.getCodice()==st.getCodice()){ris=true;}
                }
                if(ris==false){utente.addStanzaTop(st);}
            }
            else{
                Stanza st = new Stanza(codice,nome,null);
                boolean ris=false;
                for(Stanza ns: utente.getStanze()){
                    if(ns.getCodice()==st.getCodice()){ris=true;}
                }
                if(ris==false){utente.addStanza(st);}
            }


        }


        controllerListChat.aggiornalistastanze();

    }

    public void recuperaMessaggiOK(String[] stanze){
        ArrayList<Messaggio> lista = new ArrayList<>();
        for (int i = 0; i < stanze.length; i++) {
            String corpo = stanze[i];
            String tempo_invio = stanze[++i];
            String nome_mittente = stanze[++i];
            LocalDateTime dateTime = LocalDateTime.parse(tempo_invio, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
            Utente mittente = new Utente(nome_mittente);
            Messaggio messaggio = new Messaggio(corpo,dateTime,mittente);
            lista.add(messaggio);
        }

        //passa l'array all'activity
        controllerChat.setMessaggi(lista);
    }


    public void RecuperaTutteChatOK(String[] stanze) {
        ArrayList<Stanza> arraydistanze = new ArrayList<Stanza>();
        for (int i = 0; i < stanze.length; i++) {
            int codice = Integer.parseInt(stanze[i]);
            String nome = stanze[++i];
            arraydistanze.add(new Stanza(codice,nome));
        }
        cercagruppi.riempilista(arraydistanze);
    }

    public void recuperaRichiesteOK(String[] richieste){
        List<Richiesta> list_richieste = new ArrayList<>();
        for (int i = 0; i < richieste.length; i++) {
            String nome_utente = richieste[i];
            String nome_stanza = richieste[++i];
            int codice = Integer.parseInt(richieste[++i]);
            list_richieste.add(new Richiesta(codice,nome_utente,nome_stanza));
        }
        activityRichieste.riempilista(list_richieste);
    }

    public void aggiungiUtente(String username, int idstanza){
        String comando="add_user_to_chat".concat(" ").concat(String.valueOf(idstanza)).concat(" ").concat(username);
        this.clientSocket.mandaMessaggio(comando);
        Log.d("ok","Invio il comando:"+comando);
    }

    public void rifiutaUtente(String username, int idstanza){
        String comando="delete_request_user".concat(" ").concat(String.valueOf(idstanza)).concat(" ").concat(username);
        this.clientSocket.mandaMessaggio(comando);
    }

    public void entraInChat(){
        controllerListChat.startActivity(new Intent(controllerListChat, ControllerChat.class));
    }

    public void utenteAggiunto(){
        activityRichieste.utenteAggiunto();
    }
    public void utenteRifiutato(){
        activityRichieste.utenteRifiutato();
    }
    public void utenteEsiste(String username){
        creazioneGruppo.addUser(username);
    }
    public void utenteNonEsiste(){
        creazioneGruppo.userNotExists();
    }

    public void creaGruppo(String nomegruppo, ArrayList<String> lista){
        String comando = "create_chat_add_users";
        comando = comando.concat(" ").concat(nomegruppo).concat(" ").concat("¶");
        for(String stringa : lista){
            comando=comando.concat(" ").concat(stringa);
        }
        Log.d("ok","al server sto mandando:"+comando);
        this.clientSocket.mandaMessaggio(comando);
    }

    public void creazioneGruppoOk(){
        creazioneGruppo.creazioneGruppoOk();
    }

    public void erroreCreazioneGruppo(){
        creazioneGruppo.creazioneGruppoNonOk();
    }

    public void ErroreRichieste(){
        activityRichieste.Errore();
    }

    public void RichiestaInviata(){
        controllerListChat.RichiestaInviata();
    }

    public void RichiestaNonInviata(){
        controllerListChat.RichiestaFallita();
    }

    public void RichiestaInAttesa(){
        controllerListChat.RichiestaInAttesa();
    }

    public void RichiestaAccettata(){
        controllerListChat.RichiestaAccettata();
    }

    public Utente getUtente(){return utente;}









    public synchronized void onSocketError(int tentativi_max){ //ignorate per il momento
        int n_tentativo = 0;
        while(n_tentativo < tentativi_max){
            n_tentativo++;
            try{
                Thread.sleep(2000);
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
            clientSocket.setSocket();
            if(clientSocket.isConnected())
                return;
        }
        controllerlogin.startActivity(new Intent(controllerRegistrazione, ControllerLogin.class));
        controllerlogin.GenericError();
        //Remind me metodo logout
        utente = null;
    }

    public void setLoginActivity(ControllerLogin clogin){this.controllerlogin = clogin;}
    public void setRegistrazioneActivity(ControllerRegistrazione registrazione){this.controllerRegistrazione = registrazione;}
    public void setControllerListChat(ControllerListChat clistChat){this.controllerListChat = clistChat;}
    public void setCercaGruppi(CercaGruppi cg){this.cercagruppi=cg;}
    public void setControllerChat(ControllerChat cChat){this.controllerChat = cChat;}
    public void setCreazioneGruppo(ControllerCreazioneGruppo creagruppo){this.creazioneGruppo = creagruppo;}
    public void setActivityRichieste(Richieste activityRichieste){this.activityRichieste = activityRichieste;}



}
