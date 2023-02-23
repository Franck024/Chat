package com.example.lso_chat.Application.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lso_chat.Application.Controller.Controller;
import com.example.lso_chat.Application.Entities.Stanza;
import com.example.lso_chat.Application.ListaGruppiUtil.ListViewAdapter;
import com.example.lso_chat.R;

import java.util.ArrayList;

public class CercaGruppi extends AppCompatActivity {
    private Toolbar toolbar;
    ListView listView;
    ListViewAdapter adapter;
    Controller controller =Controller.getIstanza();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cerca_gruppi);
        controller.setCercaGruppi(this);
        controller.recuperaAllChat();
        toolbar=findViewById(R.id.myToolBar);

        //Animazione cambio gradiente sfondo.
        AnimationDrawable animDrawable= (AnimationDrawable) findViewById(R.id.layout_cercagruppi).getBackground();
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
            getSupportActionBar().setTitle("Esplora i gruppi");
        }

        toolbar.setTitle("Esplora i gruppi");
        toolbar.setSubtitle("Esplora e cerca i gruppi esistenti");



    }
    //Faccio l'override dell'azione dell tasto indietro
    @Override
    public boolean onSupportNavigateUp() {
        Log.d("ok","hai premuto dietro");
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem.OnActionExpandListener onActionExpandListener= new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                Toast.makeText(CercaGruppi.this, "Hai premuto su ricerca!", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                return true;
            }
        };
        menu.findItem(R.id.cercaGruppi).setOnActionExpandListener(onActionExpandListener);
        SearchView searchView=(SearchView) menu.findItem(R.id.cercaGruppi).getActionView();
        searchView.setQueryHint("Cerca tra i gruppi esistenti");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(CercaGruppi.this,"Stai cercando "+query,Toast.LENGTH_SHORT).show(); //https://www.youtube.com/watch?v=_EIYM-wwObI
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(TextUtils.isEmpty(newText)){
                    adapter.filtro("");
                    listView.clearTextFilter();
                }
                else{
                    adapter.filtro(newText);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void riempilista(ArrayList<Stanza> stanze){

        listView= findViewById(R.id.listview);
        //Passa I risultati alla classe listViewAdapter
        adapter = new ListViewAdapter(this,stanze);
        //Gli passo l'istanza dell'activity all'adapter
        adapter.setActivitycercagruppi(this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Binda l'adapter alla listview
                listView.setAdapter(adapter);
            }
        });


    }

    public void RecuperoChatFallito() {
        runOnUiThread(() -> {
            Toast.makeText(this, "L'utente non esiste", Toast.LENGTH_SHORT).show();
        });

    }

    public void copiasuclipboard(int codicegruppo, String nomegruppo){
        String TestoCodice= String.valueOf(codicegruppo);
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("codice", TestoCodice);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(CercaGruppi.this,"Ho copiato il codice del gruppo "+nomegruppo+" nella clipboard!", Toast.LENGTH_SHORT).show();
    }
}