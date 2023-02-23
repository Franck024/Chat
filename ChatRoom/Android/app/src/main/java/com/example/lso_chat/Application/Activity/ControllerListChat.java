package com.example.lso_chat.Application.Activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lso_chat.Application.Controller.Controller;
import com.example.lso_chat.Application.Entities.Stanza;
import com.example.lso_chat.Application.Entities.Utente;
import com.example.lso_chat.Application.chat.views.AdapterChat;
import com.example.lso_chat.R;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ControllerListChat extends AppCompatActivity {
    private ImageView back;
    private ImageView nuovoGruppo;
    private ImageView CercaGruppi;
    private ImageView RequestButton;
    private RecyclerView lista_gruppi;
    Controller controller = Controller.getIstanza();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listachat);
        //Animazione cambio gradiente sfondo.
        AnimationDrawable animDrawable= (AnimationDrawable) findViewById(R.id.layout_listachat).getBackground();
        animDrawable.setEnterFadeDuration(10);
        animDrawable.setExitFadeDuration(5000);
        animDrawable.start();
        //Fine animazione cambio gradiente sfondo.
        back = findViewById(R.id.backLogin);
        nuovoGruppo = findViewById(R.id.createGroup);
        CercaGruppi = findViewById(R.id.searchGroup);
        RequestButton= findViewById(R.id.requestlist);
        lista_gruppi = findViewById(R.id.RVchat);
        controller.setControllerListChat(this);


        back.setOnClickListener(view -> finish());
        nuovoGruppo.setOnClickListener(
                view -> startActivity(new Intent(ControllerListChat.this, PopupNuovogruppo.class)));
        CercaGruppi.setOnClickListener(view -> startActivity(new Intent(ControllerListChat.this,CercaGruppi.class)));
        RequestButton.setOnClickListener(view -> startActivity(new Intent(ControllerListChat.this,Richieste.class)));
        //aggiornalistastanze();  // <---------- TEST, commentare se si vuole usare il server
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d("ok","sono nell'onstart");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                controller.recuperaChat();
            }
        });

    }



    public void RecuperoChatFallito() {
        runOnUiThread(() -> {
            Toast.makeText(this, "Errore network", Toast.LENGTH_SHORT).show();
        });

    }

    public void RichiestaInviata(){
        runOnUiThread(() -> {
            Toast.makeText(this, "Richiesta inviata!", Toast.LENGTH_SHORT).show();
        });

    }

    public void RichiestaFallita(){
        runOnUiThread(() -> {
            Toast.makeText(this, "Richiesta fallita!", Toast.LENGTH_SHORT).show();
        });

    }

    public void RichiestaInAttesa(){
        runOnUiThread(() -> {
            Toast.makeText(this, "Hai già inviato una richiesta per questo gruppo!", Toast.LENGTH_SHORT).show();
        });

    }

    public void RichiestaAccettata(){
        runOnUiThread(() -> {
            Toast.makeText(this, "Già fai parte del gruppo!", Toast.LENGTH_SHORT).show();
        });

    }

    public void aggiornalistastanze() {
            // Creazione della lista dei gruppi
            AdapterChat myAdapterChat = new AdapterChat(this);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    lista_gruppi.setAdapter(myAdapterChat);
                    lista_gruppi.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }
            });

        }

    }

