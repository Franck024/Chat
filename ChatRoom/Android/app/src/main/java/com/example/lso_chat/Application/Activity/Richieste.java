package com.example.lso_chat.Application.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lso_chat.Application.Controller.Controller;
import com.example.lso_chat.Application.Entities.Richiesta;
import com.example.lso_chat.Application.RequestListUtil.ListViewAdapter;
import com.example.lso_chat.R;

import java.util.*;

public class Richieste extends AppCompatActivity {
    Toolbar toolbar;
    String[] titolo;
    String[] username;
    int[] codice;
    ListView listView;
    ListViewAdapter adapter;
    Controller controller = Controller.getIstanza();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_richieste);
        toolbar=findViewById(R.id.myToolBar);
        controller.setActivityRichieste(this);
        //Animazione cambio gradiente sfondo.
        AnimationDrawable animDrawable= (AnimationDrawable) findViewById(R.id.layout_richieste).getBackground();
        animDrawable.setEnterFadeDuration(10);
        animDrawable.setExitFadeDuration(5000);
        animDrawable.start();
        //Fine animazione cambio gradiente sfondo.

        setSupportActionBar(toolbar);
        ActionBar actionBar= getSupportActionBar();
        if(getSupportActionBar()!=null)
        {
            //Mostra il tasto indietro
            actionBar.setDisplayHomeAsUpEnabled(true);
            //Setto il titolo personalizzato https://youtu.be/o35gogTi8lY
            getSupportActionBar().setTitle("Richieste di partecipazione");
        }
        toolbar.setTitle("Richieste di partezipazione");
        toolbar.setSubtitle("Accetta o rifiuta richieste.");
        controller.recuperaRichieste();
    }

    public void riempilista(List<Richiesta> richieste){
                listView= findViewById(R.id.listviewrichieste);
                //Passa I risultati alla classe listViewAdapter
                adapter = new com.example.lso_chat.Application.RequestListUtil.ListViewAdapter(this,richieste);
                //Gli passo l'istanza dell'activity all'adapter
                adapter.setActivityrichieste(this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(adapter);
                    }
                });
                //Binda l'adapter alla listview

    }

    public void utenteAggiunto(){
        controller.recuperaRichieste();
        runOnUiThread(() -> {
            Toast.makeText(this, "Utente aggiunto", Toast.LENGTH_SHORT).show();
        });
    }

    public void utenteRifiutato(){
        controller.recuperaRichieste();
        runOnUiThread(() -> {
            Toast.makeText(this, "Richiesta rifiutata", Toast.LENGTH_SHORT).show();
        });
    }

    public void Errore(){
        runOnUiThread(() -> {
            Toast.makeText(this, "Errore Network", Toast.LENGTH_SHORT).show();
        });
    }

    //Faccio l'override dell'azione dell tasto indietro
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }



}