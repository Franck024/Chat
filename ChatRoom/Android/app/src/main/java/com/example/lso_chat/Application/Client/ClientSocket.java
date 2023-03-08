package com.example.lso_chat.Application.Client;

import android.service.controls.Control;
import android.util.Log;

import com.example.lso_chat.Application.Controller.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;


public class ClientSocket {
    private static ClientSocket istanza;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private ClientSocket(){}

    public static ClientSocket getIstanza(){
        if(istanza == null){
            istanza = new ClientSocket();
        }
        return istanza;
    }

    public void setSocket(){
        if(isConnected())
            return;
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    socket = new Socket("progettolso.ddns.net",17000);
                    socket.setKeepAlive(true);
                    out = new PrintWriter(socket.getOutputStream(),true);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while(isConnected()){
                                try {
                                    final String messaggio = in.readLine();
                                    if(messaggio == null){
                                        closeConnection();
                                        Controller.getIstanza().onSocketError(5);
                                        break;
                                    }else{
                                        gestisciMessaggio(messaggio);
                                    }
                                } catch (IOException e) {
                                    closeConnection();
                                    Controller.getIstanza().onSocketError(5);
                                    break;


                                }
                            }
                        }
                    }).start();

                }catch(IOException e){
                    if(socket != null){
                        closeConnection();
                    }
                }

            }
        }).start();
    }

    public boolean isConnected(){
        return socket != null && socket.isConnected();
    }

    public void closeConnection() {
        try {
            socket.close();
            in.close();
            out.close();
            socket = null;
            in = null;
            out = null;
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void mandaMessaggio(String messaggio){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(isConnected()){
                    out.write(messaggio);
                    out.flush();
                }else{
                    Controller.getIstanza().onSocketError(5);

                }
            }
        }).start();
    }


    public void gestisciMessaggio(String messaggio){

        String[] risposte_server = messaggio.split("¶");
        Log.d("ok","il messaggio ricevuto è:"+messaggio);
        String codice_risposta = risposte_server[0];
        switch (codice_risposta){
            case "login_success":
                String username = risposte_server[1];
                String password = risposte_server[2];
                Controller.getIstanza().LoginOK(username,password);
                break;
            case "login_failed":
                Controller.getIstanza().LoginError();
                break;
            case "sign_in_success":
                Controller.getIstanza().SignOK();
                break;
            case "sign_in_failed":
                Controller.getIstanza().SignError();
                break;
            case "already_exists":
                Controller.getIstanza().AlreadySigned();
                break;
            case "user_chat_retrieval_success":
                String[] stanze = Arrays.copyOfRange(risposte_server, 1, risposte_server.length);
                Controller.getIstanza().RecuperaChatOK(stanze);
                break;
            case "failed_user_chat_retrieval":
            case "retrieval_message_failed":
                Controller.getIstanza().RecuperoUserChatFallito();
                break;
            case "chat_retrieval_success":
                String[] tuttestanze = Arrays.copyOfRange(risposte_server, 1, risposte_server.length);
                Controller.getIstanza().RecuperaTutteChatOK(tuttestanze);
                break;
            case "failed_chat_retrieval":
                Controller.getIstanza().RecuperoChatFallito();
                break;
            case "create_chat_add_users_success":
                Controller.getIstanza().creazioneGruppoOk();
                break;
            case "create_chat_add_users_failed":
                Controller.getIstanza().erroreCreazioneGruppo();
                break;
            case "user_found":
                String user = risposte_server[1];
                Log.d("ok","sono arrivato a user_found");
                Log.d("ok", "in user_found ci sta l'utente:"+user);
                Controller.getIstanza().utenteEsiste(user);
                break;
            case "user_not_found":
                Controller.getIstanza().utenteNonEsiste();
                break;
            case "retrival_request_success":
                String[] richieste = Arrays.copyOfRange(risposte_server, 1, risposte_server.length);
                Controller.getIstanza().recuperaRichiesteOK(richieste);
                break;
            case "retrieval_request_failed":
            case "add_user_failed":
            case "delete_request_failed":
                Controller.getIstanza().ErroreRichieste();
                break;
            case "processing_request":
                Controller.getIstanza().RichiestaInAttesa();
                break;
            case "request_sent":
                Controller.getIstanza().RichiestaInviata();
                break;
            case "request_not_sent":
                Controller.getIstanza().RichiestaNonInviata();
                break;
            case "user_already_in":
                Controller.getIstanza().RichiestaAccettata();
                break;
            case "user_added_to_chat":
                Controller.getIstanza().utenteAggiunto();
                break;
            case "delete_request_user_success":
                Controller.getIstanza().utenteRifiutato();
                break;
            case "retrieval_message_success":
                String[] messaggi = Arrays.copyOfRange(risposte_server, 1, risposte_server.length);
                Controller.getIstanza().recuperaMessaggiOK(messaggi);
                break;
            case "send_message_success":
                String corpo = risposte_server[1];
                Log.d("ok","Sono in send_message_success, con corpo "+corpo);
                Controller.getIstanza().messaggioInviato(corpo);
                break;
            case "send_message_failed":
                Controller.getIstanza().messaggioNonInviato();
                break;
            case "received_message":
                String nome_mittente = risposte_server[1];
                String msg_ricevuto = risposte_server[2];
                Controller.getIstanza().messaggioRicevuto(nome_mittente,msg_ricevuto);
                break;
            case "retrieval_users_success":
                String[] partecipanti = Arrays.copyOfRange(risposte_server, 1, risposte_server.length);
                Controller.getIstanza().recuperoPartecipantiOK(partecipanti);
                break;
            default:
                break;

        }


    }






}