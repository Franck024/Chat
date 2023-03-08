package com.example.lso_chat.Application.Activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.lso_chat.Application.Controller.Controller;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lso_chat.Application.Controller.Controller;
import com.example.lso_chat.Application.Entities.Messaggio;
import com.example.lso_chat.Application.Entities.Utente;
import com.example.lso_chat.Application.chat.views.AdapterMessaggio;
import com.example.lso_chat.R;
import com.example.lso_chat.databinding.ActivityChatBinding;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ControllerChat extends AppCompatActivity {
    private TextView textViewChatName;
    private ImageView imageViewBack, imageViewSend, imageViewInfoChat;
    private EditText editTextNewMessaggio;
    private RecyclerView recyclerViewMessaggio;
    private ProgressBar progressBar;
    private Utente receiverUser;
    private ArrayList<Messaggio> chatMessages;
    private AdapterMessaggio chatAdapter;
    private ActivityChatBinding binding;
    private LinearLayoutManager linearLayoutManager;
    Controller controller = Controller.getIstanza();

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //Animazione cambio gradiente sfondo.
        AnimationDrawable animDrawable= (AnimationDrawable) findViewById(R.id.layout_chat).getBackground();
        animDrawable.setEnterFadeDuration(10);
        animDrawable.setExitFadeDuration(5000);
        animDrawable.start();
        //Fine animazione cambio gradiente sfondo.
        controller.setControllerChat(this);



        setObj();
        loadReceiverDetails();
        setListeners();
        init();
        setNomeStanza();
        chiamarecuperamessaggi(); //Qui sto recuperando la cronologia dei messaggi.
    }

    /////////////
    private void loadReceiverDetails(){
        receiverUser = Controller.getIstanza().getUtente();
        //textViewChatName.setText(Controller.getIstanza().get);  // non funziona. Problemi di comunicazione con il Controller
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init(){

        progressBar.setVisibility(View.GONE);
        recyclerViewMessaggio.setVisibility(View.VISIBLE);
        chatMessages = new ArrayList<>();
        /* Recuperare la lista dei Messaggi dal db */

        //chatMessages.add(new Messaggio("test1", LocalTime.of(10,43,12) , new Utente(receiverUser.getusername(),"B")));  // <--- prova
        //chatMessages.add(new Messaggio("test2", LocalTime.of(10,43,12), new Utente("A","A")));  // <--- prova

        // passare dati

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendMessage(){
/// 7:11
            if(!TextUtils.isEmpty(editTextNewMessaggio.getText())){
                try{
                    //imageViewSend.animate().rotation(360).start();
                    controller.inviaMessaggio(editTextNewMessaggio.getText().toString().trim().replace("¶","q"));
                    editTextNewMessaggio.setText(null);
                }
                catch (Exception e){
                    Toast.makeText(this, "Impossibile mandare il messaggio!", Toast.LENGTH_SHORT).show();
                }
            }
    }
    private String getDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
   //IMPLEMENTARE METODO PER STAMPARE L ORA

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setListeners(){
        imageViewSend.setOnClickListener(v -> sendMessage());
        imageViewBack.setOnClickListener(v -> onBackPressed());
        imageViewInfoChat.setOnClickListener(v -> startActivity(new Intent(this,PopupInfoGruppo.class)));
    }
    private void setObj(){
        textViewChatName = findViewById(R.id.textViewChatName);
        imageViewBack = findViewById(R.id.imageViewBack);
        editTextNewMessaggio = findViewById(R.id.editTextNewMessaggio);
        imageViewSend = findViewById(R.id.imageViewSend);
        progressBar = findViewById(R.id.progressBar);
        recyclerViewMessaggio = findViewById(R.id.recyclerViewMessaggio);
        imageViewInfoChat = findViewById(R.id.imageViewInfoChat);
    }

    public void chiamarecuperamessaggi(){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("key");
            //The key argument here must match that used in the other activity
            controller.recuperaMessaggi(value);
        }
    }

    public void setNomeStanza(){
        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            String nomestanza= extras.getString("nomestanza");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textViewChatName.setText(nomestanza);
                }
            });
        }
    }
    @Override
    public void onBackPressed() {
        controller.lasciaChat();
        Log.d("ok","sono tornato indietro da una chat");
        super.onBackPressed();
    }

    public void erroreInvio(){
        runOnUiThread(()->{
            Toast.makeText(this,"Impossibile inviare messaggio",Toast.LENGTH_SHORT).show();
        });
    }


    public void setMessaggi(ArrayList<Messaggio> listamessaggi){
        chatMessages= listamessaggi;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatAdapter = new AdapterMessaggio(  chatMessages, receiverUser.getusername());  // <---- test    il sec param è la persona loggata. Usare receiverUser quando funzionante
                recyclerViewMessaggio.setAdapter(chatAdapter);
            }
        });



    }

    public void messaggioricevuto(Messaggio mess){ //CHIAMARE QUESTO QUANDO RICEVO UN MESSAGGIO DAL SERVER!! IMPLEMENTARE RICEZIONE MESSAGGIO DA SERVER
        chatMessages.add(mess);
        Log.d("ok","sono in messaggioricevuto, prima del runonui");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("ok","sono in messaggioricevuto");
                chatAdapter = new AdapterMessaggio(  chatMessages, receiverUser.getusername());  // <---- test    il sec param è la persona loggata. Usare receiverUser quando funzionante
                recyclerViewMessaggio.setAdapter(chatAdapter);
            }
        });

    }


}
