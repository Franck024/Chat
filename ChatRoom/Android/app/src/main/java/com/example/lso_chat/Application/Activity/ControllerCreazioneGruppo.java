package com.example.lso_chat.Application.Activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lso_chat.Application.Controller.Controller;
import com.example.lso_chat.Application.chat.views.AdapterListNewGroup;
import com.example.lso_chat.R;

import java.util.ArrayList;

public class ControllerCreazioneGruppo extends AppCompatActivity {
    private EditText editTextNuovoUtente, editTextNomeNewGroup;
    private ImageView createNewGroup;
    private Button btnAggiungiUtente;
    private ImageView backNewGroup;
    private RecyclerView RVlistUserGroup;
    private ArrayList<String> listaUtenti;
    private Controller controller = Controller.getIstanza();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creazionegruppo);
        //Animazione cambio gradiente sfondo.
        AnimationDrawable animDrawable= (AnimationDrawable) findViewById(R.id.layout_creazionegruppo).getBackground();
        animDrawable.setEnterFadeDuration(10);
        animDrawable.setExitFadeDuration(5000);
        animDrawable.start();
        //Fine animazione cambio gradiente sfondo.
        controller.setCreazioneGruppo(this);
        listaUtenti = new ArrayList<>();
        init();
        setListeners();
    }

    void init(){
        editTextNuovoUtente = findViewById(R.id.editTextNuovoUtente);
        editTextNomeNewGroup = findViewById(R.id.editTextNomeNewGroup);
        btnAggiungiUtente = findViewById(R.id.btnAggiungiUtente);
        createNewGroup = findViewById(R.id.createNewGroup);
        backNewGroup = findViewById(R.id.backNewGroup);
        RVlistUserGroup = findViewById(R.id.RVlistUserGroup);
    }
    private void setListeners(){
        backNewGroup.setOnClickListener(v -> finish());
        btnAggiungiUtente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(editTextNuovoUtente.getText())){
                    if(listaUtenti.size()>30){
                        Toast.makeText(ControllerCreazioneGruppo.this, "Puoi inserire massimo 30 utenti!", Toast.LENGTH_SHORT).show();
                    }
                    else if(listaUtenti.contains(editTextNuovoUtente.getText().toString())){
                            Toast.makeText(ControllerCreazioneGruppo.this, "L'utente è già presente nella lista!", Toast.LENGTH_SHORT).show();
                        }
                        else if(editTextNuovoUtente.getText().toString().equals(controller.getUtente().getusername()))
                        {
                            Toast.makeText(ControllerCreazioneGruppo.this, "Non puoi aggiungere te stesso al gruppo!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            controller.controllaUtente(editTextNuovoUtente.getText().toString());
                        }
                    }
                else
                    errore(1);
            }
        });
        createNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(editTextNomeNewGroup.getText()) && !listaUtenti.isEmpty()){
                    controller.creaGruppo(editTextNomeNewGroup.getText().toString().trim(), listaUtenti);
                }else
                {errore(0);}
            }
        });
    }

    void errore(int i){
        if(i==1) {
            runOnUiThread(() -> {
            Toast.makeText(this, "Campo utente vuoto!!", Toast.LENGTH_SHORT).show();
            });
        }
        else {
            runOnUiThread(() -> {
            Toast.makeText(this, "Campo nome gruppo vuoto, o nessun utente aggiunto!", Toast.LENGTH_SHORT).show();
            });
        }
    }
    void aggiornaLista(){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                AdapterListNewGroup myAdapter = new AdapterListNewGroup(getApplicationContext(), listaUtenti);
                RVlistUserGroup.setAdapter(myAdapter);
                RVlistUserGroup.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                //Implemento la rimozione dell'elemento dalla lista.
                myAdapter.setOnItemClickListener(new AdapterListNewGroup.OnItemClickListener() {
                    @Override
                    public void onItemCLick(int position) {
                        listaUtenti.remove(position);
                        myAdapter.notifyItemRemoved(position);
                    }
                });
            }
        });
    }

    public void addUser(String username){
        Log.d("ok","sono in adduser con username "+username);
    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            listaUtenti.add(username);
            editTextNuovoUtente.setText("");
            aggiornaLista();
        }
    });

    }

    public void userNotExists(){
        runOnUiThread(() -> {
        Toast.makeText(this, "Utente inesistente!", Toast.LENGTH_SHORT).show();
        });
    }

    public void creazioneGruppoOk(){
        runOnUiThread(() -> {
        Toast.makeText(this, "Gruppo creato!", Toast.LENGTH_SHORT).show();
        finish();
        });
    }

    public void creazioneGruppoNonOk(){
        runOnUiThread(() -> {
        Toast.makeText(this, "Errore nella creazione del gruppo!", Toast.LENGTH_SHORT).show();
        });
    }

}
